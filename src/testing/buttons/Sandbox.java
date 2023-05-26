package testing.buttons;

import arc.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import mindustry.world.blocks.storage.CoreBlock.*;
import testing.ui.*;
import testing.util.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class Sandbox{
    static boolean fill = true;
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

    public static void toggling(Table t){
        Cell<ImageButton> i = t.button(TUIcons.survival, TUStyles.tuImageStyle, Sandbox::toggle);

        ImageButton b = i.get();
        TUElements.boxTooltip(b, "@tu-tooltip.button-sandbox");
        b.update(() -> {
            b.getStyle().imageUp = state.rules.infiniteResources ? TUIcons.survival : TUIcons.sandbox;
        });

    }

    public static void filling(Table t){
        Cell<ImageButton> i = t.button(TUIcons.core, TUStyles.tuImageStyle, TUVars.iconSize, () -> {
            if(!swap) coreItems();
        });

        ImageButton b = i.get();
        TUElements.boxTooltip(b, "@tu-tooltip.button-fill");
        b.update(() -> {
            if(b.isPressed() && !b.isDisabled()){
                TUVars.pressTimer += Core.graphics.getDeltaTime() * 60;
                if(TUVars.pressTimer >= TUVars.longPress && !swap){
                    fill = !fill;
                    swap = true;
                }
            }

            b.getStyle().imageUp = fill ? TUIcons.core : TUIcons.dump;
        });
        b.released(() -> {
            TUVars.pressTimer = 0;
            swap = false;
        });
    }

    public static void addButtons(Table t){
        toggling(t);
        filling(t);
    }
}
