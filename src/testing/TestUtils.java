package testing;

import arc.*;
import mindustry.game.EventType.*;
import mindustry.mod.*;
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

            TUVars.TCOffset = settings.getBool("mod-time-control-enabled", false) ? 62 : 0;
            Events.on(ClientLoadEvent.class, e -> {
                TUIcons.init();
                TUStyles.init();
                TUSettings.init();
                Setup.init();
            });
        }
    }

    @Override
    public void loadContent(){
        //There is no content to load
    }

}