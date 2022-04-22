package testing.util;

import arc.*;
import arc.scene.ui.layout.*;
import mindustry.game.EventType.*;
import testing.buttons.*;
import testing.ui.*;

import static mindustry.Vars.*;

public class Setup{
    static boolean selfInit;

    static Table buttons = new Table().bottom().left();

    public static Table
    team = newTable(),
    death = newTable(),
    sandbox = newTable(),
    status = newTable(),
    units = newTable();

    public static Table newTable(){
        return new Table().bottom().left();
    }

    public static void add(Table table){
        buttons.addChild(table);
    }

    public static void init(){
        TUVars.setDefaults();
        TUDialogs.load();

        TeamChanger.add(team);
        add(team);

        Death.init();
        Death.add(death);
        add(death);

        Sandbox.add(sandbox);
        add(sandbox);

        StatusMenu.add(status);
        add(status);

        SpawnMenu.add(units);
        add(units);

        buttons.visibility = Visibility.buttonVisibility;
        ui.hudGroup.addChild(buttons);

        Events.on(WorldLoadEvent.class, e -> {
            if(!selfInit){
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
