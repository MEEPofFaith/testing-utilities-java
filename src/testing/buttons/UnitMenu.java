package testing.buttons;

import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import mindustry.gen.*;
import testing.ui.*;
import testing.util.*;

import static mindustry.Vars.*;
import static testing.ui.TUDialogs.*;

public class UnitMenu{
    public static Cell<ImageButton> addButton(Table t){
        ImageButton b = new ImageButton(unitDialog.getUnit().uiIcon, TUStyles.tuRedImageStyle);
        TUElements.boxTooltip(b, "@tu-tooltip.button-units");
        b.clicked(unitDialog::show);
        b.setDisabled(() -> state.isCampaign());
        b.resizeImage(40f);
        b.update(() -> {
            ((TextureRegionDrawable)(b.getStyle().imageUp)).setRegion(unitDialog.getUnit().uiIcon);
        });

        return t.add(b).growX();
    }

    public static void add(Table table){
        table.table(Tex.pane, t -> {
            addButton(t).size(TUVars.iconWidth, 40f);
        }).padBottom(TUVars.TCOffset + TUVars.buttonHeight).padLeft(TUVars.iconWidth + 20);
    }
}
