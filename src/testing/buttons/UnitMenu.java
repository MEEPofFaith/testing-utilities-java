package testing.buttons;

import arc.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import mindustry.gen.*;
import testing.dialogs.*;
import testing.ui.*;
import testing.util.*;

import static mindustry.Vars.*;

public class UnitMenu{
    static UnitDialog unitDialog;

    public static void init(){
        unitDialog = new UnitDialog();
    }

    public static Cell<ImageButton> addButton(Table t, boolean label){
        ImageButton b = new ImageButton(unitDialog.getUnit().uiIcon, TUStyles.tuRedImageStyle);
        if(!mobile && label) b.label(() -> Core.bundle.format("tu-unit-menu.button", b.isDisabled() ? "gray" : "white")).growX();
        b.clicked(unitDialog::show);
        b.setDisabled(() -> state.isCampaign());
        b.resizeImage(40f);
        b.update(() -> {
            ((TextureRegionDrawable)(b.getStyle().imageUp)).setRegion(unitDialog.getUnit().uiIcon);
        });

        return t.add(b).growX();
    }

    public static void add(Table[] tables){
        tables[0].table(Tex.pane, t -> {
            addButton(t, !mobile).size(TUVars.iconWidth + (mobile ? 0 : 116), 40f);
        }).padBottom(TUVars.TCOffset + TUVars.buttonHeight * 2);

        tables[1].table(Tex.pane, t -> {
            addButton(t, false).size(TUVars.iconWidth, 40f);
        }).padBottom(TUVars.TCOffset + (mobile ? 0 : TUVars.buttonHeight)).padLeft(TUVars.iconWidth + 20);
    }
}
