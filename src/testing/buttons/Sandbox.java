package testing.buttons;

import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import mindustry.gen.*;
import mindustry.world.blocks.storage.CoreBlock.*;
import testing.*;
import testing.ui.*;
import testing.util.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class Sandbox{
    static boolean fill = true;
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
        if(fill){
            fillCore();
        }else{
            dumpCore();
        }
    }

    public static void fillCore(){
        if(Utils.noCheat()){
            if(net.client()){
                CoreBuild core = player.core();
                if(core == null) return;
                float capacity = core.storageCapacity;
                if(settings.getBool("tu-fill-all")){
                    Utils.runCommandPlayer(
                        "Vars.content.items().each(" +
                            "i=>!Vars.state.rules.hiddenBuildItems.contains(i)," +
                            "i=>p.core().items.set(i," + capacity + ")" +
                        ");"
                    );
                }else{ //Separate to prevent unnecessary command length.
                    Utils.runCommandPlayer(
                        "Vars.content.items().each(i=>p.core().items.set(i," + capacity + "));"
                    );
                }
            }else{
                CoreBuild core = player.core();
                if(core != null){
                    content.items().each(
                        i -> settings.getBool("tu-fill-all") || !state.rules.hiddenBuildItems.contains(i),
                        i -> core.items.set(i, core.storageCapacity)
                    );
                }
            }
            Utils.spawnIconEffect("core");
        }
    }

    public static void dumpCore(){
        if(Utils.noCheat()){
            if(net.client()){
                Utils.runCommandPlayerFast(".core().items.clear()");
            }else{
                player.core().items.clear();
            }
            Utils.spawnIconEffect("dump");
        }
    }

    public static Cell<ImageButton> toggling(Table t){
        Cell<ImageButton> i = t.button(TUIcons.survival, TUStyles.tuRedImageStyle, Sandbox::toggle)
            .color(TUVars.curTeam.color).growX();

        ImageButton b = i.get();
        TUElements.boxTooltip(b, "@tu-tooltip.button-sandbox");
        b.setDisabled(TestUtils::disableCampaign);
        b.update(() -> {
            b.getStyle().imageUp = state.rules.infiniteResources ? TUIcons.survival : TUIcons.sandbox;
            b.setColor(player.team().color != null ? player.team().color : TUVars.curTeam.color);
        });

        return i;
    }

    public static Cell<ImageButton> filling(Table t){
        Cell<ImageButton> i = t.button(TUIcons.core, TUStyles.tuRedImageStyle, () -> {
            if(!swap) coreItems();
        }).color(TUVars.curTeam.color).growX();

        ImageButton b = i.get();
        TUElements.boxTooltip(b, "@tu-tooltip.button-fill");
        b.setDisabled(TestUtils::disableCampaign);
        b.resizeImage(40f);
        b.update(() -> {
            if(b.isPressed() && !b.isDisabled()){
                timer += graphics.getDeltaTime() * 60;
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

    public static void add(Table table){
        table.table(Tex.buttonEdge3, t -> {
            toggling(t).size(TUVars.iconSize, 40);
            filling(t).size(TUVars.iconSize, 40);
        });
    }
}
