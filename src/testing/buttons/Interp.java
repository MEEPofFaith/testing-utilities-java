package testing.buttons;

import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import mindustry.gen.*;
import testing.ui.*;
import testing.util.*;

import static testing.ui.TUDialogs.*;

public class Interp{
    public static Cell<ImageButton> addButton(Table t){
        Cell<ImageButton> i = t.button(Icon.line, TUStyles.tuRedImageStyle, interpDialog::show);

        ImageButton b = i.get();
        TUElements.boxTooltip(b, "@tu-tooltip.button-interp");
        b.resizeImage(40f);

        return i;
    }

    public static void add(Table table){
        table.table(Tex.pane, t -> addButton(t).size(TUVars.iconSize, 40f));
    }
}
