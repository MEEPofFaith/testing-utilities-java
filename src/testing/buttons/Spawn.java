package testing.buttons;

import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import blui.*;
import blui.ui.*;
import mindustry.content.*;
import testing.ui.*;

import static mindustry.Vars.*;
import static testing.ui.TUDialogs.*;

public class Spawn{
    public static boolean spawnHover, blockHover;

    public static void unitMenu(Table t){
        ImageButton b = new ImageButton(unitDialog.getUnit().uiIcon, TUStyles.tuImageStyle);
        BLElements.boxTooltip(b, "@tu-tooltip.button-units");
        b.clicked(unitDialog::show);
        b.resizeImage(BLVars.iconSize);
        b.update(() -> {
            ((TextureRegionDrawable)(b.getStyle().imageUp)).setRegion(unitDialog.getUnit().uiIcon);
        });
        b.hovered(() -> spawnHover = true);
        b.exited(() -> spawnHover = false);

        t.add(b);
    }

    public static void blockMenu(Table t){
        ImageButton b = new ImageButton(blockDialog.getBlock().uiIcon, TUStyles.tuImageStyle);
        BLElements.boxTooltip(b, "@tu-tooltip.button-block");
        b.clicked(blockDialog::show);
        b.resizeImage(BLVars.iconSize);
        b.update(() -> {
            ((TextureRegionDrawable)(b.getStyle().imageUp)).setRegion((net.client() ? Blocks.coreShard : blockDialog.getBlock()).uiIcon);
        });
        b.hovered(() -> blockHover = true);
        b.exited(() -> blockHover = false);

        t.add(b);
    }

    public static void addButtons(Table t){
        unitMenu(t);
        blockMenu(t);
    }
}
