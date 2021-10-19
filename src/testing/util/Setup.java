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

    /*public static <T extends TUButton> void add(T source, Table[] tables){
        source.add(tables);
        ui.hudGroup.addChild(tables[0]);
        ui.hudGroup.addChild(tables[1]);
    }*/

    public static void init(){
        //Yea I have no idea how to get this to work
        //add(Folding, folder);

        Folding.add(folder);
        ui.hudGroup.addChild(folder[0]);
        ui.hudGroup.addChild(folder[1]);
    }
}
