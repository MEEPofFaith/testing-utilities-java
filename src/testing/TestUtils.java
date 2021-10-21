package testing;

import arc.*;
import mindustry.*;
import mindustry.game.EventType.*;
import mindustry.mod.*;
import testing.content.*;
import testing.ui.*;
import testing.util.*;

import static arc.Core.*;

public class TestUtils extends Mod{
    public TestUtils(){
        if(!Vars.headless){
            TUVars.TCOffset = settings.getBool("mod-time-control-enabled", false) ? 46 : 0;
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