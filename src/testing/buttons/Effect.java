package testing.buttons;

import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import testing.ui.*;
import testing.util.*;

import static testing.ui.TUDialogs.*;

public class Effect{
    public static void statusButton(Table t){
        ImageButton b = new ImageButton(statusDialog.getStatus().uiIcon, TUStyles.tuImageStyle);
        TUElements.boxTooltip(b, "@tu-tooltip.button-status");
        b.clicked(statusDialog::show);
        b.update(() -> {
            ((TextureRegionDrawable)(b.getStyle().imageUp)).setRegion(statusDialog.getStatus().uiIcon);
        });

        t.add(b);
    }

    public static void weatherButton(Table t){
        ImageButton b = new ImageButton(TUIcons.weather, TUStyles.tuImageStyle);
        TUElements.boxTooltip(b, "@tu-tooltip.button-weather");
        b.clicked(weatherDialog::show);
        b.resizeImage(40f);

        t.add(b);
    }

    public static void addButtons(Table t){
        statusButton(t);
        weatherButton(t);
    }
}
