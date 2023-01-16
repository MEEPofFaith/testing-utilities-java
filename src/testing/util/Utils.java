package testing.util;

import arc.*;
import arc.util.*;
import mindustry.core.*;
import mindustry.gen.*;
import testing.content.*;

import static mindustry.Vars.*;

public class Utils{
    public static void spawnIconEffect(String sprite){
        TUFx.iconEffect.at(player.x, player.y, 0, "test-utils-" + sprite);
    }

    public static void runCommand(String command){
        boolean shown = ui.chatfrag.shown();
        if(!shown){
            if(TUVars.activeDialog != null) TUVars.activeDialog.hide();
            ui.chatfrag.toggle();
            Timer.schedule(() -> Timer.schedule(() -> {
                Call.sendChatMessage("/js " + command);
                ui.chatfrag.toggle();
            }, netClient.getPing() / 1000f), netClient.getPing() / 1000f);
        }else{
            Call.sendChatMessage("/js " + command);
        }
    }

    public static void runCommandPlayer(String command){
        runCommand("let e=Groups.player.getByID(" + player.id + ");" + command);
    }
    public static void runCommandPlayerShort(String command){
        runCommand("Groups.player.getByID(" + player.id + ")" + command);
    }

    public static String constructCommand(String base, Object... args){
        return Log.format(base, args);
    }

    public static String round(float f){
        if(f >= 1_000_000_000){
            return Strings.autoFixed(f / 1_000_000_000, 1) + UI.billions;
        }else if(f >= 1_000_000){
            return Strings.autoFixed(f / 1_000_000, 1) + UI.millions;
        }else if(f >= 1000){
            return Strings.autoFixed(f / 1000, 1) + UI.thousands;
        }else{
            return (int)f + "";
        }
    }
}
