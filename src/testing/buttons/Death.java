package testing.buttons;

import arc.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import testing.content.*;
import testing.ui.*;
import testing.util.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class Death{
    static float sTimer;

    static Stack kill = new Stack();
    static Image unit = new Image(), knife = new Image();

    public static void init(){
        knife.setDrawable(TUIcons.seppuku);

        unit.setScaling(Scaling.fit).setSize(TUIcons.seppuku.imageSize());
        knife.setScaling(Scaling.fit).setSize(TUIcons.seppuku.imageSize());

        Events.run(Trigger.update, () -> {
            if(state.isGame()){
                Unit u = player.unit();
                unit.setDrawable(u != null ? u.type.uiIcon : Icon.units.getRegion());
            }
        });
    }

    /** <i><b>SPONTANIUM COMBUSTUM!</b> That's a spell that makes the person who said it <b>e x p l o -</b></i> */
    public static void spontaniumCombustum(){
        if(net.client()){
            if(settings.getBool("tu-instakill")){
                Utils.runCommandPlayer(
                    "p.unit().elevation = 0;" +
                    "p.unit().health = -1;" +
                    "p.unit().dead = true;"
                );
            }
            Utils.runCommandPlayerFast(".unit().kill();");
        }else{
            Unit u = player.unit();
            if(u != null){
                for(int i = 0; i < Math.max(1f, u.hitSize / 4f); i++){
                    TUFx.deathLightning.at(u, true);
                }

                if(settings.getBool("tu-instakill")){
                    u.elevation(0);
                    u.health(-1);
                    u.dead(true);
                }
                u.kill();
            }
        }
    }

    public static Cell<ImageButton> seppuku(Table t){
        Cell<ImageButton> i = t.button(Icon.units, TUStyles.tuRedImageStyle, () -> {
            if(sTimer > TUVars.longPress) return;
            spontaniumCombustum();
        }).growX();

        ImageButton b = i.get();
        TUElements.boxTooltip(b, "@tu-tooltip.button-seppuku");
        b.setDisabled(() -> player.unit() == null || player.unit().type == UnitTypes.block);
        b.update(() -> {
            if(b.isPressed()){
                sTimer += Time.delta;
                if(sTimer > TUVars.longPress){
                    spontaniumCombustum();
                }
            }else{
                sTimer = 0f;
            }

            kill.clearChildren();
            kill.add(unit);
            kill.add(knife);
            b.replaceImage(kill);
            b.resizeImage(40f);
        });
        b.setColor(player.team().color != null ? player.team().color : TUVars.curTeam.color);

        return i;
    }

    public static void add(Table table){
        table.table(Tex.buttonEdge3, t -> {
            seppuku(t).size(TUVars.iconSize, 40);
        });
    }
}
