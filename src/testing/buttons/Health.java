package testing.buttons;

import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import mindustry.gen.*;
import testing.*;
import testing.ui.*;
import testing.util.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class Health{
    public static void heal(boolean invincibility){
        if(player.unit() == null) return;

        Unit u = player.unit();
        if(net.client()){
            if(u instanceof BlockUnitc b){
                String health = invincibility ? "Number.MAX_VALUE" : (b.tile().block.health + "");
                Utils.runCommandPlayerShort(
                    "p.unit().tile().maxHealth=" + health + ";" +
                    "p.unit().tile().health=" + health + ";"
                );
            }else{
                String health = invincibility ? "Number.MAX_VALUE" : (u.type.health + "");
                Utils.runCommandPlayerShort(
                    "p.unit().maxHealth=" + health + ";" +
                    "p.unit().health=" + health + ";"
                );
            }
        }else{
            if(u instanceof BlockUnitc bu){
                Building b = bu.tile();
                b.maxHealth(invincibility ? Float.POSITIVE_INFINITY : b.block.health);
                b.health = b.maxHealth;
            }else{
                u.maxHealth(invincibility ? Float.POSITIVE_INFINITY : u.type.health);
                u.health = u.maxHealth;
            }
        }
        Utils.spawnIconEffect(invincibility ? "invincibility" : "heal");
    }

    public static Cell<ImageButton> healing(Table t){
        Cell<ImageButton> i = t.button(TUIcons.heal, TUStyles.tuRedImageStyle, TUVars.iconSize, () -> {
            heal(false);
        }).growX();

        ImageButton b = i.get();
        TUElements.boxTooltip(b, "@tu-tooltip.button-heal");
        b.setDisabled(TestUtils::disableButton);
        b.label(() -> "[" + (b.isDisabled() ? "gray" : "white") + "]" + bundle.get("tu-ui-button.heal")).growX();
        b.update(() -> {
            b.setColor(player.team().color != null ? player.team().color : TUVars.curTeam.color);
        });

        return i;
    }

    public static Cell<ImageButton> invincibility(Table t){
        Cell<ImageButton> i = t.button(TUIcons.invincibility, TUStyles.tuRedImageStyle, TUVars.iconSize, () -> {
            heal(true);
        }).growX();

        ImageButton b = i.get();
        TUElements.boxTooltip(b, "@tu-tooltip.button-invincibility");
        b.setDisabled(TestUtils::disableButton);
        b.label(() -> "[" + (b.isDisabled() ? "gray" : "white") + "]" + bundle.get("tu-ui-button.invincible")).growX();
        b.update(() -> {
            b.setColor(player.team().color != null ? player.team().color : TUVars.curTeam.color);
        });

        return i;
    }
}
