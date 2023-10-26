package testing.dialogs;

import arc.graphics.*;
import arc.input.*;
import arc.math.*;
import arc.scene.event.*;
import arc.scene.ui.*;
import arc.scene.ui.TextField.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.ui.*;
import testing.ui.*;
import testing.util.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class StatusDialog extends TUBaseDialog{
    private static final float minDur = 0.125f, maxDur = 60f;
    private final Table selection = new Table();
    private TextField search;
    private StatusEffect status = StatusEffects.burning;
    private float duration = 10f;
    private boolean perma;

    public StatusDialog(){
        super("@tu-status-menu.name");

        perma = settings.getBool("tu-permanent", false);

        cont.table(s -> {
            s.image(Icon.zoom).padRight(8);
            search = s.field(null, text -> rebuild()).growX().get();
            search.setMessageText("@players.search");
        }).fillX().padBottom(4).row();

        cont.pane(all -> {
            all.add(selection);
            all.row();

            all.collapser(s -> TUElements.sliderSet(
                s, text -> duration = Strings.parseFloat(text), () -> String.valueOf(duration),
                TextFieldFilter.floatsOnly, Strings::canParsePositiveFloat,
                minDur, maxDur, 0.125f, duration, (n, f) -> {
                    duration = n;
                    f.setText(String.valueOf(n));
                },
                "@tu-status-menu.duration",
                "@tu-tooltip.status-duration"
            ), true, () -> !perma && !status.permanent).bottom().get().setDuration(0.06f);
            all.row();

            all.table(null, b -> {
                ImageButton ab = b.button(TUIcons.get(Icon.add), TUStyles.lefti, TUVars.buttonSize, this::apply).get();
                TUElements.boxTooltip(ab, "@tu-tooltip.status-apply");
                ab.label(() -> "@tu-status-menu.apply").padLeft(6).growX();

                ImageButton pb = b.button(TUIcons.get(Icon.refresh), TUStyles.toggleRighti, TUVars.buttonSize, () -> perma = !perma).get();
                TUElements.boxTooltip(pb, "@tu-tooltip.status-perma");
                Label pl = pb.label(() -> "@tu-status-menu.perma").padLeft(6).growX().get();
                pb.setDisabled(() -> status.permanent);
                pb.update(() -> {
                    pb.setChecked(perma);
                    pl.setColor(pb.isDisabled() ? Color.gray : Color.white);
                });
            }).padTop(6);
        });

        TUElements.boxTooltip(
            buttons.button("$tu-status-menu.clear", Icon.cancel, this::clearStatus).get(),
            "@tu-tooltip.status-clear"
        );
    }

    @Override
    protected void rebuild(){
        selection.clear();
        String text = search.getText();

        selection.label(
            () -> bundle.get("tu-menu.selection") + "[#" + status.color + "]" +
                status.localizedName +
                (status.permanent ? bundle.get("tu-status-menu.permaeff") : "")
        ).padBottom(6);
        selection.row();

        Seq<StatusEffect> array = content.statusEffects().select(e -> e != StatusEffects.none && (text.isEmpty() || e.localizedName.toLowerCase().contains(text.toLowerCase())));
        selection.table(list -> {
            list.left();

            float iconMul = 1.5f;
            int cols = (int)Mathf.clamp((graphics.getWidth() - Scl.scl(30)) / Scl.scl(32 + 10) / iconMul, 1, 22 / iconMul);
            int count = 0;

            for(StatusEffect s : array){
                Image image = new Image(s.uiIcon).setScaling(Scaling.fit);
                list.add(image).size(8 * 4 * iconMul).pad(3);

                ClickListener listener = new ClickListener();
                image.addListener(listener);
                if(!mobile){
                    image.addListener(new HandCursorListener());
                    image.update(() -> image.color.lerp(listener.isOver() || status == s ? Color.white : Color.lightGray, Mathf.clamp(0.4f * TUVars.delta())));
                }else{
                    image.update(() -> image.color.lerp(status == s ? Color.white : Color.lightGray, Mathf.clamp(0.4f * TUVars.delta())));
                }

                image.clicked(() -> {
                    if(input.keyDown(KeyCode.shiftLeft) && Fonts.getUnicode(s.name) != 0){
                        app.setClipboardText((char)Fonts.getUnicode(s.name) + "");
                        ui.showInfoFade("@copied");
                    }else{
                        status = s;
                    }
                });
                TUElements.boxTooltip(image, s.localizedName);

                if((++count) % cols == 0){
                    list.row();
                }
            }
        }).growX().left().padBottom(10);
    }

    void apply(){
        if(net.client()){ //For 2r2t
            Utils.runCommand("statuseff @ @", status.name, perma ? "MAX_VALUE" : duration * 60);
        }else if(player.unit() != null){
            if(input.shift()){
                Utils.copyJS("Vars.player.unit().apply(Vars.content.getByID(ContentType.status, @), @);",
                    status.id, perma ? "Number.MAX_VALUE" : duration * 60
                );
                return;
            }

            player.unit().apply(status, perma ? Float.MAX_VALUE : duration * 60);
        }
    }

    void clearStatus(){
        if(input.shift()){
            Utils.copyJS("Vars.player.unit().clearStatuses();");
            return;
        }

        player.unit().clearStatuses();
    }

    public StatusEffect getStatus(){
        return status;
    }
}
