package testing.util;

import arc.*;
import mindustry.*;
import mindustry.game.*;

import static arc.Core.*;

public class TUVars{
    public static float longPress = 30f;

    public static Team curTeam = Team.sharded;
    public static boolean folded;

    /** Offset for when sk7725/timecontrol is enabled */
    public static float TCOffset;
    /** Used for positioning */
    public static float buttonHeight = 60f, miniWidth = 56f, iconWidth = 40f;

    public static void setDefaults(){
        TCOffset = settings.getBool("mod-time-control-enabled", false) && Vars.mods.getMod("time-control") != null ? 62 : 0;
        folded = settings.getBool("tu-startfolded", false);
    }
}