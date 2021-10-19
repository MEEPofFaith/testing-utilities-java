package testing.util;

import arc.scene.ui.layout.*;
import arc.util.*;
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
        Table t1 = new Table().bottom().left();
        t1.fillParent = true;
        t1.visibility = ButtonVisibility.unfoldedVisibility;

        Table t2 = new Table().bottom().left();
        t2.fillParent = true;
        t2.visibility = ButtonVisibility.foldedVisibility;

        return new Table[]{t1, t2};
    }

    public static void add(Table[] tables){
        ui.hudGroup.addChild(tables[0]);
        ui.hudGroup.addChild(tables[1]);
    }

    public static void init(){
        //welp since you can't just make a class as one of a method's inputs and run something from that class my code will be a few lines of duplicated code longer.
        Folding.add(folder);
        add(folder);

        TeamChanger.add(team);
        add(team);
    }
}
