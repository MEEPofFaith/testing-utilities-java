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

    /** Offset for when sk7725/timecontrol is enabled */
    public static float TCOffset;
    /** Used for positioning */
    public static float buttonHeight = 60f, miniWidth = 56f, iconWidth = 40f;
}