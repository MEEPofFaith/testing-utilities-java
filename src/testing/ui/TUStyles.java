package testing.ui;

import arc.scene.style.*;
import arc.scene.ui.ImageButton.*;
import mindustry.gen.*;
import mindustry.ui.*;

import static arc.Core.*;
import static arc.graphics.Color.*;

public class TUStyles{
    public static Drawable
        buttonLeft, buttonLeftDown, buttonLeftOver,
        buttonCenter, buttonCenterDown, buttonCenterOver,
        buttonRight, buttonRightOver, buttonRightDown,
        paneBottom;
    public static ImageButtonStyle tuImageStyle, togglei, lefti, toggleLefti, righti, toggleRighti, centeri;

    public static void init(){
        buttonLeft = atlas.getDrawable("test-utils-button-left");
        buttonLeftDown = atlas.getDrawable("test-utils-button-left-down");
        buttonLeftOver = atlas.getDrawable("test-utils-button-left-over");
        buttonCenter = atlas.getDrawable("test-utils-button-center");
        buttonCenterDown = atlas.getDrawable("test-utils-button-center-down");
        buttonCenterOver = atlas.getDrawable("test-utils-button-center-over");
        buttonRight = atlas.getDrawable("test-utils-button-right");
        buttonRightDown = atlas.getDrawable("test-utils-button-right-down");
        buttonRightOver = atlas.getDrawable("test-utils-button-right-over");
        paneBottom = atlas.getDrawable("test-utils-pane-bottom");

        tuImageStyle = new ImageButtonStyle(Styles.logici){{
            down = Styles.flatDown;
            over = Styles.flatOver;
            imageDisabledColor = gray;
            imageUpColor = white;
        }};

        togglei = new ImageButtonStyle(Styles.defaulti){{
            checked = Tex.buttonOver;
        }};

        lefti = new ImageButtonStyle(Styles.defaulti){{
            up = buttonLeft;
            down = buttonLeftDown;
            over = buttonLeftOver;
        }};

        toggleLefti = new ImageButtonStyle(lefti){{
            checked = buttonLeftOver;
        }};

        righti = new ImageButtonStyle(Styles.defaulti){{
            up = buttonRight;
            down = buttonRightDown;
            over = buttonRightOver;
        }};

        toggleRighti = new ImageButtonStyle(righti){{
            checked = buttonRightOver;
        }};

        centeri = new ImageButtonStyle(Styles.defaulti){{
            up = buttonCenter;
            down = buttonCenterDown;
            over = buttonCenterOver;
        }};
    }
}
