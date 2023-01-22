package testing.buttons;

import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.*;
import mindustry.game.*;
import mindustry.gen.*;
import testing.*;
import testing.ui.*;
import testing.util.*;

import static mindustry.Vars.*;
import static testing.ui.TUDialogs.*;

public class TeamChanger extends TUButton{
    static float tTimer;

    public static void changeTeam(Team team){
        if(Vars.net.client()){
            Utils.runCommandPlayerShort(".team(Team.get(" + team.id + "));");
        }else{
            player.team(team);
        }
    }

    public static Cell<Button> addMini(Table t){
        Cell<Button> i = t.button(b -> {
            TUElements.boxTooltip(b, "@tu-tooltip.button-team");
            b.setDisabled(() -> TestUtils.disableButton() || player.unit().type.internal);
            b.label(TeamChanger::teamName);
        }, TUStyles.redButtonStyle, () -> {
            if(tTimer > TUVars.longPress) return;
            changeTeam(getNextTeam());
        }).size(TUVars.iconSize).color(curTeam().color);

        Button b = i.get();
        b.update(() -> {
            if(b.isPressed() && !b.isDisabled()){
                tTimer += Time.delta;
                if(tTimer > TUVars.longPress){
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

    public void add(Table table){
        table.table(Tex.pane, t -> addMini(t).size(100, TUVars.iconSize));
    }

    static String teamName(){
        String t = teamDialog.teamName(curTeam());
        return "[#" + curTeam().color.toString() + "]" + t.substring(0, 1).toUpperCase() + t.substring(1);
    }
}
