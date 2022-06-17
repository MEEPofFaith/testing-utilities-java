package testing.buttons;

import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import mindustry.*;
import mindustry.gen.*;
import testing.*;
import testing.ui.*;
import testing.util.*;

public class Console{
    static boolean shown;

    public static Cell<ImageButton> addToggleButton(Table t){
        Cell<ImageButton> i = t.button(new TextureRegionDrawable(Icon.eye), TUStyles.tuRedImageStyle, () -> {
            shown = !shown;
            Vars.ui.consolefrag.visible(() -> shown);
        });

        ImageButton b = i.get();
        TUElements.boxTooltip(b, "@tu-tooltip.button-console");
        b.setDisabled(TestUtils::disableCampaign);
        b.resizeImage(40f);

        return i;
    }
    
    public static Cell<ImageButton> addRefreshButton(Table t){
        Cell<ImageButton> i = t.button(new TextureRegionDrawable(Icon.refresh), TUStyles.tuRedImageStyle, () -> Vars.ui.consolefrag.clearMessages());

        ImageButton b = i.get();
        TUElements.boxTooltip(b, "@tu-tooltip.button-console");
        b.setDisabled(TestUtils::disableCampaign);
        b.resizeImage(40f);

        return i;
    }
    
    public static Cell<ImageButton> addTerminalButton(Table t){
        Cell<ImageButton> i = t.button(new TextureRegionDrawable(Icon.terminal), TUStyles.tuRedImageStyle, () -> Vars.ui.consolefrag.toggle());

        ImageButton b = i.get();
        TUElements.boxTooltip(b, "@tu-tooltip.button-console");
        b.setDisabled(TestUtils::disableCampaign);
        b.resizeImage(40f);

        return i;
    }

    public static void add(Table table){
        table.table(Tex.pane, t -> {
            addToggleButton(t).size(TUVars.iconSize, 40f);
            addRefreshButton(t).size(TUVars.iconSize, 40f);
            addTerminalButton(t).size(TUVars.iconSize, 40f);
        });
    }
}
