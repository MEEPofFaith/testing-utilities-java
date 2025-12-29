package testing.util;

import arc.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import blui.ui.*;
import mindustry.*;
import mindustry.core.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.mod.Mods.*;
import mindustry.world.*;
import testing.*;
import testing.buttons.*;
import testing.ui.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class Setup{
    public static boolean posLabelAligned = false;

    public static TerrainPainterFragment terrainFrag;
    private static Table timeSlider;
    private static boolean tcOutdated = false;
    private static float startX = Float.MIN_VALUE, startY;

    public static void init(){
        TUDialogs.load();

        BLSetup.addTable(table -> {
            if(mobile && settings.getBool("console")){
                table.table(Tex.buttonEdge3, Console::addButtons);
                table.row();
            }
            table.table(Tex.buttonEdge3, t -> {
                Spawn.addButtons(t);
                Environment.worldButton(t);
                Effect.statusButton(t);
                Sandbox.addButtons(t);
            });
            table.row();

            boolean timeControl = timeControlEnabled();

            table.table(timeControl ? Tex.buttonEdge3 : Tex.pane, t -> {
                TeamChanger.addButton(t);
                Health.addButtons(t);
                Death.addButtons(t);
                LightSwitch.lightButton(t);
            });

            if(timeControl){
                table.row();
                table.add(yoinkTimeSlider());
            }
        }, () -> !net.client() && !TestUtils.disableCampaign());

        BLSetup.addTable(table -> {
            if(timeControlEnabled()){
                table.add(yoinkTimeSlider());
            }

            table.table(Tex.pane, Death::seppuku);
        }, () -> !net.client() && state.isCampaign() && TestUtils.disableCampaign());

        Table miniPos = ui.hudGroup.find("minimap/position");
        Label pos = miniPos.find("position");
        pos.setText(() -> {
            String playerPos = "";
            if(settings.getBool("position")){
                playerPos = player.tileX() + ", " + player.tileY() + "\n";
                if(settings.getBool("tu-wu-coords", true)){
                    playerPos += "[accent]" + fix(player.x) + ", " + fix(player.y) + "\n";
                }
            }

            int tx = World.toTile(Core.input.mouseWorldX()),
                ty = World.toTile(Core.input.mouseWorldY());

            String cursorPos = "";
            if(settings.getBool("mouseposition")){
                cursorPos = "[lightgray]" + tx + ", " + ty + "\n";
                if(settings.getBool("tu-wu-coords", true)){
                    cursorPos += "[#d4816b]" + fix(Core.input.mouseWorldX()) + ", " + fix(Core.input.mouseWorldY()) + "\n";
                }

                if(settings.getBool("tu-tile-info", false)){
                    Tile tile = world.tile(tx, ty);
                    cursorPos += "[#a9d8ff]";
                    if(tile == null){
                        cursorPos += "-----";
                    }else{
                        cursorPos += tile.floor().localizedName
                            + " | " + tile.overlay().localizedName
                            + " | " + tile.block().localizedName
                            + " | data = ";
                        StringBuilder data = new StringBuilder();
                        for(int i = 7; i >= 0; i--){
                            data.append((tile.data & (1 << i)) != 0 ? '1' : '0');
                        }
                        cursorPos += data;
                    }
                }
            }

            return playerPos + cursorPos;
        });
        miniPos.getCell(miniPos.find("minimap")).top().right();
        miniPos.getCell(pos).top().right();

        terrainFrag = new TerrainPainterFragment();
        Core.app.post(() -> { //Wait for BLUI to set up.
            terrainFrag.build(ui.hudGroup);
            setOffsetX(settings.getFloat("tu-offset-x"));
            setOffsetY(settings.getFloat("tu-offset-y"));
        });

        Events.on(WorldLoadEvent.class, e -> {
            if(posLabelAligned) return;
            pos.setAlignment(Align.right, Align.right);
            posLabelAligned = true;
        });
    }

    private static Table yoinkTimeSlider(){
        if(timeSlider == null){
            timeSlider = Vars.ui.hudGroup.find("tc-slidertable");

            if(timeSlider == null){
                timeSlider = new Table();
                tcOutdated = true;
                Vars.ui.showErrorMessage("@mod.tc-outdated");
                Log.err("@mod.tc-outdated");
                return timeSlider;
            }

            timeSlider.visible(() -> true);

            Vars.ui.hudGroup.find("tc-foldedtable").visible(() -> false);
        }
        return timeSlider;
    }

    public static boolean timeControlEnabled(){
        LoadedMod timeControl = Vars.mods.getMod("time-control");
        return !tcOutdated && timeControl != null && timeControl.isSupported() && timeControl.enabled();
    }

    private static float startX(){
        if(startX == Float.MIN_VALUE) startX = ui.hudGroup.find("blui").x;
        return startX;
    }

    private static float startY(){
        if(startY == Float.MIN_VALUE) startY = ui.hudGroup.find("blui").y;
        return startY;
    }

    public static void setOffsetX(float x){
        Table blui = ui.hudGroup.find("blui");
        blui.setPosition(startX() + x, blui.y);
    }

    public static void setOffsetY(float y){
        Table blui = ui.hudGroup.find("blui");
        blui.setPosition(blui.x, startY() + y);
    }

    private static String fix(float f){
        return Strings.autoFixed(f, 1);
    }
}
