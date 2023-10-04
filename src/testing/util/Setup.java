package testing.util;

import arc.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.core.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.input.*;
import mindustry.mod.Mods.*;
import testing.*;
import testing.buttons.*;
import testing.ui.*;

import static mindustry.Vars.*;

public class Setup{
    public static boolean on2r2t, posLabelAligned = false;
    private static Table temp;

    public static TerrainPainterFragment terrainFrag;

    public static Table newTable(){
        Table table = new Table().bottom().left();
        table.table(Tex.buttonEdge3, t -> {
            t.defaults().size(TUVars.iconSize, TUVars.iconSize);
            temp = t;
        }).left();
        return table;
    }

    public static void row(Table table){
        table.row();
        table.table(Tex.buttonEdge3, t -> {
            t.defaults().size(TUVars.iconSize, TUVars.iconSize);
            temp = t;
        }).left();

        Seq<Cell> cells = table.getCells();
        int[] row = {cells.size};
        cells.each(c -> {
            if(row[0] > 1) c.padBottom(-4f);
            row[0]--;
        });
    }

    public static void offset(Table table){
        LoadedMod timeControl = Vars.mods.getMod("time-control");
        table.moveBy(0f, Scl.scl(
            (mobile ? 46f : 0f) + //Account for command mode button on mobile.
            (timeControl != null && timeControl.isSupported() && timeControl.enabled() ? 68 : 0) //Account for sk7725/timecontrol being enabled.
        ));
    }

    public static void init(){
        TUVars.setDefaults();
        TUDialogs.load();
        Events.on(ClientServerConnectEvent.class, e -> {
            //Log.info("Checking if you're joining 2r2t...");
            //Log.info("ip: @ | port: @", e.ip, e.port);

            on2r2t = (e.ip.equals("130.61.214.19") || e.ip.equals("n1.yeet.ml")) && e.port == 6568;
        });

        //Build normal UI.
        Table mainButtons = newTable();
        mainButtons.setOrigin(Align.bottomLeft);

        ///First row
        if(Vars.mobile && Core.settings.getBool("console")){
            Console.addButtons(temp);

            row(mainButtons);
        }

        ///Second row
        Spawn.addButtons(temp);
        Environment.worldButton(temp);
        Effect.statusButton(temp);
        Sandbox.addButtons(temp);

        ///Third row
        row(mainButtons);

        TeamChanger.addButton(temp);
        Health.addButtons(temp);
        Death.init();
        Death.addButtons(temp);

        //Normal UI
        mainButtons.visible(() -> {
            if(net.client() || TestUtils.disableCampaign()) return false;
            return buttonVisibility();
        });
        ui.hudGroup.addChild(mainButtons);
        offset(mainButtons);

        //Campaign UI. Only has the kill button.
        Table campaignKill = newTable();
        Death.seppuku(temp);
        campaignKill.visible(() -> {
            if(net.client() || !state.isCampaign() || mainButtons.visible) return false;
            return buttonVisibility();
        });
        ui.hudGroup.addChild(campaignKill);
        offset(campaignKill);

        //2r2t UI.
        Table commandButtons = newTable();
        //TeamChanger.addButton(temp);
        Spawn.unitMenu(temp);
        //Spawn.placeCore(temp);
        Effect.statusButton(temp);
        Health.addButtons(temp);
        Death.seppuku(temp);
        commandButtons.visible(() -> {
            if(!net.client() || !on2r2t) return false;
            return buttonVisibility();
        });
        ui.hudGroup.addChild(commandButtons);
        offset(commandButtons);

        Table miniPos = ui.hudGroup.find("minimap/position");
        Label pos = miniPos.find("position");
        pos.setText(() ->
            (Core.settings.getBool("position") ?
                player.tileX() + ", " + player.tileY() + "\n" +
                (Core.settings.getBool("tu-wu-coords", true) ? "[accent]" + fix(player.x) + ", " + fix(player.y) + "\n" : "") :
                ""
            ) +
            (Core.settings.getBool("mouseposition") ?
                "[lightgray]" + World.toTile(Core.input.mouseWorldX()) + ", " + World.toTile(Core.input.mouseWorldY()) + "\n" +
                (Core.settings.getBool("tu-wu-coords", true) ? "[#d4816b]" + fix(Core.input.mouseWorldX()) + ", " + fix(Core.input.mouseWorldY()) : "") : //accentBack is not an indexed color for [] format
                ""
            )
        );
        miniPos.getCell(miniPos.find("minimap")).top().right();
        miniPos.getCell(pos).top().right();

        terrainFrag = new TerrainPainterFragment();
        terrainFrag.build(ui.hudGroup);

        Events.on(WorldLoadEvent.class, e -> {
            if(posLabelAligned) return;
            pos.setAlignment(Align.right, Align.right);
            posLabelAligned = true;
        });
    }

    public static boolean buttonVisibility(){
        if(!ui.hudfrag.shown || ui.minimapfrag.shown()) return false;
        if(!mobile) return true;

        return !(control.input instanceof MobileInput input) || input.lastSchematic == null || input.selectPlans.isEmpty();
    }

    private static String fix(float f){
        return Strings.autoFixed(f, 1);
    }
}
