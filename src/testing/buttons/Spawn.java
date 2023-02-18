package testing.buttons;

import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import mindustry.content.*;
import testing.ui.*;
import testing.util.*;

import static mindustry.Vars.*;
import static testing.ui.TUDialogs.*;

public class Spawn{
    public static boolean spawnHover, blockHover;

    public static void unitMenu(Table t){
        ImageButton b = new ImageButton(unitDialog.getUnit().uiIcon, TUStyles.tuImageStyle);
        TUElements.boxTooltip(b, "@tu-tooltip.button-units");
        b.clicked(unitDialog::show);
        b.resizeImage(40f);
        b.update(() -> {
            ((TextureRegionDrawable)(b.getStyle().imageUp)).setRegion(unitDialog.getUnit().uiIcon);
        });
        b.hovered(() -> spawnHover = true);
        b.exited(() -> spawnHover = false);

        t.add(b);
    }

    public static void blockMenu(Table t){
        ImageButton b = new ImageButton(blockDialog.getBlock().uiIcon, TUStyles.tuImageStyle);
        TUElements.boxTooltip(b, "@tu-tooltip.button-block");
        b.clicked(() -> {
            if(net.client()){
                Utils.runCommand("core pos");
            }else{
                blockDialog.show();
            }
        });
        b.resizeImage(40f);
        b.update(() -> {
            ((TextureRegionDrawable)(b.getStyle().imageUp)).setRegion((net.client() ? Blocks.coreShard : blockDialog.getBlock()).uiIcon);
        });
        b.hovered(() -> blockHover = true);
        b.exited(() -> blockHover = false);

        t.add(b);
    }

    public static void placeCore(Table t){
        ImageButton b = new ImageButton(Blocks.coreShard.uiIcon, TUStyles.tuImageStyle);
        TUElements.boxTooltip(b, "@tu-tooltip.button-core");
        b.clicked(() -> {
            if(net.client()) Utils.runCommand("core pos");
        });
        b.resizeImage(40f);
        t.add(b);
    }

    public static void addButtons(Table t){
        unitMenu(t);
        blockMenu(t);
    }
}
