package testing.buttons;

import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import blui.*;
import blui.ui.*;
import mindustry.gen.*;
import testing.ui.*;
import testing.util.*;

import static mindustry.Vars.*;

public class Health{
    public static void heal(boolean invincibility){
        Unit u = player.unit();
        if(u == null) return;
        if(u instanceof BlockUnitc bu){
            Building b = bu.tile();
            b.maxHealth(invincibility ? Float.MAX_VALUE : b.block.health);
            b.health = b.maxHealth;
        }else{
            u.maxHealth(invincibility ? Float.MAX_VALUE : u.type.health);
            u.health = u.maxHealth;
        }
    }

    public static void healing(Table t){
        Cell<ImageButton> i = t.button(TUIcons.heal, TUStyles.tuImageStyle, BLVars.iconSize, () -> {
            heal(false);
        }).growX();

        ImageButton b = i.get();
        BLElements.boxTooltip(b, "@tu-tooltip.button-heal");
        b.update(() -> {
            b.setColor(player.team().color != null ? player.team().color : TUVars.curTeam.color);
        });

    }

    public static void invincibility(Table t){
        Cell<ImageButton> i = t.button(TUIcons.invincibility, TUStyles.tuImageStyle, BLVars.iconSize, () -> {
            heal(true);
        }).growX();

        ImageButton b = i.get();
        BLElements.boxTooltip(b, "@tu-tooltip.button-invincibility");
        b.update(() -> {
            b.setColor(player.team().color != null ? player.team().color : TUVars.curTeam.color);
        });

    }

    public static void addButtons(Table t){
        healing(t);
        invincibility(t);
    }
}
