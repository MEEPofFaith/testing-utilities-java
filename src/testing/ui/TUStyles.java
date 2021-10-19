package testing.ui;

import arc.scene.style.*;
import arc.scene.ui.Button.*;
import arc.scene.ui.ImageButton.*;
import mindustry.gen.*;
import mindustry.ui.*;

import static arc.graphics.Color.*;

public class TUStyles{
    public static ButtonStyle redButtonStyle;
    public static ImageButtonStyle tuButtonStyle;

    public static void init(){
        redButtonStyle = new ButtonStyle(Styles.logict){{
            disabled = ((TextureRegionDrawable)(Tex.whiteui)).tint(0.625f, 0, 0, 0.8f);
        }};

        tuButtonStyle = new ImageButtonStyle(Styles.logici){{
            down = Styles.flatDown;
            over = Styles.flatOver;
            imageDisabledColor = gray;
            imageUpColor = white;
        }};
    }
}
