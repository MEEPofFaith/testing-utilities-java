package testing.ui.fragments;

import arc.graphics.*;
import arc.input.*;
import arc.math.*;
import arc.scene.*;
import arc.scene.event.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.gen.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.blocks.legacy.*;
import testing.ui.*;
import testing.util.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class TerrainPainterFragment{
    public static boolean show = false;

    TextField search;
    Table selection = new Table();
    Block block = Blocks.boulder;

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
                all.pane(sel -> sel.add(selection)).fillX().padBottom(4).height(h);
                all.row();

                all.table(b -> {
                    TUElements.imageButton(
                        b, TUIcons.get(Icon.left), Styles.defaulti, TUVars.buttonSize,
                        () -> show = false,
                        () -> "@close",
                        null
                    );
                }).fillX();
            });
        });

        rebuild();
    }

    void rebuild(){
        selection.clear();
        String text = search.getText();

        Seq<Block> array = content.blocks()
            .select(b -> (b.isFloor() || b.isOverlay() || b.isStatic()) &&
                !b.isAir() && b.inEditor && b != Blocks.spawn &&
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
}
