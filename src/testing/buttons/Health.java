package testing.buttons;

import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import mindustry.gen.*;
import testing.ui.*;
import testing.util.*;

import static mindustry.Vars.*;

public class Health{
    public static void heal(boolean invincibility){
        if(net.client()){
            Utils.runCommandPlayer(
                "p.unit().dead = false; " +
                "p.unit().maxHealth = " + (invincibility ? "Number.MAX_VALUE" : "p.unit().type.health") + "; " +
                "p.unit().health = p.unit().maxHealth;"
            );
        }else if(player.unit() != null && player.unit().type != null){
            Unit u = player.unit();
            u.dead = false;
            u.maxHealth(invincibility ? Float.POSITIVE_INFINITY : u.type.health);
            u.health = u.maxHealth;
        }
        Utils.spawnIconEffect(invincibility ? "invincibility" : "heal");
    }

    public static Cell<ImageButton> healing(Table t){
        Cell<ImageButton> i = t.button(TUIcons.heal, TUStyles.tuRedImageStyle, () -> {
            heal(false);
        }).growX();
        ImageButton b = i.get();
        b.setDisabled(() -> state.isCampaign());
        b.label(() -> "[" + (b.isDisabled() ? "gray" : "white") + "]Heal").growX();
        b.update(() -> {
            b.setColor(player.team().color != null ? player.team().color : TUVars.curTeam.color);
        });

        return i;
    }

    public static Cell<ImageButton> invincibility(Table t){
        Cell<ImageButton> i = t.button(TUIcons.invincibility, TUStyles.tuRedImageStyle, () -> {
            heal(true);
        }).growX();
        ImageButton b = i.get();
        b.setDisabled(() -> state.isCampaign());
        b.label(() -> "[" + (b.isDisabled() ? "gray" : "white") + "]Invincibility").growX();
        b.update(() -> {
            b.setColor(player.team().color != null ? player.team().color : TUVars.curTeam.color);
        });

        return i;
    }
}