package testing.util;

import arc.*;
import arc.scene.ui.layout.*;
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

    static Table buttons = newTable(), temp;
    static int rows = 3;

    public static TerrainPainterFragment terrainFrag;

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

    public static void add(TUButton button, Table table){
        float shift = 0;
        button.add(table);
        if(temp.getChildren().size > 0) shift = -4;
        temp.add(table).padLeft(shift);
    }

    public static void init(){
        TUVars.setDefaults();
        TUDialogs.load();
        buttons.setOrigin(Align.bottomLeft);

        //First row

        if(Vars.mobile && Core.settings.getBool("console")){
            row();
            add(new Console(), newTable());
        }else{
            rows--;
        }

        //Second row
        row();

        add(new Spawn(), newTable());
        add(new Sandbox(), newTable());

        //Third row
        row();

        add(new TeamChanger(), newTable());
        add(new Effect(), newTable());
        Death.init();
        add(new Death(), newTable());

        buttons.visible(() -> {
            if(!ui.hudfrag.shown || ui.minimapfrag.shown() || TestUtils.disableCampaign()) return false;
            if(!mobile) return true;

            return !(control.input instanceof MobileInput input) || input.lastSchematic == null || input.selectPlans.isEmpty();
        });
        ui.hudGroup.addChild(buttons);
        buttons.moveBy(0f, Scl.scl((mobile ? 46f : 0f) + TUVars.TCOffset));

        Table campaignKill = newTable();
        campaignKill.setOrigin(Align.bottomLeft);
        campaignKill.table(Tex.buttonEdge3, t -> {
            Death.seppuku(t).size(TUVars.iconSize, TUVars.iconSize);
        });
        campaignKill.visible(() -> {
            if(!ui.hudfrag.shown || ui.minimapfrag.shown() || (!TestUtils.disableCampaign() && state.isCampaign())) return false;
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
