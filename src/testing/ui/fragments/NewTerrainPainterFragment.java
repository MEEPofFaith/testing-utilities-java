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
import mindustry.world.blocks.environment.*;
import testing.ui.*;
import testing.util.*;

import static arc.Core.*;
import static mindustry.Vars.*;
import static testing.util.TUVars.*;

public class NewTerrainPainterFragment{
    private boolean show = false;

    TextField search;
    Table selection = new Table();

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

                all.label(() -> "[accent]" + painter.drawBlock.localizedName).padBottom(6).row();

                int rows = 6;
                float h = rows * (4 * 8) + (rows - 1) * 6 + 2 * 3;
                all.pane(sel -> {
                    sel.top();
                    sel.add(selection);
                }).fillX().padBottom(4).height(h);
                all.row();

                //TODO editor buttons

                TUElements.imageButton(
                    all, TUIcons.get(Icon.left), TUStyles.righti, TUVars.buttonSize,
                    this::hide,
                    () -> "@close",
                    "@tu-tooltip.painter-close"
                );
            });
        });

        rebuild();
    }

    public void show(){
        show = true;
        painter.beginEditing();
    }

    public void hide(){
        show = false;
        painter.endEditing();
        painter.clearOp();
    }

    public boolean shown(){
        return show;
    }

    private void rebuild(){
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
        }).growX().left().padBottom(10);
    }
}
