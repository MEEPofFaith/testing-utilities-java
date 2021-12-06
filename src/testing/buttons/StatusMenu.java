package testing.buttons;

import arc.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import mindustry.gen.*;
import testing.buttons.dialogs.*;
import testing.ui.*;
import testing.util.*;

import static mindustry.Vars.*;

public class StatusMenu{
    static StatusDialog statusDialog;

    public static void init(){
        statusDialog = new StatusDialog();
    }

    public static Cell<ImageButton> addButton(Table t, boolean label){
        ImageButton b = new ImageButton(statusDialog.getStatus().uiIcon, TUStyles.tuRedImageStyle);
        if(!mobile && label) b.label(() -> Core.bundle.format("tu-status-menu.button", b.isDisabled() ? "gray" : "white")).growX();
        b.clicked(statusDialog::show);
        b.setDisabled(() -> state.isCampaign());
        b.resizeImage(40f);
        b.update(() -> {
            ((TextureRegionDrawable)(b.getStyle().imageUp)).setRegion(statusDialog.getStatus().uiIcon);
        });

        return t.add(b).growX();
    }

    public static void add(Table[] tables){
        tables[0].table(Tex.pane, t -> {
            addButton(t, !mobile).size(TUVars.iconWidth + (mobile ? 0 : 128), 40f);
        }).padBottom(TUVars.TCOffset + TUVars.buttonHeight);

        tables[1].table(Tex.pane, t -> {
            addButton(t, false).size(TUVars.iconWidth, 40f);
        }).padBottom(TUVars.TCOffset + (mobile ? 0 : TUVars.buttonHeight));
    }
}