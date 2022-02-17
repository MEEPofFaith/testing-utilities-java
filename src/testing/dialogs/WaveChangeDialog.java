package testing.dialogs;

import arc.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.dialogs.*;
import testing.ui.*;
import testing.util.*;

import static mindustry.Vars.*;

public class WaveChangeDialog extends BaseDialog{
    Table all = new Table();

    int minWave = 1, maxWave = 50;

    public WaveChangeDialog(){
        super("@tu-unit-menu.waves");

        shouldPause = false;
        addCloseButton();
        shown(this::rebuild);
        onResize(this::rebuild);

        all.margin(20).marginTop(0f);

        cont.table(w -> {
            w.add("@tu-unit-menu.wave-range");
            TextField minField = TUElements.textField(
                String.valueOf(minWave),
                field -> {
                    if(field.isValid()){
                        String s = Utils.extractNumber(field.getText());
                        if(!s.isEmpty()){
                            minWave = Math.max(Integer.parseInt(s), 1);
                            minWave = Math.min(minWave, maxWave); //Cannot be greater than max
                            rebuild();
                        }
                    }
                },
                () -> String.valueOf(minWave)
            );
            w.add(minField).left().padLeft(6).width(60f);

            w.add("-");

            TextField maxField = TUElements.textField(
                String.valueOf(maxWave),
                field -> {
                    if(field.isValid()){
                        String s = Utils.extractNumber(field.getText());
                        if(!s.isEmpty()){
                            maxWave = Math.max(Integer.parseInt(s), minWave); //Cannot be less than min
                            rebuild();
                        }
                    }
                },
                () -> String.valueOf(maxWave)
            );
            w.add(maxField).left().padLeft(6).width(60f);
        });
        cont.row();
        cont.label(() -> Core.bundle.format("tu-unit-menu.wave-current", state.wave));
        cont.row();
        cont.pane(all);
    }

    void rebuild(){
        all.clear();

        float iconMul = 1.5f;
        all.table(t -> {
            for(int i = minWave; i <= maxWave; i++){
                int ii = i;
                TextButton b = t.button(String.valueOf(ii), () -> setWave(ii)).right().grow().padRight(6f).padTop(6f).get();
                b.getLabel().setWrap(false);
                b.getLabelCell().center();

                t.table(w -> {
                    int wave = ii - 1;
                    for(SpawnGroup group: state.rules.spawns){
                        if(group.getSpawned(wave) <= 0) continue;
                        w.table(u -> {
                            u.add(TUElements.itemImage(
                                new TextureRegionDrawable(group.type.uiIcon),
                                () -> String.valueOf(group.getSpawned(wave))
                            )).size(8 * 4 * iconMul).top().grow();
                            boolean hasEffect = group.effect != null && group.effect != StatusEffects.none,
                                hasShield = group.getShield(wave) > 0;
                            if(hasEffect || hasShield){
                                u.row();
                                u.table(e -> {
                                    if(hasEffect){
                                        e.add(new Image(group.effect.uiIcon).setScaling(Scaling.fit)).size(8 * 2 * iconMul);
                                    }
                                    if(hasShield){
                                        e.add(TUElements.itemImage(
                                            Icon.defense,
                                            () -> String.valueOf((int)group.getShield(wave)),
                                            Pal.accentBack,
                                            Pal.accent,
                                            0.5f,
                                            Align.center
                                        )).size(8 * 2 * iconMul).growX();
                                    }
                                }).top().grow();
                            }
                        }).grow();
                    }
                }).growY().left().padTop(4f);
                t.row();
            }
        });
    }

    void setWave(int wave){
        if(Utils.noCheat()){
            if(net.client()){
                Utils.runCommand("Vars.state.wave = " + wave);
            }else{
                state.wave = wave;
            }
        }
    }
}
