package testing.util;

import arc.*;
import arc.scene.ui.layout.*;
import mindustry.game.EventType.*;
import testing.buttons.*;

import static mindustry.Vars.*;

public class Setup{
    static boolean selfInit;

    static Table
        unfolded = new Table().bottom().left(),
        folded = new Table().bottom().left();

    public static Table[]

    folder = newTables(),
    team = newTables(),
    death = newTables(),
    sandbox = newTables(),
    status = newTables(),
    units = newTables();

    public static Table[] newTables(){
        return new Table[]{new Table().bottom().left(), new Table().bottom().left()};
    }

    public static void add(Table[] tables){
        unfolded.addChild(tables[0]);
        folded.addChild(tables[1]);
    }

    public static void init(){
        TUVars.setDefaults();

        Folding.add(folder);
        add(folder);

        TeamChanger.add(team);
        add(team);

        Death.init();
        Death.add(death);
        add(death);

        Sandbox.add(sandbox);
        add(sandbox);

        StatusMenu.init();
        StatusMenu.add(status);
        add(status);

        UnitMenu.init();
        UnitMenu.add(units);
        add(units);

        unfolded.visibility = Visibility.unfoldedVisibility;
        ui.hudGroup.addChild(unfolded);
        folded.visibility = Visibility.foldedVisibility;
        ui.hudGroup.addChild(folded);

        Events.on(WorldLoadEvent.class, e -> {
            if(!selfInit){
                //lmao
                Table healthUI = placement();
                healthUI.row();
                Health.healing(healthUI).size(96, 40).color(TUVars.curTeam.color).pad(0).left().padLeft(4);
                Health.invincibility(healthUI).size(164, 40).color(TUVars.curTeam.color).pad(0).left().padLeft(-20);
                selfInit = true;
            }
        });
    }

    public static Table placement(){
        return ui.hudGroup
            .<Table>find("overlaymarker")
            .<Stack>find("waves/editor")
            .<Table>find("waves")
            .find("status");
    }
}