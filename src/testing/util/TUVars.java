package testing.util;

import arc.util.async.*;
import mindustry.game.*;
import mindustry.gen.*;
import testing.content.*;

import static mindustry.Vars.*;

public class TUVars{
    public static float longPress = 30f;

    public static Team curTeam = Team.sharded;
    public static boolean folded;

    public static float TCOffset;
    public static float buttonHeight = 60f, buttonWidth = 56f, iconSize = 40f;

    public static void spawnIconEffect(String sprite){
        TUFx.iconEffect.at(player.x, player.y, 0, sprite);
    }

    public static void check(){
        /* lmao
        Groups.build.each(b -> {
            if(b.team == state.rules.defaultTeam){
                b.kill();
            }
        });
        */
        if(!net.client() && state.isCampaign()){
            Threads.throwAppException(new Throwable("No cheating! Don't use Testing Utilities in campaign!"));
        }
    }

    public static void runCommand(String command){
        String code = "Groups.player.each(p=>{p.name.includes(\"";
        code += player.name;
        code += "\")?";
        code += command;
        code += ":0})";
        Call.sendChatMessage("/js" + code);
    }
}
