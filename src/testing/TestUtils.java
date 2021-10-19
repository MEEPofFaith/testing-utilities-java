package testing;

import arc.*;
import arc.util.*;
import mindustry.*;
import mindustry.game.EventType.*;
import mindustry.mod.*;
import testing.ui.*;
import testing.util.*;

import static arc.Core.*;

public class TestUtils extends Mod{
    public TestUtils(){
        if(!Vars.headless){
            TUVars.TCOffset = settings.getBool("mod-time-control-enabled", false) ? 62 : 0;
            Events.on(ClientLoadEvent.class, e -> {
                TUStyles.init();
                Setup.init();
            });
        }
    }

    @Override
    public void loadContent(){
        //There is no content to load
    }

}
