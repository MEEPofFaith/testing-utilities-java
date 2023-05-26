package testing.buttons;

import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import mindustry.gen.*;
import testing.*;
import testing.ui.*;
import testing.util.*;

import static testing.ui.TUDialogs.*;

public class Fields{
    public static void addButton(Table t){
        ImageButton b = new ImageButton(Tex.alphaaaa, TUStyles.tuImageStyle);
        TUElements.boxTooltip(b, "@tu-tooltip.button-fields");
        b.clicked(fieldEditor::show);

        t.add(b);
    }
}
