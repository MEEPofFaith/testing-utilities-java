package testing.util;

import mindustry.*;
import mindustry.game.*;

import static arc.Core.*;

public class TUVars{
    public static float longPress;

    public static Team curTeam = Team.sharded;

    /** Offset for when sk7725/timecontrol is enabled */
    public static float TCOffset;
    /** Used for positioning */
    public static float buttonHeight = 60f, iconWidth = 40f, buttonSize = 24f;

    public static void setDefaults(){
        longPress = settings.getInt("tu-long-press", 2) * 60f / 4f;
        TCOffset = settings.getBool("mod-time-control-enabled", false) && Vars.mods.getMod("time-control") != null ? 62 : 0;
    }
}
