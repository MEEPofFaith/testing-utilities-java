package testing;

import arc.*;
import arc.func.*;
import arc.util.*;
import mindustry.game.EventType.*;
import mindustry.mod.*;
import mindustry.mod.Mods.*;
import testing.content.*;
import testing.ui.*;
import testing.util.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class TestUtils extends Mod{
    public TestUtils(){
        if(!headless){
            enableConsole =  experimental = true; //Dev mode
            renderer.minZoom = 0.667f; //Zoom out farther
            renderer.maxZoom = 24f; //Get a closer look at yourself

            Events.on(ClientLoadEvent.class, e -> {
                TUIcons.init();
                TUStyles.init();
                TUSettings.init();
                Setup.init();
            });
        }
    }

    @Override
    public void init(){
        if(!headless){
            LoadedMod tu = mods.locateMod("test-utils");

            Func<String, String> getModBundle = value -> bundle.get("mod." + value);

            tu.meta.displayName = "[#FCC21B]" + tu.meta.displayName;
            tu.meta.author = "[#FCC21B]" + tu.meta.author;

            StringBuilder tools = new StringBuilder(getModBundle.get(tu.meta.name + ".description"));
            tools.append("\n\n");
            int i = 0;
            while(bundle.has("mod." + tu.meta.name + "-tool." + i)){
                tools.append("\n    ").append(getModBundle.get(tu.meta.name + "-tool." + i));
                i++;
            }
            tu.meta.description = tools.toString();
        }
    }

    public static boolean disableCampaign(){
        return state.isCampaign() && !OS.username.equals("MEEP");
    }
}
