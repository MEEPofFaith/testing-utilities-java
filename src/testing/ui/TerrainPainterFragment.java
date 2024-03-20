package testing.ui;

import arc.*;
import arc.func.*;
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
import mindustry.editor.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.blocks.legacy.*;
import testing.editor.*;
import testing.util.*;

import static arc.Core.*;
import static mindustry.Vars.*;
import static testing.ui.TUDialogs.*;
import static testing.util.TUVars.*;

public class TerrainPainterFragment{
    TextField search;
    Table selection = new Table();
    private boolean show = false;
    private boolean buildings = false;

    public void build(Group parent){
        Boolp visibility = () -> show && !ui.minimapfrag.shown();

        parent.fill(t -> {
            t.name = "terrainpainterselection";
            t.bottom().right().visible(visibility);
            t.table(Tex.buttonEdge1, all -> {
                all.table(s -> {
                    s.image(Icon.zoom).padRight(8);
                    search = s.field(null, text -> rebuild()).growX().get();
                    search.setMessageText("@players.search");
                    s.button(TUIcons.get(Icon.crafting), Styles.squareTogglei, buttonSize, () -> {
                        buildings = !buildings;
                        rebuild();
                    }).checked(b -> buildings).size(iconSize);
                }).fillX().padBottom(4);

                all.row();
                all.label(() -> "[accent]" + painter.drawBlock.localizedName).padBottom(4).row();

                int rows = 9;
                float h = rows * (4 * 8) + (rows - 1) * 6 + 2 * 3;
                all.pane(sel -> {
                    sel.top();
                    sel.add(selection);
                }).fillX().height(h);
            });
        });

        parent.fill(t -> {
            t.name = "terrainpaintermenu";
            t.bottom().left().visible(visibility);
            t.setFillParent(false);
            t.table(Tex.buttonEdge3, all -> {
                all.table(tools -> {
                    //From MapEditorDialog
                    ButtonGroup<ImageButton> group = new ButtonGroup<>();
                    Table[] lastTable = {null};

                    Cons<PainterTool> addTool = tool -> {

                        ImageButton button = new ImageButton(TUIcons.get(ui.getIcon(tool.name())), Styles.squareTogglei);
                        button.clicked(() -> {
                            paintbrush.setTool(tool);
                            if(lastTable[0] != null){
                                lastTable[0].remove();
                            }
                        });
                        button.update(() -> button.setChecked(paintbrush.getTool() == tool));
                        group.add(button);

                        if(tool.altModes.length > 0){
                            button.clicked(l -> {
                                if(!mobile){
                                    //desktop: rightclick
                                    l.setButton(KeyCode.mouseRight);
                                }
                            }, e -> {
                                //need to double tap
                                if(mobile && e.getTapCount() < 2){
                                    return;
                                }

                                if(lastTable[0] != null){
                                    lastTable[0].remove();
                                }

                                Table table = new Table(Styles.black9);
                                table.defaults().size(300f, 70f);

                                for(int i = 0; i < tool.altModes.length; i++){
                                    int mode = i;
                                    String name = tool.altModes[i];

                                    table.button(b -> {
                                        b.left();
                                        b.marginLeft(6);
                                        b.setStyle(Styles.flatTogglet);
                                        b.add(bundle.get("toolmode." + name)).left();
                                        b.row();
                                        b.add(bundle.get("toolmode." + name + ".description")).color(Color.lightGray).left();
                                    }, () -> {
                                        tool.mode = (tool.mode == mode ? -1 : mode);
                                        table.remove();
                                    }).update(b -> b.setChecked(tool.mode == mode));
                                    table.row();
                                }

                                table.update(() -> {
                                    Vec2 v = button.localToStageCoordinates(Tmp.v1.setZero());
                                    table.setPosition(v.x, v.y, Align.topLeft);
                                    if(!shown()){
                                        table.remove();
                                        lastTable[0] = null;
                                    }
                                });

                                table.pack();
                                table.act(graphics.getDeltaTime());

                                parent.addChild(table);
                                lastTable[0] = table;
                            });
                        }


                        Label mode = new Label("");
                        mode.setColor(Pal.remove);
                        mode.update(() -> mode.setText(tool.mode == -1 ? "" : "M" + (tool.mode + 1) + " "));
                        mode.setAlignment(Align.bottomRight, Align.bottomRight);
                        mode.touchable = Touchable.disabled;

                        tools.stack(button, mode);
                    };

                    tools.defaults().size(iconSize);

                    ImageButton undo = tools.button(Icon.undo, Styles.flati, painter::undo).get();
                    ImageButton redo = tools.button(Icon.redo, Styles.flati, painter::redo).get();

                    undo.setDisabled(() -> !painter.canUndo());
                    redo.setDisabled(() -> !painter.canRedo());

                    undo.update(() -> undo.getImage().setColor(undo.isDisabled() ? Color.gray : Color.white));
                    redo.update(() -> redo.getImage().setColor(redo.isDisabled() ? Color.gray : Color.white));

                    addTool.get(PainterTool.pick);

                    ImageButton grid = tools.button(Icon.grid, Styles.squareTogglei, () -> paintbrush.drawGrid = !paintbrush.drawGrid).get();
                    grid.getStyle().down = Styles.flatOver;
                    grid.update(() -> grid.setChecked(paintbrush.drawGrid));

                    ImageButton rotate = tools.button(Icon.right, Styles.flati, () -> painter.rotation = (painter.rotation + 1) % 4).get();
                    rotate.getImage().update(() -> {
                        rotate.getImage().setRotation(painter.rotation * 90);
                        rotate.getImage().setOrigin(Align.center);
                    });

                    tools.row();

                    addTool.get(PainterTool.line);
                    addTool.get(PainterTool.pencil);
                    addTool.get(PainterTool.eraser);
                    addTool.get(PainterTool.fill);
                    addTool.get(PainterTool.spray);
                });

                all.row();

                TUElements.imageButton(
                    all, TUIcons.get(Icon.defense), Styles.defaulti, buttonSize,
                    () -> teamDialog.show(painter.drawTeam, team -> painter.drawTeam = team),
                    () -> bundle.format("tu-unit-menu.set-team", "[#" + painter.drawTeam.color + "]" + teamDialog.teamName(painter.drawTeam) + "[]"),
                    "@tu-tooltip.block-set-team"
                ).padTop(4f);

                all.row();

                Slider slider = new Slider(0, MapEditor.brushSizes.length - 1, 1, false);
                slider.moved(f -> painter.brushSize = MapEditor.brushSizes[(int)f]);
                for(int j = 0; j < MapEditor.brushSizes.length; j++){
                    if(MapEditor.brushSizes[j] == painter.brushSize){
                        slider.setValue(j);
                    }
                }

                var label = new Label("@editor.brush");
                label.setAlignment(Align.center);
                label.touchable = Touchable.disabled;

                all.stack(slider, label).width(sliderWidth).padTop(4f);
                all.row();

                TUElements.imageButton(
                    all, TUIcons.get(Icon.terrain), Styles.defaulti, buttonSize,
                    () -> painter.flushCliffs(),
                    () -> "@tu-painter.cliffs",
                    "@tu-tooltip.painter-cliffs"
                ).padTop(4f).disabled(b -> painter.pendingCliffs.isEmpty());

                all.row();

                TUElements.imageButton(
                    all, TUIcons.get(Icon.left), Styles.defaulti, buttonSize,
                    this::hide,
                    () -> "@close",
                    "@tu-tooltip.painter-close"
                ).padTop(4f);
            });
        });

        rebuild();

        Events.run(Trigger.draw, () -> {
            if(show) ui.hudfrag.shown = false;
        });

        //Disable pause menu when open and display message
        ui.paused.shown(() -> {
            if(show){
                if(settings.getBool("tu-tp-close-warn")){
                    app.post(() -> {
                        ui.showCustomConfirm(
                            "@tu-painter.paused", "@tu-painter.pauseclose",
                            "@ok", "@cancel",
                            this::hide,
                            () -> ui.paused.hide()
                        );
                    });
                }else{
                    hide();
                }
            }
        });

        Events.on(GameOverEvent.class, e -> hide());
    }

