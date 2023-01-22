package testing.buttons;

import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import mindustry.*;
import mindustry.gen.*;
import testing.*;
import testing.ui.*;
import testing.util.*;

public class Console extends TUButton{
    static boolean shown;

    public static Cell<ImageButton> addToggleButton(Table t){
        Cell<ImageButton> i = t.button(new TextureRegionDrawable(Icon.eye), TUStyles.tuRedImageStyle, TUVars.iconSize, () -> {
            shown = !shown;
            Vars.ui.consolefrag.visible(() -> shown);
        });

        ImageButton b = i.get();
        TUElements.boxTooltip(b, "@tu-tooltip.button-console-show");

        return i;
    }
    
    public static Cell<ImageButton> addRefreshButton(Table t){
        Cell<ImageButton> i = t.button(new TextureRegionDrawable(Icon.trash), TUStyles.tuRedImageStyle, TUVars.iconSize, () -> Vars.ui.consolefrag.clearMessages());

        ImageButton b = i.get();
        TUElements.boxTooltip(b, "@tu-tooltip.button-console-clear");

        return i;
    }
    
    public static Cell<ImageButton> addTerminalButton(Table t){
        Cell<ImageButton> i = t.button(new TextureRegionDrawable(Icon.terminal), TUStyles.tuRedImageStyle, TUVars.iconSize, () -> Vars.ui.consolefrag.toggle());

        ImageButton b = i.get();
        TUElements.boxTooltip(b, "@tu-tooltip.button-console-input");

        return i;
    }

    public void add(Table table){
        table.table(Tex.buttonEdge3, t -> {
            addToggleButton(t).size(TUVars.iconSize, TUVars.iconSize);
            addRefreshButton(t).size(TUVars.iconSize, TUVars.iconSize);
            addTerminalButton(t).size(TUVars.iconSize, TUVars.iconSize);
        });
    }
}
