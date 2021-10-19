package testing;

import arc.util.async.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.mod.*;
import testing.content.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class TestingUtilities extends Mod{
    public float longPress = 30f;

    public Team curTeam = Team.sharded;
    public boolean folded;

    public float TCOffset;
    public float buttonHeight = 60f, buttonWidth = 56f, iconSize = 40f;

    public TestingUtilities(){
        TCOffset = settings.getBool("mod-time-control-enabled", false) ? 62 : 0;
    }

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

    @Override
    public void loadContent(){
        //There is no content to load
    }

}
