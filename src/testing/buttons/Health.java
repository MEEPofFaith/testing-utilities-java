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
        if(net.client()){
            Utils.runCommandPlayer(
                "p.unit().dead=false;" +
                "p.unit().maxHealth=" + (invincibility ? "Number.MAX_VALUE" : "p.unit().type.health") + ";" +
                "p.unit().health=p.unit().maxHealth;" +
                "if(p.unit() instanceof BlockUnitUnit){" +
                "p.unit().tile().dead=false;" +
                "p.unit().tile().maxHealth=" + (invincibility ? "Number.MAX_VALUE" : "p.unit().tile().block.health") + ";" +
                "p.unit().maxHealth=p.unit().tile().maxHealth;" +
                "p.unit().health=p.unit().maxHealth;}"
            );
        }else if(player.unit() != null && player.unit().type != null){
            Unit u = player.unit();
            u.dead = false;
            u.maxHealth(invincibility ? Float.POSITIVE_INFINITY : u instanceof BlockUnitUnit b ? b.tile().block.health : u.type.health);
            u.health = u.maxHealth;
            if(u instanceof BlockUnitUnit b){
                Building build = b.tile();
                build.dead = false;
                build.maxHealth(invincibility ? Float.POSITIVE_INFINITY : build.block.health);
                build.health = build.maxHealth;
            }
        }
        Utils.spawnIconEffect(invincibility ? "invincibility" : "heal");
    }

    public static Cell<ImageButton> healing(Table t){
        Cell<ImageButton> i = t.button(TUIcons.heal, TUStyles.tuRedImageStyle, () -> {
            heal(false);
        }).growX();

        ImageButton b = i.get();
        TUElements.boxTooltip(b, "@tu-tooltip.button-heal");
        b.setDisabled(TestUtils::disableCampaign);
        b.label(() -> "[" + (b.isDisabled() ? "gray" : "white") + "]" + bundle.get("tu-ui-button.heal")).growX();
        b.resizeImage(40f);
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
        TUElements.boxTooltip(b, "@tu-tooltip.button-invincibility");
        b.setDisabled(TestUtils::disableCampaign);
        b.label(() -> "[" + (b.isDisabled() ? "gray" : "white") + "]" + bundle.get("tu-ui-button.invincible")).growX();
        b.resizeImage(40f);
        b.update(() -> {
            b.setColor(player.team().color != null ? player.team().color : TUVars.curTeam.color);
        });

        return i;
    }
}
