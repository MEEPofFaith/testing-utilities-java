package testing.buttons;

import arc.graphics.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.ImageButton.*;
import arc.scene.ui.layout.*;
import testing.ui.*;
import testing.util.*;

import static testing.ui.TUDialogs.*;

public class Environment{
    public static void weatherButton(Table t){
        ImageButton b = new ImageButton(TUIcons.weather, TUStyles.tuImageStyle);
        TUElements.boxTooltip(b, "@tu-tooltip.button-weather");
        b.clicked(weatherDialog::show);
        b.resizeImage(40f);

        t.add(b);
    }

    public static void planetButton(Table t){
        ImageButton b = new ImageButton(environmentDialog.getIcon(), TUStyles.tuImageStyle);
        TUElements.boxTooltip(b, "@tu-tooltip.button-planet");
        b.clicked(environmentDialog::show);
        b.update(() -> {
            ImageButtonStyle style = b.getStyle();
            ((TextureRegionDrawable)(style.imageUp)).setRegion(environmentDialog.getIcon());
            Color iColor = environmentDialog.getIconColor();
            style.imageDownColor = style.imageUpColor = style.imageOverColor = iColor;
        });

        t.add(b);
    }

    public static void addButtons(Table t){
        weatherButton(t);
        planetButton(t);
    }
}
