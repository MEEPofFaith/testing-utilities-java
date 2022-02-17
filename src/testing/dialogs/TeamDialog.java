package testing.dialogs;

import arc.graphics.*;
import arc.math.*;
import arc.scene.*;
import arc.scene.event.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.dialogs.*;
import testing.ui.*;
import testing.util.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class TeamDialog extends BaseDialog{
    Team spawnTeam = Team.sharded;

    Table all = new Table();

    public TeamDialog(){
        super("@tu-unit-menu.team");
        spawnTeam = Team.get(settings.getInt("tu-default-team", 1));

        shouldPause = false;
        addCloseButton();
        shown(this::rebuild);
        onResize(this::rebuild);

        all.margin(20).marginTop(0f);

        cont.pane(all);
    }

    void rebuild(){
        all.clear();

        all.table(t -> {
            t.add("@tu-unit-menu.teams-id").right().color(Pal.accent);

            TextField tField = TUElements.textField(
                String.valueOf(spawnTeam.id),
                field -> {
                    if(field.isValid()){
                        String team = Utils.extractNumber(field.getText());
                        if(!team.isEmpty()){
                            if(!state.isGame()) settings.put("tu-default-team", Integer.parseInt(team));
                            spawnTeam = Team.get(Integer.parseInt(team));
                        }
                    }
                },
                () -> String.valueOf(getTeam().id)
            );
            t.add(tField).left().padLeft(6);
        }).left().padBottom(6);
        all.row();

        all.add("@tu-unit-menu.teams-main").growX().left().color(Pal.accent);
        all.row();
        all.image().growX().pad(5).padLeft(0).padRight(0).height(3).color(Pal.accent);
        all.row();
        all.table(t -> {
            float iconMul = 2f;
            int cols = (int)Mathf.clamp((graphics.getWidth() - Scl.scl(30)) / Scl.scl(32 + 10) / iconMul, 1, 6);
            int count = 0;
            for(Team team : Team.baseTeams){
                addButton(t, team, 8 * 4 * iconMul);
                if((++count) % cols == 0){
                    t.row();
                }
            }
        });
        all.row();

        all.add("@tu-unit-menu.teams-other").growX().left().color(Pal.accent);
        all.row();
        all.image().growX().pad(5).padLeft(0).padRight(0).height(3).color(Pal.accent);
        all.row();
        all.table(t -> {
            float iconMul = 1.5f;
            int cols = (int)Mathf.clamp((graphics.getWidth() - Scl.scl(30)) / Scl.scl(32 + 10) / iconMul, 1, 22 / iconMul);
            int count = 0;
            for(int i = 6; i < Team.all.length; i++){
                addButton(t, Team.get(i), 8 * 4 * iconMul);
                if((++count) % cols == 0){
                    t.row();
                }
            }
        });
    }

    void addButton(Table t, Team team, float size){
        Image image = t.image().size(size).pad(3).color(team.color).get();

        ClickListener listener = new ClickListener();
        image.addListener(listener);
        if(!mobile){
            image.addListener(new HandCursorListener());
            Color lerpColor = team.color.cpy().lerp(Color.white, 0.5f);
            image.update(() -> image.color.lerp(!listener.isOver() ? team.color : lerpColor, Mathf.clamp(0.4f * Time.delta)));
        }

        image.clicked(() -> {
            if(!state.isGame()) settings.put("tu-default-team", team.id);
            spawnTeam = team;
            hide();
        });
        image.addListener(new Tooltip(tp ->
            tp.background(Tex.button)
            .label(() -> teamName(team))
        ));
    }

    public Team getTeam(){
        return state.isGame() ? spawnTeam : Team.get(settings.getInt("tu-default-team", 1));
    }

    public String teamName(Team team){
        return bundle.has("team." + team.name + ".name") ? bundle.get("team." + team.name + ".name") : String.valueOf(team.id);
    }

    public String teamName(){
        return teamName(getTeam());
    }
}
