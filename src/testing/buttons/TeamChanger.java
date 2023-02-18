package testing.buttons;

import arc.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.game.*;
import mindustry.ui.*;
import testing.ui.*;
import testing.util.*;

import static mindustry.Vars.*;
import static testing.ui.TUDialogs.*;

public class TeamChanger{
    static float tTimer;

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

    public static Cell<Button> teamChanger(Table t){
        Cell<Button> i = t.button(b -> {
            TUElements.boxTooltip(b, "@tu-tooltip.button-team");
            b.setDisabled(() -> player.unit().type.internal);
            b.label(TeamChanger::teamName);
        }, Styles.logict, () -> {
            changeTeam(getNextTeam());
        }).size(TUVars.iconSize).color(curTeam().color);

        Button b = i.get();
        b.update(() -> {
            if(b.isPressed() && !b.isDisabled()){
                tTimer += Time.delta;
                if(tTimer >= TUVars.longPress && !teamDialog.isShown()){
                    teamDialog.show(curTeam(), TeamChanger::changeTeam);
                }
            }else{
                tTimer = 0f;
            }

            b.setColor(curTeam().color);
        });

        return i;
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
