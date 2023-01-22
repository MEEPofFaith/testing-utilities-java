package testing.buttons;

import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import mindustry.gen.*;
import testing.*;
import testing.ui.*;
import testing.util.*;

import static testing.ui.TUDialogs.*;

public class Effect extends TUButton{
    public static Cell<ImageButton> statusButton(Table t){
        ImageButton b = new ImageButton(statusDialog.getStatus().uiIcon, TUStyles.tuRedImageStyle);
        TUElements.boxTooltip(b, "@tu-tooltip.button-status");
        b.clicked(statusDialog::show);
        b.setDisabled(TestUtils::disableButton);
        b.update(() -> {
            ((TextureRegionDrawable)(b.getStyle().imageUp)).setRegion(statusDialog.getStatus().uiIcon);
        });

        return t.add(b).growX();
    }

    public static Cell<ImageButton> weatherButton(Table t){
        ImageButton b = new ImageButton(TUIcons.weather, TUStyles.tuRedImageStyle);
        TUElements.boxTooltip(b, "@tu-tooltip.button-weather");
        b.clicked(weatherDialog::show);
        b.setDisabled(TestUtils::disableButton);
        b.resizeImage(40f);

        return t.add(b).growX();
    }

    public void add(Table table){
        table.table(Tex.pane, t -> {
            statusButton(t).size(TUVars.iconSize, TUVars.iconSize);
            weatherButton(t).size(TUVars.iconSize, TUVars.iconSize);
        });
    }
}
