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

    public static Cell<ImageButton> addButton(Table t){
        Cell<ImageButton> i = t.button(new TextureRegionDrawable(Icon.terminal), TUStyles.tuRedImageStyle, () -> {
            shown = !shown;
            Vars.ui.scriptfrag.toggle();
        });

        ImageButton b = i.get();
        TUElements.boxTooltip(b, "@tu-tooltip.button-console");
        b.setDisabled(TestUtils::disableCampaign);
        b.resizeImage(40f);

        return i;
    }

    public static void add(Table table){
        table.table(Tex.pane, t -> addButton(t).size(TUVars.iconSize, 40f));

        Vars.ui.scriptfrag.visible(() -> shown);
    }
}
