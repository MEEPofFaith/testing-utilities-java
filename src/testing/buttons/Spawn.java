package testing.buttons;

import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import mindustry.content.*;
import mindustry.gen.*;
import testing.*;
import testing.ui.*;
import testing.util.*;

import static mindustry.Vars.*;
import static testing.ui.TUDialogs.*;

public class Spawn extends TUButton{
    public static boolean spawnHover, blockHover;

    public static Cell<ImageButton> unitMenu(Table t){
        ImageButton b = new ImageButton(unitDialog.getUnit().uiIcon, TUStyles.tuRedImageStyle);
        TUElements.boxTooltip(b, "@tu-tooltip.button-units");
        b.clicked(unitDialog::show);
        b.setDisabled(TestUtils::disableCommandButton);
        b.resizeImage(40f);
        b.update(() -> {
            ((TextureRegionDrawable)(b.getStyle().imageUp)).setRegion(unitDialog.getUnit().uiIcon);
        });
        b.hovered(() -> spawnHover = true);
        b.exited(() -> spawnHover = false);

        return t.add(b).growX();
    }

    public static Cell<ImageButton> blockMenu(Table t){
        ImageButton b = new ImageButton(blockDialog.getBlock().uiIcon, TUStyles.tuRedImageStyle);
        TUElements.boxTooltip(b, "@tu-tooltip.button-block");
        b.clicked(() -> {
            if(net.client()){
                Utils.runCommand("core pos");
            }else{
                blockDialog.show();
            }
        });
        b.setDisabled(TestUtils::disableCommandButton);
        b.resizeImage(40f);
        b.update(() -> {
            ((TextureRegionDrawable)(b.getStyle().imageUp)).setRegion((net.client() ? Blocks.coreShard : blockDialog.getBlock()).uiIcon);
        });
        b.hovered(() -> blockHover = true);
        b.exited(() -> blockHover = false);

        return t.add(b).growX();
    }

    public void add(Table table){
        table.table(Tex.pane, t -> {
            unitMenu(t).size(TUVars.iconSize, TUVars.iconSize);
            blockMenu(t).size(TUVars.iconSize, TUVars.iconSize);
        });
    }
}
