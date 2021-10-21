package testing.ui;

import arc.*;
import arc.scene.style.*;
import arc.scene.ui.Button.*;
import arc.scene.ui.ImageButton.*;
import mindustry.gen.*;
import mindustry.ui.*;

import static arc.graphics.Color.*;

public class TUStyles{
    public static Drawable redBack, buttonEdgeCap;
    public static ButtonStyle redButtonStyle;
    public static ImageButtonStyle tuImageStyle, tuRedImageStyle;

    public static void init(){
        redBack = ((TextureRegionDrawable)(Tex.whiteui)).tint(0.625f, 0, 0, 0.8f);
        buttonEdgeCap = Core.atlas.getDrawable("test-utils-button-edge-cap");

        redButtonStyle = new ButtonStyle(Styles.logict){{
            disabled = redBack;
        }};

        tuImageStyle = new ImageButtonStyle(Styles.logici){{
            down = Styles.flatDown;
            over = Styles.flatOver;
            imageDisabledColor = gray;
            imageUpColor = white;
        }};

        tuRedImageStyle = new ImageButtonStyle(tuImageStyle){{
            disabled = redBack;
        }};
    }
}