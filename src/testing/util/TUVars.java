package testing.util;

import arc.*;
import arc.util.*;
import mindustry.core.*;
import mindustry.game.*;
import testing.dialogs.*;
import testing.editor.*;

import static arc.Core.*;

public class TUVars{
    public static float longPress, pressTimer;

    public static Team curTeam = Team.sharded;
    public static TUBaseDialog activeDialog;
    public static TerrainPainter painter = new TerrainPainter();
    public static TerrainPaintbrush paintbrush = new TerrainPaintbrush();
    public static boolean foos = Structs.contains(Version.class.getDeclaredFields(), var -> var.getName().equals("foos"));
    public static float iconSize = 40f, buttonSize = 24f, sliderWidth = 140f, fieldWidth = 80f;

    public static void setDefaults(){
        longPress = settings.getInt("tu-long-press", 2) * 60f / 4f;
    }

    /** Delta time that is unaffected by time control. */
    public static float delta(){
        return Core.graphics.getDeltaTime() * 60;
    }
}
