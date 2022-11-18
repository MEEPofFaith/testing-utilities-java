package testing.dialogs;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.input.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.event.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.core.*;
import mindustry.game.EventType.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.*;
import mindustry.world.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.blocks.legacy.*;
import testing.*;
import testing.buttons.*;
import testing.ui.*;
import testing.ui.fragments.*;
import testing.util.*;

import static arc.Core.*;
import static mindustry.Vars.*;
import static testing.ui.TUDialogs.*;

public class BlockDialog extends BaseDialog{
    TextField search;
    Table selection = new Table();
    Block block = Blocks.coreShard;
    Team placeTeam = Team.get(settings.getInt("tu-default-team", 1));
    int placePos, rotation = 1;
    static boolean initialized;

    boolean expectingPos;

    public BlockDialog(){
        super("@tu-block-menu.name");

        shouldPause = false;
        addCloseButton();
        shown(this::rebuild);
        onResize(this::rebuild);

        cont.table(s -> {
            s.image(Icon.zoom).padRight(8);
            search = s.field(null, text -> rebuild()).growX().get();
            search.setMessageText("@players.search");
        }).fillX().padBottom(4).row();

        cont.label(() -> bundle.get("tu-menu.selection") + block.localizedName).padBottom(6).row();

        cont.pane(all -> all.add(selection)).fillX().row();

        cont.table(t -> {
            TUElements.imageButton(
                t, TUIcons.get(Icon.defense), TUStyles.lefti, TUVars.buttonSize,
                () -> teamDialog.show(placeTeam, team -> placeTeam = team),
                () -> bundle.format("tu-unit-menu.set-team", "[#" + placeTeam.color + "]" + teamName() + "[]"),
                "@tu-tooltip.block-set-team"
            );

            TUElements.imageButton(
                t, TUIcons.get(Icon.map), TUStyles.toggleRighti, TUVars.buttonSize,
                () -> {
                    hide();
                    expectingPos = true;
                },
                () -> bundle.format("tu-unit-menu.pos", Point2.x(placePos), Point2.y(placePos)),
                "@tu-tooltip.block-pos"
            );
        }).padTop(6).row();

        cont.table(p -> {
            ImageButton rb = TUElements.imageButton(
                p, TUIcons.get(Icon.up), TUStyles.lefti, TUVars.buttonSize,
                () -> rotation = Mathf.mod(rotation - 1, 4),
                null, "@tu-tooltip.block-rotate"
            );
            rb.setDisabled(() -> !block.rotate);
            rb.update(() -> ((TextureRegionDrawable)(rb.getStyle().imageUp)).setRegion(getDirection()));

            ImageButton pb = TUElements.imageButton(
                p, new TextureRegionDrawable(block.uiIcon), TUStyles.centeri, TUVars.buttonSize,
                this::placeBlock,
                () -> "@tu-block-menu.place",
                "@tu-tooltip.block-place"
            );
            pb.setDisabled(() -> Vars.world.tile(placePos) == null);
            pb.update(() -> {
                ((TextureRegionDrawable)(pb.getStyle().imageUp)).setRegion(block.uiIcon);
            });

            ImageButton cb = TUElements.imageButton(
                p, TUIcons.get(Icon.cancel), TUStyles.righti, TUVars.buttonSize,
                this::deleteBlock,
                () -> "@tu-block-menu.delete",
                "@tu-tooltip.block-delete"
            );
        }).padTop(6f).row();

        ImageButton pb = TUElements.imageButton(
            cont, TUIcons.get(Icon.terrain), Styles.defaulti, TUVars.buttonSize,
            () -> {
                TerrainPainterFragment.show = true;
                hide();
            },
            () -> "@tu-block-menu.open-painter",
            "@tu-tooltip.block-terrain-painter-open"
        );
        pb.update(() -> pb.setDisabled(TerrainPainterFragment.show));

        if(!initialized){
            Events.run(Trigger.update, () -> {
                if(expectingPos){
                    if(!state.isGame()){
                        expectingPos = false;
                    }else if(input.justTouched()){
                        if(!scene.hasMouse()){
                            int x = World.toTile(input.mouseWorldX()),
                                y = World.toTile(input.mouseWorldY());
                            placePos = Point2.pack(x, y);
                            ui.showInfoToast(bundle.format("tu-unit-menu.set-pos", x, y), 4f);
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

    public void drawPos(){
        float size = block.size * tilesize,
            offset = (1 - block.size % 2) * tilesize / 2f,
            x, y;
        if(expectingPos && state.isGame() && !scene.hasMouse()){
            x = World.toTile(input.mouseWorldX()) * tilesize;
            y = World.toTile(input.mouseWorldY()) * tilesize;
        }else if(Spawn.blockHover && !TestUtils.disableCampaign()){
            x = Point2.x(placePos) * tilesize;
            y = Point2.y(placePos) * tilesize;
        }else{
            return;
        }
        Draw.z(Layer.overlayUI);
        Lines.stroke(1f, placeTeam.color);
        Lines.rect(x - size/2 + offset, y - size/2 + offset, size, size);
        Draw.rect(Icon.cancel.getRegion(), x, y, tilesize, tilesize);
    }

    void rebuild(){
        expectingPos = false;
        selection.clear();
        String text = search.getText();

        Seq<Block> array = content.blocks()
            .select(b -> !b.isFloor() && !b.isStatic() &&
                !(b instanceof Prop) &&
                !(b instanceof TallBlock) &&
                !(b instanceof TreeBlock) &&
                !(b instanceof ConstructBlock) &&
                !(b instanceof LegacyBlock) &&
                (!b.isHidden() || settings.getBool("tu-show-hidden")) &&
                (text.isEmpty() || b.localizedName.toLowerCase().contains(text.toLowerCase())));
        if(array.size == 0) return;

        selection.table(list -> {
            list.left();

            float iconMul = 1.25f;
            int cols = (int)Mathf.clamp((graphics.getWidth() - Scl.scl(30)) / Scl.scl(32 + 10) / iconMul, 1, 22 / iconMul);
            int count = 0;

            for(Block b : array){
                Image image = new Image(b.uiIcon).setScaling(Scaling.fit);
                list.add(image).size(8 * 4 * iconMul).pad(3);

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

    TextureRegion getDirection(){
        TextureRegionDrawable tex = switch(rotation){
            case 1 -> Icon.up;
            case 2 -> Icon.left;
            case 3 -> Icon.down;
            default -> Icon.right;
        };
        return tex.getRegion();
    }

    void placeBlock(){
        if(net.client()){
            Utils.runCommand("Vars.world.tile(" + placePos + ").setNet(Vars.content.block(" + block.id + "),Team.get(" + placeTeam.id + ")," + rotation + ")");
        }else{
            world.tile(placePos).setNet(block, placeTeam, rotation);
        }
    }

    void deleteBlock(){
        if(net.client()){
            Utils.runCommand("Vars.world.tile(" + placePos + ").setAir()");
        }else{
            world.tile(placePos).setAir();
        }
    }

    String teamName(){
        return teamDialog.teamName(placeTeam);
    }

    public Block getBlock(){
        return block;
    }
}
