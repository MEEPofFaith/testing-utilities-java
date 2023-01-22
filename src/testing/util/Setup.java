package testing.util;

import arc.*;
import arc.scene.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.input.*;
import testing.*;
import testing.buttons.*;
import testing.ui.*;
import testing.ui.fragments.*;

import static mindustry.Vars.*;

public class Setup{
    static boolean selfInit;

    static Table temp;

    public static TerrainPainterFragment terrainFrag;

    public static Table newTable(){
        return new Table().bottom().left();
    }

    public static void row(Table table){
        table.row();
        table.table(t -> {
            temp = t;
        }).left();

        Seq<Cell> cells = table.getCells();
        int[] row = {cells.size};
        cells.each(c -> {
            if(row[0] > 1) c.padBottom(-4f);
            row[0]--;
        });
    }

    public static void add(TUButton button, Table table){
        float shift = 0;
        button.add(table);
        if(temp.getChildren().size > 0) shift = -4;
        temp.add(table).padLeft(shift);
    }

    public static void init(){
        TUVars.setDefaults();
        TUDialogs.load();

        //Build normal UI.
        Table buttons = newTable();
        buttons.setOrigin(Align.bottomLeft);

        ///First row

        if(Vars.mobile && Core.settings.getBool("console")){
            row(buttons);
            add(new Console(), newTable());
        }

        ///Second row
        row(buttons);

        add(new Spawn(), newTable());
        add(new Sandbox(), newTable());

        ///Third row
        row(buttons);

        add(new TeamChanger(), newTable());
        add(new Effect(), newTable());
        Death.init();
        add(new Death(), newTable());

        //Normal UI
        buttons.visible(() -> {
            if(!ui.hudfrag.shown || ui.minimapfrag.shown() || TestUtils.disableCampaign()) return false;
            if(!mobile) return true;

            return !(control.input instanceof MobileInput input) || input.lastSchematic == null || input.selectPlans.isEmpty();
        });
        ui.hudGroup.addChild(buttons);
        buttons.moveBy(0f, Scl.scl((mobile ? 46f : 0f) + TUVars.TCOffset));

        //Campaign UI. Only has the kill button.
        Table campaignKill = newTable();
        campaignKill.setOrigin(Align.bottomLeft);
        campaignKill.table(Tex.buttonEdge3, t -> {
            Death.seppuku(t).size(TUVars.iconSize, TUVars.iconSize);
        });
        campaignKill.visible(() -> {
            if(!ui.hudfrag.shown || ui.minimapfrag.shown() || !state.isCampaign() || buttons.visible) return false;
            if(!mobile) return true;

            return !(control.input instanceof MobileInput input) || input.lastSchematic == null || input.selectPlans.isEmpty();
        });
        ui.hudGroup.addChild(campaignKill);
        campaignKill.moveBy(0f, Scl.scl((mobile ? 46f : 0f) + TUVars.TCOffset));

        terrainFrag = new TerrainPainterFragment();
        terrainFrag.build(ui.hudGroup);

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
