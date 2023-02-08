package testing.buttons;

import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.world.blocks.storage.CoreBlock.*;
import testing.*;
import testing.ui.*;
import testing.util.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class Sandbox extends TUButton{
    static boolean fill = true;
    static float timer;
    static boolean swap;

    public static void toggle(){
        if(input.shift()){
            Utils.copyJS("Vars.state.rules.infiniteResources = !Vars.state.rules.infiniteResources;");
            return;
        }

        Utils.spawnIconEffect(state.rules.infiniteResources ? "survival" : "sandbox");
        state.rules.infiniteResources = !state.rules.infiniteResources;
    }

    public static void coreItems(){
        if(fill){
            fillCore();
        }else{
            dumpCore();
        }
    }

    public static void fillCore(){
        if(input.shift()){
            if(settings.getBool("tu-fill-all")){
                Utils.copyJS("""
                    CoreBuild core = player.core();
                    Vars.content.items().each(i => core.items.set(i, core.storageCapacity));"""
                );
            }else{
                Utils.copyJS("""
                    CoreBuild core = player.core();
                    Vars.content.items().each(
                        i => !state.rules.hiddenBuildItems.contains(i),
                        i => core.items.set(i, core.storageCapacity)
                    );"""
                );
            }
            return;
        }

        CoreBuild core = player.core();
        if(core != null){
            content.items().each(
                i -> settings.getBool("tu-fill-all") || !state.rules.hiddenBuildItems.contains(i),
                i -> core.items.set(i, core.storageCapacity)
            );
        }
        Utils.spawnIconEffect("core");
    }

    public static void dumpCore(){
        if(input.shift()){
            Utils.copyJS("Vars.player.core().items.clear();");
            return;
        }

        if(player.core() != null) player.core().items.clear();
        Utils.spawnIconEffect("dump");
    }

    public static Cell<ImageButton> toggling(Table t){
        Cell<ImageButton> i = t.button(TUIcons.survival, TUStyles.tuRedImageStyle, Sandbox::toggle)
            .color(TUVars.curTeam.color).growX();

        ImageButton b = i.get();
        TUElements.boxTooltip(b, "@tu-tooltip.button-sandbox");
        b.setDisabled(TestUtils::disableButton);
        b.update(() -> {
            b.getStyle().imageUp = state.rules.infiniteResources ? TUIcons.survival : TUIcons.sandbox;
            b.setColor(player.team().color != null ? player.team().color : TUVars.curTeam.color);
        });

        return i;
    }

    public static Cell<ImageButton> filling(Table t){
        Cell<ImageButton> i = t.button(TUIcons.core, TUStyles.tuRedImageStyle, TUVars.iconSize, () -> {
            if(!swap) coreItems();
        }).color(TUVars.curTeam.color).growX();

        ImageButton b = i.get();
        TUElements.boxTooltip(b, "@tu-tooltip.button-fill");
        b.setDisabled(TestUtils::disableButton);
        b.update(() -> {
            if(b.isPressed() && !b.isDisabled()){
                timer += Time.delta;
                if(timer >= TUVars.longPress && !swap){
                    fill = !fill;
                    swap = true;
                }
            }else{
                timer = 0;
                swap = false;
            }

            b.getStyle().imageUp = fill ? TUIcons.core : TUIcons.dump;
            b.setColor(player.team().color != null ? player.team().color : TUVars.curTeam.color);
        });

        return i;
    }

    public void add(Table table){
        table.table(Tex.buttonEdge3, t -> {
            toggling(t).size(TUVars.iconSize, TUVars.iconSize);
            filling(t).size(TUVars.iconSize, TUVars.iconSize);
        });
    }
}
