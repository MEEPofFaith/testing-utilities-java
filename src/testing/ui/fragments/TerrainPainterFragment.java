package testing.ui.fragments;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
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
import mindustry.editor.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;
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
    /** If false, erase blocks. If true, erase floor overlays. */
    boolean eraseMode;
    int brushSize;
    private boolean initialized;
    float hold;
    private final Vec2[][] brushPolygons = new Vec2[MapEditor.brushSizes.length][0];
    private final Seq<Tile> newCliffTiles = new Seq<>();

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
                    Slider slider = new Slider(0, MapEditor.brushSizes.length - 1, 1, false);
                    slider.moved(f -> brushSize = (int)f);

                    var label = new Label("@editor.brush");
                    label.setAlignment(Align.center);
                    label.touchable = Touchable.disabled;

                    b.top().stack(slider, label);
                    b.row();

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
                            d, TUIcons.get(Icon.eraser), TUStyles.toggleCenteri, TUVars.buttonSize,
                            () -> {
                                erasing = !erasing;
                                drawing = false;
                            },
                            () -> "@tu-painter.erase",
                            "@tu-tooltip.painter-erase"
                        );
                        eb.update(() -> eb.setChecked(erasing));
                        Button mb = new Button(TUStyles.right);
                        ToggleStack modes = new ToggleStack(TUIcons.get(Icon.terrain), TUIcons.get(Icon.grid));
                        if(eraseMode) modes.swap();
                        modes.setSize(TUVars.buttonSize);
                        mb.add(modes).size(TUVars.buttonSize);
                        mb.clicked(() -> {
                            modes.swap();
                            eraseMode = !eraseMode;
                        });
                        TUElements.boxTooltip(mb, () -> eraseMode ? "@tu-tooltip.painter-erase-floors" : "@tu-tooltip.painter-erase-blocks");
                        d.add(mb);
                    }).padTop(6f).row();

                    b.table(c -> {
                        ImageButton rb = TUElements.imageButton(
                            c, TUIcons.get(Icon.rotate), TUStyles.lefti, TUVars.buttonSize,
                            () -> {
                                boolean wasDrawing = drawing;
                                boolean wasErasing = erasing;
                                reload();
                                show = true;
                                drawing = wasDrawing;
                                erasing = wasErasing;
                            },
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
                    }).padTop(6f);
                }).fillX();
            });
        });

        rebuild();

        if(!initialized){
            for(int i = 0; i < MapEditor.brushSizes.length; i++){
                float size = MapEditor.brushSizes[i];
                float mod = size % 1f;
                brushPolygons[i] = Geometry.pixelCircle(size, (index, x, y) -> Mathf.dst(x, y, index - mod, index - mod) <= size - 0.5f);
            }

            Events.run(Trigger.update, () -> {
                if(!state.isGame()){
                    show = drawing = erasing = false;
                }else if(!scene.hasMouse()){
                    if(!mobile && input.keyDown(KeyCode.mouseMiddle)){
                        int tx = World.toTile(input.mouseWorldX()), ty = World.toTile(input.mouseWorldY());
                        Tile tile = world.tile(tx, ty);
                        if(tile != null && !(tile.block() != Blocks.air && !terrainBlock(tile.block()))){
                            block = tile.block() == Blocks.air ? tile.overlay() == Blocks.air ? tile.floor() : tile.overlay() : tile.block();
                        }
                    }

                    if(drawing || erasing){
                        if(!TestUtils.click()){
                            hold = 0;
                            return;
                        }
                        player.shooting(false);

                        if(mobile){
                            hold += TUVars.delta();
                            if(hold < 2f * 60f) return;
                        }

                        int tx = World.toTile(input.mouseWorldX()), ty = World.toTile(input.mouseWorldY());

                        if(drawing){
                            if(block instanceof SteamVent){
                                paintSqure(tx, ty, 3, t -> {
                                    placeFloor(t.pos());
                                });
                            }else{
                                paintCircle(tx, ty, t -> {
                                    if(block.isOverlay()){
                                        placeOverlayFloor(t.pos());
                                    }else if(block.isFloor()){
                                        placeFloor(t.pos());
                                    }else{
                                        placeBlock(t.pos());
                                    }
                                });
                            }
                        }else if(erasing){
                            paintCircle(tx, ty, t -> {
                                erase(t.pos());
                            });
                        }
                    }
                }
            });

            Events.on(WorldLoadEvent.class, e -> {
                show = drawing = erasing = changed = false;
            });

            initialized = true;
        }
    }

    boolean terrainBlock(Block b){
        return b.isStatic() || b instanceof Prop || b instanceof TreeBlock || b instanceof TallBlock || b instanceof Cliff;
    }

    public void drawPos(){
        if((drawing || erasing) && state.isGame() && !scene.hasMouse()){
            int tx = World.toTile(input.mouseWorldX()), ty = World.toTile(input.mouseWorldY());
            float wx = tx * tilesize, wy = ty * tilesize;

            Draw.z(Layer.overlayUI);
            Lines.stroke(1f, drawing ? Pal.accent : Pal.remove);

            if(block instanceof SteamVent){
                float size = 3;
                float offset = (1 - size % 2) * tilesize / 2f;
                size *= tilesize;
                Lines.rect(wx - size / 2 + offset, wy - size / 2 + offset, size, size);
            }else{
                Lines.poly(brushPolygons[brushSize], wx - tilesize / 2, wy - tilesize / 2, tilesize);
            }
            Draw.rect(Icon.cancel.getRegion(), wx, wy, tilesize / 2f, tilesize / 2f);
        }
    }

    /** Taken from {@link MapEditor::drawCircle} */
    public void paintCircle(int x, int y, Cons<Tile> drawer){
        float bSize = MapEditor.brushSizes[brushSize];
        int clamped = (int)bSize;
        for(int rx = -clamped; rx <= clamped; rx++){
            for(int ry = -clamped; ry <= clamped; ry++){
                if(Mathf.within(rx, ry, bSize - 0.5f + 0.0001f)){
                    int wx = x + rx, wy = y + ry;

                    if(wx < 0 || wy < 0 || wx >= world.width() || wy >= world.height()){
                        continue;
                    }

                    drawer.get(world.tile(wx, wy));
                }
            }
        }
    }

    public void paintSqure(int x, int y, int size, Cons<Tile> drawer){
        for(int rx = 0; rx < size; rx++){
            for(int ry = 0; ry < size; ry++){
                int wx = x + rx - size/2, wy = y + ry - size/2;

                if(wx < 0 || wy < 0 || wx >= world.width() || wy >= world.height()){
                    continue;
                }

                drawer.get(world.tile(wx, wy));
            }
        }
    }

    void rebuild(){
        selection.clear();
        String text = search.getText();

        Seq<Block> array = content.blocks()
            .select(b ->
                (
                    b.isFloor() || b.isOverlay() || b.isStatic() ||
                    b instanceof Prop || b instanceof TreeBlock || b instanceof TallBlock || b instanceof Cliff
                ) &&
                !b.isAir() && (b.inEditor || b == Blocks.cliff) && b != Blocks.spawn &&
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
                    image.update(() -> image.color.lerp(listener.isOver() || block == b ? Color.white : Color.lightGray, Mathf.clamp(0.4f * TUVars.delta())));
                }else{
                    image.update(() -> image.color.lerp(block == b ? Color.white : Color.lightGray, Mathf.clamp(0.4f * TUVars.delta())));
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
        if(world.tile(pos) == null || world.tile(pos).floor() == block) return;

        world.tile(pos).setFloorUnder((Floor)block);
        changed = true;
    }

    void placeOverlayFloor(int pos){
        if(world.tile(pos) == null || world.tile(pos).overlay() == block) return;

        world.tile(pos).setOverlay(block);
        changed = true;
    }

    void placeBlock(int pos){
        Tile tile = world.tile(pos);
        if(tile == null || (block != Blocks.cliff && tile.block() == block)) return;

        tile.setBlock(block);
        changed = true;

        if(block == Blocks.cliff){
            tile.data = 1; //At least make them visible.
            newCliffTiles.add(tile);
        }else{
            tile.data = 0;
            newCliffTiles.remove(tile, true);
        }
    }

    void erase(int pos){
        if(world.tile(pos) == null) return;

        if(eraseMode){
            if(world.tile(pos).overlay() == Blocks.air) return;
            world.tile(pos).setOverlay(Blocks.air);
        }else{
            if(world.tile(pos).block() == Blocks.air) return;
            world.tile(pos).setBlock(Blocks.air);
        }
        changed = true;
    }

    void reload(){
        if(newCliffTiles.any()){
            for(Tile tile : newCliffTiles){
                if(!tile.block().isStatic() || tile.block() != Blocks.cliff) continue;

                int rotation = 0;
                for(int i = 0; i < 8; i++){
                    Tile other = world.tiles.get(tile.x + Geometry.d8[i].x, tile.y + Geometry.d8[i].y);
                    if(other != null && !other.block().isStatic()){
                        rotation |= (1 << i);
                    }
                }

                tile.data = (byte)rotation;
            }

            for(Tile tile : newCliffTiles){
                if(tile.block() == Blocks.cliff && tile.data == 0){
                    tile.setBlock(Blocks.air);
                }
            }

            newCliffTiles.clear();
        }
        if(changed){
            Events.fire(new WorldLoadEvent());
        }

        changed = false;
    }
}
