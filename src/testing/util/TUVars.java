package testing.util;

import arc.*;
import mindustry.game.*;

public class TUVars{
    public static float longPress = 30f;

    public static Team curTeam = Team.sharded;
    public static boolean folded, despawns = true, perma = false;

    /** Offset for when sk7725/timecontrol is enabled */
    public static float TCOffset;
    /** Used for positioning */
    public static float buttonHeight = 60f, miniWidth = 56f, iconWidth = 40f;

    public static void setDefaults(){
        folded = Core.settings.getBool("tu-startfolded", false);
    }
}