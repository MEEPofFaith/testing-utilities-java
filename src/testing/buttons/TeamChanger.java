package testing.buttons;

import arc.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.game.EventType.*;
import mindustry.game.*;
import mindustry.gen.*;
import testing.ui.*;
import testing.util.*;

import java.util.*;

import static mindustry.Vars.*;

public class TeamChanger{
    public static int mode;
    static Integer[] mainTeams = {0, 1, 2}; //Derelict, Sharded, Crux

    public static void changeTeam(){
        if(Utils.noCheat()){
            if(Vars.net.client()){
                Utils.runCommandPlayer("p.team(Team." + TUVars.curTeam.name + ");");
            }else{
                player.team(TUVars.curTeam);
            }
        }
    }

    public static Cell<Button> addSingle(Table t, Team team, int setMode){
        return t.button(b -> {
            b.setDisabled(() -> state.isCampaign() || player.unit().type == UnitTypes.block);

            String s = team.name;
            String name = s.substring(0, 1).toUpperCase() + (!mobile ? s.substring(1) : "");
            b.label(() -> "[#" + team.color + "]" + name);
        }, TUStyles.redButtonStyle, () -> {
            TUVars.curTeam = team;
            mode = setMode;
            changeTeam();
        }).size(40).color(team.color);
    }

    public static Cell<Button> addMini(Table t, Integer[] teams){
        return t.button(b -> {
            b.setDisabled(() -> state.isCampaign() || player.unit().type == UnitTypes.block);
            b.update(() -> b.setColor(Team.baseTeams[mode].color));

            b.label(() -> {
                String s = Team.baseTeams[mode].name;
                s = s.substring(0, 1).toUpperCase() + (!mobile ? s.substring(1) : "");
                return "[#" + Team.baseTeams[mode].color + "]" + s;
            });
        }, TUStyles.redButtonStyle, () -> {
            do{
                mode = (mode + 1) % 5;
            }while(!Structs.contains(teams, mode));
            TUVars.curTeam = Team.baseTeams[mode];
            changeTeam();
        }).size(40).color(Team.baseTeams[mode].color);
    }

    public static void add(Table[] tables){
        tables[0].table(Tex.pane, t -> {
            float[] widths = {100, 100, 60, 68, 70, 60};
            if(mobile) Arrays.fill(widths, 24);
            for(int i = 0; i < 6; i++){
                addSingle(t, Team.baseTeams[i], i).width(widths[i]);
            }
        });

        tables[1].table(Tex.pane, t -> {
            addMini(t, mainTeams).width(mobile ? 24 : 100);
        }).padBottom(mobile ? TUVars.buttonHeight : 0);

        Events.on(WorldLoadEvent.class, e -> {
            for(int i = 0; i < 6; i++){
                if(Structs.contains(mainTeams, i) && player.team() == Team.baseTeams[i]){
                    mode = i;
                    break;
                }
            }
        });
    }
}