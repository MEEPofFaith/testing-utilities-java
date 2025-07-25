package testing.buttons;

import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import blui.ui.*;
import testing.ui.*;

import static mindustry.Vars.*;
import static testing.ui.TUDialogs.*;

public class Effect{
    public static void statusButton(Table t){
        ImageButton b = new ImageButton(statusDialog.getStatus().uiIcon, TUStyles.tuImageStyle);
        BLElements.boxTooltip(b, "@tu-tooltip.button-status");
        b.clicked(statusDialog::show);
        b.setDisabled(() -> player.unit() == null || player.unit().type.internal);
        b.update(() -> {
            ((TextureRegionDrawable)(b.getStyle().imageUp)).setRegion(statusDialog.getStatus().uiIcon);
        });

        t.add(b);
    }
}
