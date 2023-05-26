package testing.buttons;

import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import testing.ui.*;

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
}
