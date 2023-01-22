package testing.util;

import arc.*;
import arc.math.*;
import arc.util.*;
import mindustry.core.*;
import mindustry.gen.*;
import testing.content.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class Utils{
    public static void spawnIconEffect(String sprite){
        TUFx.iconEffect.at(player.x, player.y, 0, "test-utils-" + sprite);
    }

    public static void runCommand(String command){
        if(Core.settings.getBool("tu-clipboard", false)){
            app.setClipboardText("/js " + command);
            ui.showInfoFade("@copied");
        }else{
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
    }

    public static void runCommandPlayer(String command, char playerChar){
        runCommand(constructCommand("let @=Groups.player.getByID(@);@",
            playerChar, player.id, command
        ));
    }
    public static void runCommandPlayerShort(String command){
        runCommand(constructCommand("Groups.player.getByID(@)@",
            player.id, command
        ));
    }

    public static String constructCommand(String base, Object... args){
        return Log.format(base, args);
    }

    public static char rand1(){
        char c;
        do{
            c = (char)(65 + Mathf.range(122-65));
        }while(c == 'p' || !Character.isLetter(c));
        return c;
    }

    public static char rand2(char other){
        char c;
        do{
            c = (char)(65 + Mathf.range(122-65));
        }while(c == 'p' || c == other || !Character.isLetter(c));
        return c;
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
