package testing.util;

import arc.scene.*;
import arc.util.*;
import mindustry.core.*;
import mindustry.game.*;
import mindustry.gen.*;
import testing.content.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class Utils{
    public static void spawnIconEffect(String sprite){
        TUFx.iconEffect.at(player.x, player.y, 0, "test-utils-" + sprite);
    }

    public static void runCommand(String command){
        Call.sendChatMessage("/" + command);
    }

    public static void runCommand(String command, Object... args){
        runCommand(Log.format(command, args));
    }

    public static void copyJS(String js, Object... args){
        if(net.client()) return;
        ui.showInfoFade("@copied");
        app.setClipboardText(Log.format(js, args));
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

    public static int countSpawns(SpawnGroup group){
        if(group.spawn != -1) return 1; //If the group has a set spawn pos, assume it's a valid position and count it as 1 spawn.

        //Otherwise count all.
        if(group.type.flying){
            return spawner.countFlyerSpawns();
        }
        return spawner.countGroundSpawns();
    }

    public static boolean hasMouse(){
        Element e = scene.hit(input.mouseX(), input.mouseY(), false);
        return e != null && !e.fillParent;
    }
}
