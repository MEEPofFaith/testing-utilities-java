package testing.ui;

import arc.scene.ui.ImageButton.*;
import mindustry.ui.*;

import static arc.graphics.Color.*;

public class TUStyles{
    public static ImageButtonStyle tuButtonStyle;

    public static void init(){
        tuButtonStyle = new ImageButtonStyle(Styles.logici){{
            down = Styles.flatDown;
            over = Styles.flatOver;
            imageDisabledColor = gray;
            imageUpColor = white;
        }};
    }
}
