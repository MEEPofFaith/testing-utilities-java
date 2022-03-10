package testing.buttons;

import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.blocks.storage.CoreBlock.*;
import testing.ui.*;
import testing.util.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class Sandbox{
    static boolean fillMode = true;
    static float timer;
    static boolean swap;

    public static void toggle(){
        if(Utils.noCheat()){
            Utils.spawnIconEffect(state.rules.infiniteResources ? "survival" : "sandbox");
            if(net.client()){
                Utils.runCommand("Vars.state.rules.infiniteResources = !Vars.state.rules.infiniteResources;Call.setRules(Vars.state.rules);");
            }else{
                state.rules.infiniteResources = !state.rules.infiniteResources;
            }
        }
    }

    public static void coreItems(){
        if(Utils.noCheat()){
            if(net.client()){
                Utils.runCommandPlayer(
                    "Vars.content.items().each(i => p.core().items.set(i," +
                    (fillMode ? "p.core().storageCapacity" : "0") +
                    "));"
                );
            }else{
                CoreBuild core = player.core();
                if(core != null){
                    content.items().each(i -> core.items.set(i, fillMode ? core.storageCapacity : 0));
                }
            }
            Utils.spawnIconEffect(fillMode ? "core" : "dump");
        }
    }

    public static Cell<ImageButton> toggling(Table t){
        Cell<ImageButton> i = t.button(TUIcons.survival, TUStyles.tuRedImageStyle, Sandbox::toggle)
            .color(TUVars.curTeam.color).growX().tooltip("@tu-tooltip.button-sandbox");
        ImageButton b = i.get();
        b.setDisabled(() -> state.isCampaign());
        b.update(() -> {
            b.getStyle().imageUp = state.rules.infiniteResources ? TUIcons.survival : TUIcons.sandbox;
            b.setColor(player.team().color != null ? player.team().color : TUVars.curTeam.color);
        });

        return i;
    }

    public static Cell<ImageButton> filling(Table t){
        Cell<ImageButton> i = t.button(TUIcons.core, TUStyles.tuRedImageStyle, () -> {
            if(!swap) coreItems();
        }).color(TUVars.curTeam.color).growX().tooltip("@tu-tooltip.button-fill");
        ImageButton b = i.get();
        b.setDisabled(() -> state.isCampaign());
        b.resizeImage(40f);
        b.update(() -> {
            if(b.isPressed() && !b.isDisabled()){
                timer += graphics.getDeltaTime() * 60;
                if(timer >= TUVars.longPress && !swap){
                    fillMode = !fillMode;
                    swap = true;
                }
            }else{
                timer = 0;
                swap = false;
            }

            b.getStyle().imageUp = fillMode ? TUIcons.core : TUIcons.dump;
            b.setColor(player.team().color != null ? player.team().color : TUVars.curTeam.color);
        });

        return i;
    }

    public static void add(Table table){
        table.table(Tex.buttonEdge3, t -> {
            toggling(t).size(TUVars.iconWidth, 40);
            filling(t).size(TUVars.iconWidth, 40);
        }).padBottom(TUVars.TCOffset + TUVars.buttonHeight).padLeft(2 * (TUVars.iconWidth + 20));
    }
}
