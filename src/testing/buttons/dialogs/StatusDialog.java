package testing.buttons.dialogs;

import arc.*;
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
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.*;
import testing.util.*;

import static mindustry.Vars.*;

public class StatusDialog extends BaseDialog{
    TextField search;
    Table all = new Table();
    StatusEffect status = StatusEffects.burning;
    float duration = 0.125f;

    float minDur = 0.125f, maxDur = 60f;

    public StatusDialog(){
        super("@tu-status-menu.name");

        shouldPause = false;
        addCloseButton();
        shown(this::rebuild);
        onResize(this::rebuild);

        all.margin(20).marginTop(0f);

        cont.table(s -> {
            s.image(Icon.zoom).padRight(8);
            search = s.field(null, text -> rebuild()).growX().get();
            search.setMessageText("@players.search");
        }).fillX().padBottom(4).row();

        cont.pane(all);

        buttons.button("$tu-status-menu.clear", Icon.cancel, this::clearStatus);
    }

    void rebuild(){
        all.clear();
        String text = search.getText();

        all.label(
            () -> "[accent]Current selection: [#" + status.color + "]" +
            status.localizedName +
            (status.permanent ? " [accent](Permanent Effect)" : "")
        ).padBottom(6);
        all.row();

        Seq<StatusEffect> array = content.statusEffects().select(e -> e != StatusEffects.none && (text.isEmpty() || e.localizedName.toLowerCase().contains(text.toLowerCase())));
        all.table(list -> {
            list.left();

            int cols = (int)Mathf.clamp((Core.graphics.getWidth() - Scl.scl(30)) / Scl.scl(32 + 10) / 2, 1, 22);
            int count = 0;

            for(StatusEffect s : array){
                Image image = new Image(s.uiIcon).setScaling(Scaling.fit);
                list.add(image).size(8 * 8).pad(3);

                ClickListener listener = new ClickListener();
                image.addListener(listener);
                if(!mobile){
                    image.addListener(new HandCursorListener());
                    image.update(() -> image.color.lerp(!listener.isOver() ? Color.lightGray : Color.white, Mathf.clamp(0.4f * Time.delta)));
                }

                image.clicked(() -> {
                    if(Core.input.keyDown(KeyCode.shiftLeft) && Fonts.getUnicode(s.name) != 0){
                        Core.app.setClipboardText((char)Fonts.getUnicode(s.name) + "");
                        ui.showInfoFade("@copied");
                    }else{
                        status = s;
                    }
                });
                image.addListener(new Tooltip(t -> t.background(Tex.button).add(s.localizedName)));

                if((++count) % cols == 0){
                    list.row();
                }
            }
        }).growX().left().padBottom(10);
        all.row();

        all.table(d -> {
            TextField dField = new TextField(String.valueOf(duration));
            d.slider(minDur, maxDur, 0.125f, duration, n -> {
                duration = n;
                dField.setText(String.valueOf(n));
            }).get();
            d.add("Duration (seconds): ").padLeft(8);
            d.add(dField);
            dField.changed(() -> {
                if(dField.isValid()){
                    String s = Utils.extractNumber(dField.getText());
                    if(!s.isEmpty()){
                        duration = Float.parseFloat(s);
                    }
                }
            });
            dField.update(() -> {
                Scene stage = dField.getScene();
                if(!(stage != null && stage.getKeyboardFocus() == dField))
                    dField.setText(String.valueOf(duration));
            });
        }).bottom();
        all.row();

        all.table(null, b -> {
            b.defaults().size(210, 64);

            b.button("$tu-status-menu.apply", Icon.add, () -> apply(false)).padRight(8);
            b.button("$tu-status-menu.perma", Icon.add, () -> apply(true));
        });
    }

    void apply(boolean perma){
        Utils.check();
        if(net.client()){
            Utils.runCommand("let tempEff=Vars.content.statusEffects().find(b=>b.name===" + status.name + ")");
            Utils.runCommandPlayer("(p.unit()!=null?p.unit().apply(tempEff," + (perma ? "Number.MAX_VALUE" : duration * 60) + "):0));");
        }else if(player.unit() != null){
            player.unit().apply(status, perma ? Float.MAX_VALUE : duration * 60);
        }
    }

    void clearStatus(){
        Utils.check();
        if(net.client()){
            Utils.runCommandPlayer("(p.unit()!=null?p.unit().clearStatuses():0)");
        }else if(player.unit() != null){
            player.unit().clearStatuses();
        }
    }

    public StatusEffect getStatus(){
        return status;
    }
}