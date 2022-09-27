package testing.util;

import arc.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.*;
import mindustry.game.EventType.*;
import mindustry.input.*;
import testing.buttons.*;
import testing.ui.*;

import static mindustry.Vars.*;

public class Setup{
    static boolean selfInit;

    static Table buttons = newTable(), temp;
    static int rows = 3;

    public static Table
    team = newTable(),
    death = newTable(),
    sandbox = newTable(),
    status = newTable(),
    units = newTable(),
    console = newTable(),
    fields = newTable();

    public static Table newTable(){
        return new Table().bottom().left();
    }

    public static void row(){
        buttons.row();
        buttons.table(t -> {
            if(rows > 1) t.defaults().padBottom(-4);
            temp = t;
        }).left();
        rows--;
    }

    public static void add(Table table){
        float shift = 0;
        if(temp.getChildren().size > 0) shift = -4;
        temp.add(table).padLeft(shift);
    }

    public static void init(){
        TUVars.setDefaults();
        TUDialogs.load();
        buttons.setOrigin(Align.bottomLeft);

        //First row
        row();

        if(Vars.mobile && Core.settings.getBool("console")){
            Console.add(console);
            add(console);
        }

        //Second row
        row();

        Spawn.add(units);
        add(units);

        Sandbox.add(sandbox);
        add(sandbox);

        //Third row
        row();

        TeamChanger.add(team);
        add(team);

        if(Core.settings.getBool("tu-field-editor")){
            Fields.add(fields);
            add(fields);
        }

        Statuses.add(status);
        add(status);

        Death.init();
        Death.add(death);
        add(death);

        buttons.visible(() -> {
            if(!ui.hudfrag.shown || ui.minimapfrag.shown()) return false;
            if(!mobile) return true;

            return !(control.input instanceof MobileInput input) || input.lastSchematic == null || input.selectPlans.isEmpty();
        });
        ui.hudGroup.addChild(buttons);
        buttons.moveBy(0f, Scl.scl((mobile ? 46f : 0f) + TUVars.TCOffset));

        Events.on(WorldLoadEvent.class, e -> {
            if(!selfInit){
                Table healthUI = placement();
                healthUI.row();
                Health.healing(healthUI).height(TUVars.iconSize).color(TUVars.curTeam.color).pad(0).left().padLeft(4);
                Health.invincibility(healthUI).height(TUVars.iconSize).color(TUVars.curTeam.color).pad(0).left().padLeft(-20);
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
