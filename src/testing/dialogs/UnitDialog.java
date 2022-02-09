package testing.dialogs;

import arc.*;
import arc.graphics.*;
import arc.input.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.*;
import arc.scene.event.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.game.EventType.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.*;
import testing.ui.*;
import testing.util.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class UnitDialog extends BaseDialog{
    TeamDialog teamDialog;

    TextField search;
    Table all = new Table();
    UnitType spawnUnit = UnitTypes.dagger;
    Vec2 spawnPos = new Vec2();
    int amount = 1;
    float radius = 2;
    static boolean despawns = true, initialized;

    boolean expectingPos;

    final int maxAmount = 100;
    final float minRadius = 0.125f, maxRadius = 10f;

    public UnitDialog(){
        super("@tu-unit-menu.name");
        teamDialog = new TeamDialog();

        shouldPause = false;
        addCloseButton();
        shown(this::rebuild);
        onResize(this::rebuild);
        despawns = Core.settings.getBool("tu-despawns", true);

        all.margin(20).marginTop(0f);

        cont.table(s -> {
            s.image(Icon.zoom).padRight(8);
            search = s.field(null, text -> rebuild()).growX().get();
            search.setMessageText("@players.search");
        }).fillX().padBottom(4).row();

        cont.pane(all);

        if(!initialized){
            Events.run(Trigger.update, () -> {
                if(expectingPos){
                    if(!state.isGame()){
                        expectingPos = false;
                    }else if(input.justTouched()){
                        if(!scene.hasMouse()){
                            spawnPos.set(Mathf.round(input.mouseWorld().x), Mathf.round(input.mouseWorld().y));
                            ui.showInfoToast(bundle.format("tu-unit-menu.setpos", spawnPos.x / 8f, spawnPos.y / 8f), 4f);
                            show();
                        }else{
                            ui.showInfoToast("@tu-unit-menu.cancel", 4f);
                        }
                        expectingPos = false;
                    }
                }
            });
            initialized = true;
        }
    }

    void rebuild(){
        expectingPos = false;
        all.clear();
        String text = search.getText();

        all.label(
            () -> bundle.get("tu-menu.selection") + spawnUnit.localizedName
        ).padBottom(6);
        all.row();

        Seq<UnitType> array = content.units().select(e -> e != UnitTypes.block && !e.isHidden() && (text.isEmpty() || e.localizedName.toLowerCase().contains(text.toLowerCase())));
        all.table(list -> {
            list.left();

            float iconMul = 1.5f;
            int cols = (int)Mathf.clamp((graphics.getWidth() - Scl.scl(30)) / Scl.scl(32 + 10) / iconMul, 1, 22 / iconMul);
            int count = 0;

            for(UnitType u : array){
                Image image = new Image(u.uiIcon).setScaling(Scaling.fit);
                list.add(image).size(8 * 4 * iconMul).pad(3);

                ClickListener listener = new ClickListener();
                image.addListener(listener);
                if(!mobile){
                    image.addListener(new HandCursorListener());
                    image.update(() -> image.color.lerp(!listener.isOver() ? Color.lightGray : Color.white, Mathf.clamp(0.4f * Time.delta)));
                }

                image.clicked(() -> {
                    if(input.keyDown(KeyCode.shiftLeft) && Fonts.getUnicode(u.name) != 0){
                        app.setClipboardText((char)Fonts.getUnicode(u.name) + "");
                        ui.showInfoFade("@copied");
                    }else{
                        spawnUnit = u;
                    }
                });
                image.addListener(new Tooltip(t -> t.background(Tex.button).add(u.localizedName)));

                if((++count) % cols == 0){
                    list.row();
                }
            }
        }).growX().left().padBottom(10);
        all.row();

        all.table(t -> {
            TextField aField = new TextField(String.valueOf(amount));
            t.slider(1, maxAmount, 1, amount, n -> {
                amount = (int)n;
                aField.setText(String.valueOf(n));
            }).right();
            t.add("@tu-unit-menu.amount").left().padLeft(6);
            t.add(aField).left().padLeft(6);
            aField.changed(() -> {
                if(aField.isValid()){
                    String s = Utils.extractNumber(aField.getText());
                    if(!s.isEmpty()){
                        amount = Integer.parseInt(s);
                    }
                }
            });
            aField.update(() -> {
                Scene stage = aField.getScene();
                if(!(stage != null && stage.getKeyboardFocus() == aField))
                    aField.setText(String.valueOf(amount));
            });

            t.row();

            TextField rField = new TextField(String.valueOf(radius));
            t.slider(minRadius, maxRadius, 1, radius, n -> {
                radius = n;
                rField.setText(String.valueOf(n));
            }).right();
            t.add("@tu-unit-menu.radius").left().padLeft(6);
            t.add(rField).left().padLeft(6);
            rField.changed(() -> {
                if(rField.isValid()){
                    String s = Utils.extractNumber(rField.getText());
                    if(!s.isEmpty()){
                        radius = Float.parseFloat(s);
                    }
                }
            });
            rField.update(() -> {
                Scene stage = rField.getScene();
                if(!(stage != null && stage.getKeyboardFocus() == rField))
                    rField.setText(String.valueOf(radius));
            });
        });
        all.row();

        all.table(t -> {
            t.button(Icon.defense, 32, teamDialog::show).get()
                .label(() -> bundle.format("tu-unit-menu.team-set", "[#" + spawnTeam().color + "]" + teamName() + "[]")).padLeft(6).growX();
            t.button(Icon.map, 32, () -> {
                hide();
                expectingPos = true;
            }).padLeft(6).get().label(() -> bundle.format("tu-unit-menu.pos", spawnPos.x / 8f, spawnPos.y / 8f)).padLeft(6).growX();
        }).padTop(6);
        all.row();

        all.table(b -> {
            ImageButton ib = b.button(Icon.units, TUStyles.lefti, 32, () -> {
                if(spawnUnit.constructor.get().canPass(player.tileX(), player.tileY())){
                    //For some reason spider units also return false even though they can stand on blocks.
                    transform();
                }else{
                    ui.showInfoToast("@tu-unit-menu.canttransform", 4f);
                }
            }).get();
            ib.setDisabled(() -> player.unit().type == UnitTypes.block);
            ib.label(() -> "@tu-unit-menu.transform").padLeft(6).growX();

            ImageButton db = b.button(TUIcons.shard, TUStyles.toggleRighti, 32, () -> despawns = !despawns).growX().get();
            db.update(() -> db.setChecked(despawns));
            db.label(() -> "@tu-unit-menu.despawns").padLeft(6).growX();
        }).padTop(6);

        all.row();
        all.button(Icon.add, 32, this::spawn).padTop(6).get()
            .label(() -> "@tu-unit-menu." + (amount != 1 ? "spawnplural" : "spawn")).padLeft(6).growX();
    }

    void spawn(){
        if(Utils.noCheat()){
            if(net.client())
                Utils.runCommand("let tempUnit = Vars.content.units().find(b => b.name === \"" + Utils.fixQuotes(spawnUnit.name) + "\")");
            for(int i = 0; i < amount; i++){
                float r = radius * tilesize * Mathf.sqrt(Mathf.random());
                Tmp.v1.setToRandomDirection().setLength(r).add(spawnPos);
                if(net.client()){
                    Utils.runCommand("tempUnit.spawn(Team.get(" + spawnTeam().id + "), " + Tmp.v1.x + ", " + Tmp.v1.y + ")");
                }else{
                    spawnUnit.spawn(spawnTeam(), Tmp.v1);
                }
            }
        }
    }

    void transform(){
        if(Utils.noCheat()){
            if(net.client()){
                Utils.runCommand("let tempUnit = Vars.content.units().find(b => b.name === \"" + Utils.fixQuotes(spawnUnit.name) + "\")");
                Utils.runCommandPlayer(
                    //Don't use let/var/etc., it breaks the command and makes nothing happen on use. Idk why, it makes no sense. I need a js expert for this qmelz help.
                    "spawned = tempUnit.spawn(p.team(), p.x, p.y); " +
                    //These don't work. I assume that trying to modify parts of the unit just straight up break.
                    //Because uncommenting either of these makes nothing happen in game when used.
                    //"spawned.spawnedByCore = " + despawns + "; " +
                    //"spawned.rotation = p.unit().rotation; " +
                    "Call.unitControl(p, spawned);"
                );
            }else if(player.unit() != null){
                Unit u = spawnUnit.spawn(player.team(), player);
                float rot = player.unit().rotation;
                u.controller(player);
                u.rotation(rot);
                u.spawnedByCore(despawns);
                Fx.unitControl.at(u, true);
            }
            hide();
        }
    }

    Team spawnTeam(){
        return teamDialog.getTeam();
    }

    String teamName(){
        return teamDialog.teamName();
    }

    public UnitType getUnit(){
        return spawnUnit;
    }
}
