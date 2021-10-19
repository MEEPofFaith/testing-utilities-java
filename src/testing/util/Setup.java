package testing.util;

import arc.scene.ui.layout.*;
import mindustry.gen.*;
import mindustry.type.*;
import testing.buttons.*;

import static mindustry.Vars.*;

public class Setup{
    public static Table[]

    folder = newTables(),
    team = newTables(),
    self = newTables(),
    sandbox = newTables(),
    status = newTables();

    public static Table[] newTables(){
        return new Table[]{new Table().bottom().left(), new Table().bottom().left()};
    }

    public static void init(){
        //welp since you can't just make a class as one of a method's inputs and run something from that class my code will be a few lines of duplicated code longer.
        Folding.add(folder);
        ui.hudGroup.addChild(folder[0]);
        ui.hudGroup.addChild(folder[1]);
    }
}
