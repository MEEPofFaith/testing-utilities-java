package testing.buttons;

import arc.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.ui.*;
import testing.ui.*;
import testing.util.*;

import static mindustry.Vars.*;
import static testing.ui.TUDialogs.*;

public class TeamChanger{
    public static void changeTeam(Team team){
        if(net.client()){ //For 2r2t
            Utils.runCommand("team @", team.id);
        }else{
            if(Core.input.shift()){
                Utils.copyJS("Vars.player.team(Team.get(@));", team.id);
                return;
            }

            player.team(team);
        }
    }

    public static Cell<Table> teamChanger(Table t){
        return t.table(teams -> {
            int i = 0;
            for(Team team : Team.baseTeams){
                ImageButton button = new ImageButton(Tex.whiteui, Styles.clearNoneTogglei);
                TUElements.boxTooltip(button, "@tu-tooltip.button-team");
                button.clicked(() -> {
                    if(TUVars.pressTimer > TUVars.longPress) return;
                    changeTeam(team);
                });
                button.getImageCell().grow().scaling(Scaling.stretch).center().pad(0).margin(0);
                button.getStyle().imageUpColor = team.color;
                button.update(() -> {
                    if(button.isPressed()){
                        TUVars.pressTimer += Time.delta;
                        if(TUVars.pressTimer >= TUVars.longPress && !teamDialog.isShown()){
                            teamDialog.show(curTeam(), TeamChanger::changeTeam);
                        }
                    }

                    button.setChecked(player.team() == team);
                });
                button.released(() -> TUVars.pressTimer = 0);

                teams.add(button).grow().margin(6f).center();
                if(++i % 3 == 0){
                    teams.row();
                }
            }
        });
    }

    public static Team curTeam(){
        return player.team();
    }

    public static Team getNextTeam(){
        if(player.team() == state.rules.defaultTeam){
            return state.rules.waveTeam;
        }else{
            return state.rules.defaultTeam;
        }
    }

    public static void addButton(Table t){
        teamChanger(t).width(100);
    }

    static String teamName(){
        String t = teamDialog.teamName(curTeam());
        return "[#" + curTeam().color.toString() + "]" + t.substring(0, 1).toUpperCase() + t.substring(1);
    }
}
