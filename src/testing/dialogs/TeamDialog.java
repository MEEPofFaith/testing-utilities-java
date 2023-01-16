package testing.dialogs;

import arc.func.*;
import arc.graphics.*;
import arc.math.*;
import arc.scene.event.*;
import arc.scene.ui.*;
import arc.scene.ui.TextField.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.game.*;
import mindustry.graphics.*;
import mindustry.ui.dialogs.*;
import testing.ui.*;
import testing.util.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class TeamDialog extends TUBaseDialog{
    Team curTeam;
    Cons<Team> changed;

    Table all = new Table();

    public TeamDialog(){
        super("@tu-unit-menu.team");

        shouldPause = false;
        addCloseButton();
        shown(this::rebuild);
        onResize(this::rebuild);

        all.margin(20).marginTop(0f);

        cont.pane(all);
    }

    public void show(Team startTeam, Cons<Team> changed){
        curTeam = startTeam;
        this.changed = changed;
        show();
    }

    void rebuild(){
        all.clear();

        all.table(t -> {
            t.add("@tu-unit-menu.teams-id").right().color(Pal.accent);

            TextField tField = TUElements.textField(
                String.valueOf(curTeam.id),
                text -> {
                    Team team = Team.get(Strings.parseInt(text));
                    changed.get(team);
                    curTeam = team;
                },
                () -> String.valueOf(curTeam.id),
                TextFieldFilter.digitsOnly,
                Strings::canParsePositiveInt
            );
            t.add(tField).left().padLeft(6).width(TUVars.fieldWidth);
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
            changed.get(team);
            hide();
        });
        TUElements.boxTooltip(image, () -> teamName(team));
    }

    public String teamName(Team team){
        return bundle.has("team." + team.name + ".name") ? bundle.get("team." + team.name + ".name") : String.valueOf(team.id);
    }
}
