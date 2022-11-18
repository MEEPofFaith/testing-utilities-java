package testing.ui.fragments;

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
import mindustry.core.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.blocks.legacy.*;
import testing.*;
import testing.ui.*;
import testing.util.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class TerrainPainterFragment{
    public boolean show = false;

    TextField search;
    Table selection = new Table();
    Block block = Blocks.boulder;
    boolean drawing, erasing, changed;
    private boolean initialized;
    float hold;

    public void build(Group parent){
        parent.fill(t -> {
            t.name = "menu";
            t.center().right().visible(() -> show);
            t.table(Tex.buttonSideLeft, all -> {
                all.table(s -> {
                    s.image(Icon.zoom).padRight(8);
                    search = s.field(null, text -> rebuild()).growX().get();
                    search.setMessageText("@players.search");
                }).fillX().padBottom(4).row();

                all.label(() -> "[accent]" + block.localizedName).padBottom(6).row();

                int rows = 6;
                float h = rows * (4 * 8) + (rows - 1) * 6 + 2 * 3;
                all.pane(sel -> {
                    sel.top();
                    sel.add(selection);
                }).fillX().padBottom(4).height(h);
                all.row();

                all.table(b -> {
                    b.table(d -> {
                        ImageButton db = TUElements.imageButton(
                            d, TUIcons.get(Icon.pencil), TUStyles.toggleLefti, TUVars.buttonSize,
                            () -> {
                                drawing = !drawing;
                                erasing = false;
                            },
                            () -> "@tu-painter.draw",
                            "@tu-tooltip.painter-draw"
                        );
                        db.update(() -> db.setChecked(drawing));
                        ImageButton eb = TUElements.imageButton(
                            d, TUIcons.get(Icon.eraser), TUStyles.toggleRighti, TUVars.buttonSize,
                            () -> {
                                erasing = !erasing;
                                drawing = false;
                            },
                            () -> "@tu-painter.erase",
                            "@tu-tooltip.painter-erase"
                        );
                        eb.update(() -> eb.setChecked(erasing));
                    }).padTop(6f).row();

                    b.table(c -> {
                        ImageButton rb = TUElements.imageButton(
                            c, TUIcons.get(Icon.rotate), TUStyles.lefti, TUVars.buttonSize,
                            this::reload,
                            () -> "@tu-painter.reload",
                            "@tu-tooltip.painter-reload"
                        );
                        rb.setDisabled(() -> !changed);

                        TUElements.imageButton(
                            c, TUIcons.get(Icon.left), TUStyles.righti, TUVars.buttonSize,
                            () -> {
                                show = false;
                                drawing = false;
                                erasing = false;
                                reload();
                            },
                            () -> "@close",
                            "@tu-tooltip.painter-close"
                        );
                    });
                }).fillX();
            });
        });

        rebuild();

        if(!initialized){
            Events.run(Trigger.update, () -> {
                if(!state.isGame()){
                    show = drawing = erasing = false;
                }else if(!scene.hasMouse()){
                    if(!TestUtils.click()){
                        hold = 0;
                        return;
                    }

                    if(mobile){
                        hold += Time.delta;
                        if(hold < 5f * 60f) return;
                    }

                    if(drawing){
                        int pos = Point2.pack(World.toTile(input.mouseWorldX()), World.toTile(input.mouseWorldY()));
                        if(block.isOverlay()){
                            placeOverlayFloor(pos);
                        }else if(block.isFloor()){
                            placeFloor(pos);
                        }else{
                            placeBlock(pos);
                        }
                    }else if(erasing){
                        int pos = Point2.pack(World.toTile(input.mouseWorldX()), World.toTile(input.mouseWorldY()));
                        erase(pos);
                    }
                }
            });

            initialized = true;
        }
    }

    void rebuild(){
        selection.clear();
        String text = search.getText();

        Seq<Block> array = content.blocks()
            .select(b ->
                (
                    b.isFloor() || b.isOverlay() || b.isStatic() ||
                    b instanceof Prop || b instanceof TreeBlock || b instanceof TallBlock
                ) &&
                !b.isAir() && b.inEditor && b != Blocks.spawn && b != Blocks.empty &&
                (!b.isHidden() || settings.getBool("tu-show-hidden")) &&
                (text.isEmpty() || b.localizedName.toLowerCase().contains(text.toLowerCase())));
        if(array.size == 0) return;

        selection.table(list -> {
            list.left();

            int cols = 6;
            int count = 0;

            for(Block b : array){
                Image image = new Image(b.uiIcon).setScaling(Scaling.fit);
                list.add(image).size(8 * 4).pad(3);

                ClickListener listener = new ClickListener();
                image.addListener(listener);
                if(!mobile){
                    image.addListener(new HandCursorListener());
                    image.update(() -> image.color.lerp(listener.isOver() || block == b ? Color.white : Color.lightGray, Mathf.clamp(0.4f * Time.delta)));
                }else{
                    image.update(() -> image.color.lerp(block == b ? Color.white : Color.lightGray, Mathf.clamp(0.4f * Time.delta)));
                }

                image.clicked(() -> {
                    if(input.keyDown(KeyCode.shiftLeft) && Fonts.getUnicode(b.name) != 0){
                        app.setClipboardText((char)Fonts.getUnicode(b.name) + "");
                        ui.showInfoFade("@copied");
                    }else{
                        block = b;
                    }
                });
                TUElements.boxTooltip(image, b.localizedName);

                if((++count) % cols == 0){
                    list.row();
                }
            }
        }).growX().left().padBottom(10);
    }

    void placeFloor(int pos){
        //TODO if(block instanceof SteamVent){place 3x3}
        if(world.tile(pos).floor() == block) return;

        if(net.client()){
            int overlay = world.tile(pos).overlayID();

            Utils.runCommand("Vars.world.tile(" + pos + ").setFloorNet(Vars.content.block(" + block.id + "))");
            Utils.runCommand("Vars.world.tile(" + pos + ").setOverlayNet(Vars.content.block(" + overlay + "))");
        }else{
            world.tile(pos).setFloorUnder((Floor)block);
        }
        changed = true;
    }

    void placeOverlayFloor(int pos){
        if(world.tile(pos).overlay() == block) return;

        if(net.client()){
            Utils.runCommand("Vars.world.tile(" + pos + ").setOverlayNet(Vars.content.block(" + block.id + "))");
        }else{
            world.tile(pos).setOverlayNet(block);
        }
        changed = true;
    }

    void placeBlock(int pos){
        if(world.tile(pos).block() == block) return;

        if(net.client()){
            Utils.runCommand("Vars.world.tile(" + pos + ").setNet(Vars.content.block(" + block.id + "))");
        }else{
            world.tile(pos).setNet(block);
        }
        changed = true;
    }

    void erase(int pos){
        if(world.tile(pos).overlay() == Blocks.air && world.tile(pos).block() == Blocks.air) return;

        if(net.client()){
            Utils.runCommand("Vars.world.tile(" + pos + ").setOverlayNet(Blocks.air)");
            Utils.runCommand("Vars.world.tile(" + pos + ").setNet(Blocks.air)");
        }else{
            world.tile(pos).setOverlayNet(Blocks.air);
            world.tile(pos).setNet(Blocks.air);
        }
        changed = true;
    }

    void reload(){
        if(changed) Events.fire(new WorldLoadEvent());

        changed = false;
    }
}