    public void show(){
        show = true;
        control.input.commandMode = false;
        painter.beginEditing();
    }

    public void hide(){
        if(!show) return;

        show = false;
        painter.endEditing();
        painter.clearOp();
        ui.hudfrag.shown = true;
    }

    public boolean shown(){
        return show;
    }

    private void rebuild(){
        selection.clear();
        String text = search.getText();

        Seq<Block> array = content.blocks()
            .select(b ->
                blockFilter(b)
                    && (text.isEmpty() || b.localizedName.toLowerCase().contains(text.toLowerCase()))
            );
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
                    image.update(() -> image.color.lerp(listener.isOver() || painter.drawBlock == b ? Color.white : Color.lightGray, Mathf.clamp(0.4f * TUVars.delta())));
                }else{
                    image.update(() -> image.color.lerp(painter.drawBlock == b ? Color.white : Color.lightGray, Mathf.clamp(0.4f * TUVars.delta())));
                }

                image.clicked(() -> {
                    if(input.keyDown(KeyCode.shiftLeft) && Fonts.getUnicode(b.name) != 0){
                        app.setClipboardText((char)Fonts.getUnicode(b.name) + "");
                        ui.showInfoFade("@copied");
                    }else{
                        painter.drawBlock = b;
                    }
                });
                TUElements.boxTooltip(image, b.localizedName);

                if((++count) % cols == 0){
                    list.row();
                }
            }
        }).fillX().left().padBottom(10);
    }

    private boolean blockFilter(Block b){
        if(buildings){
            return isBuilding(b);
        }else{
            return isTerrainBlock(b);
        }
    }

    private boolean isTerrainBlock(Block b){
        return (
            b.isFloor() || b.isOverlay() || b.isStatic() ||
                b instanceof Prop ||
                b instanceof TreeBlock ||
                b instanceof TallBlock ||
                b instanceof Cliff
        ) && !b.isAir() && (b.inEditor || b == Blocks.cliff) && b != Blocks.spawn;
    }

    private boolean isBuilding(Block b){
        return !b.isFloor() && !b.isStatic()
            && !(b instanceof Prop)
            && !(b instanceof TallBlock)
            && !(b instanceof TreeBlock)
            && !(b instanceof ConstructBlock)
            && !(b instanceof LegacyBlock)
            && (!b.isHidden() || settings.getBool("tu-show-hidden"));
    }
}
