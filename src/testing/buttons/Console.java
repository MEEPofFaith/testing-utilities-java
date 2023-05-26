package testing.buttons;

import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import mindustry.*;
import mindustry.gen.*;
import testing.ui.*;
import testing.util.*;

public class Console{
    static boolean shown;

    public static void addToggleButton(Table t){
        Cell<ImageButton> i = t.button(new TextureRegionDrawable(Icon.eye), TUStyles.tuImageStyle, TUVars.iconSize, () -> {
            shown = !shown;
            Vars.ui.consolefrag.visible(() -> shown);
        });

        ImageButton b = i.get();
        TUElements.boxTooltip(b, "@tu-tooltip.button-console-show");

    }
    
    public static void addRefreshButton(Table t){
        Cell<ImageButton> i = t.button(new TextureRegionDrawable(Icon.trash), TUStyles.tuImageStyle, TUVars.iconSize, () -> Vars.ui.consolefrag.clearMessages());

        ImageButton b = i.get();
        TUElements.boxTooltip(b, "@tu-tooltip.button-console-clear");

    }
    
    public static void addTerminalButton(Table t){
        Cell<ImageButton> i = t.button(new TextureRegionDrawable(Icon.terminal), TUStyles.tuImageStyle, TUVars.iconSize, () -> Vars.ui.consolefrag.toggle());

        ImageButton b = i.get();
        TUElements.boxTooltip(b, "@tu-tooltip.button-console-input");

    }

    public static void addButtons(Table t){
        addToggleButton(t);
        addRefreshButton(t);
        addTerminalButton(t);
    }
}
