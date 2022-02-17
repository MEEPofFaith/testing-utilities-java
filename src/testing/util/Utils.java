package testing.util;

import arc.math.*;
import arc.util.*;
import arc.util.async.*;
import mindustry.core.*;
import mindustry.gen.*;
import testing.content.*;

import static arc.Core.bundle;
import static mindustry.Vars.*;

public class Utils{
    /** Extracts a number out of a string by removing every non-numerical character  */
    public static String extractNumber(String s){
        //God, I love google. I have no idea what the "[^\\d.]" part even is.
        return s.replaceAll("[^\\d.]", "");
    }

    public static void spawnIconEffect(String sprite){
        TUFx.iconEffect.at(player.x, player.y, 0, "test-utils-" + sprite);
    }

    public static boolean noCheat(){
        if(!net.client() && state.isCampaign()){
            /* lmao
            Groups.build.each(b -> {
                if(b.team == state.rules.defaultTeam){
                    b.kill();
                }
            });
            */
            Threads.throwAppException(new Throwable("No cheating! Don't use Testing Utilities in campaign!"));
            return false;
        }
        return true;
    }

    public static void runCommand(String command){
        Call.sendChatMessage("/js " + command);
    }

    public static void runCommandPlayer(String command){
        String code =
            "Groups.player.each(p => p.name == \"" + fixQuotes(player.name) + "\", " +
            "p => {" + command + "}" +
            ")";
        runCommand(code);
    }

    public static String fixQuotes(String s){
        return s.replaceAll("\"", "\\\\\"");
    }

    public static String roundAmount(float amount){
        amount = UI.roundAmount((int)amount);
        if(amount >= 1_000_000_000){
            return Strings.autoFixed(amount / 1_000_000_000, 1) + bundle.get("unit.billions");
        }else if(amount >= 1_000_000){
            return Strings.autoFixed(amount / 1_000_000, 1) + bundle.get("unit.millions");
        }else if(amount >= 1000){
            return Strings.autoFixed(amount / 1000, 1) + bundle.get("unit.thousands");
        }else{
            return String.valueOf((int)amount);
        }
    }
}
