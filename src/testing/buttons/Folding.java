package testing.buttons;

import arc.scene.ui.layout.*;
import mindustry.*;
import mindustry.gen.*;
import testing.ui.*;
import testing.util.*;

public class Folding{
    public static void folding(Table t){
        t.button(Icon.resize, TUStyles.tuImageStyle, () -> {
            TUVars.folded = !TUVars.folded;
        }).size(40).pad(0);
    }

    public static void add(Table[] tables){
        tables[0].table(Tex.buttonEdge3, Folding::folding)
            .padLeft(Vars.mobile ? 164 : 480);
        tables[1].table(Tex.buttonEdge3, Folding::folding)
            .padBottom(Vars.mobile ? TUVars.buttonHeight : 0).padLeft(Vars.mobile ? 144 : 220);
    }
}