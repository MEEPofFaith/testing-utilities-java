package testing.buttons;

import arc.scene.ui.layout.*;
import mindustry.gen.*;
import testing.ui.*;
import testing.util.*;

public class Folding{
    public static void folding(Table t){
        t.button(Icon.resize, TUStyles.tuButtonStyle, () -> {
            TUVars.folded = !TUVars.folded;
        });
    }

    public static void add(Table[] tables){
        Table unfolded = tables[0];
        unfolded.table(Tex.buttonEdge3, Folding::folding);
        unfolded.fillParent = true;
        unfolded.visibility = ButtonVisibility.unfoldedVisibility;

        Table folded = tables[1];
        folded.table(Tex.buttonEdge3, Folding::folding);
        folded.fillParent = true;
        folded.visibility = ButtonVisibility.foldedVisibility;
    }
}
