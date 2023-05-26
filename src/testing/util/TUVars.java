package testing.util;

import arc.*;
import mindustry.game.*;
import testing.dialogs.*;

import static arc.Core.*;

public class TUVars{
    public static float longPress, pressTimer;

    public static Team curTeam = Team.sharded;
    public static TUBaseDialog activeDialog;
    public static float iconSize = 40f, buttonSize = 24f, sliderWidth = 140f, fieldWidth = 80f;

    public static void setDefaults(){
        longPress = settings.getInt("tu-long-press", 2) * 60f / 4f;
    }

    public static float delta(){
        return Core.graphics.getDeltaTime() * 60;
    }
}
