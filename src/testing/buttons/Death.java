package testing.buttons;

import arc.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import testing.ui.*;
import testing.util.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class Death{
    static float sTimer, cTimer;

    static Stack kill = new Stack(), dupe = new Stack();
    static Image unit1 = new Image(), unit2 = new Image(), knife = new Image(), plus = new Image();

    public static void init(){
        knife.setDrawable(TUIcons.seppuku);
        plus.setDrawable(TUIcons.clone);

        unit1.setScaling(Scaling.fit).setSize(TUIcons.seppuku.imageSize());
        unit2.setScaling(Scaling.fit).setSize(TUIcons.clone.imageSize());
        knife.setScaling(Scaling.fit).setSize(TUIcons.seppuku.imageSize());
        plus.setScaling(Scaling.fit).setSize(TUIcons.clone.imageSize());

        Events.run(Trigger.update, () -> {
            if(state.isGame()){
                Unit u = player.unit();
                unit1.setDrawable(u != null ? u.type.uiIcon : Icon.units.getRegion());
                unit2.setDrawable(u != null ? u.type.uiIcon : Icon.units.getRegion());
            }
        });
    }

    /** <i><b>SPONTANIUM COMBUSTUM!</b> That's a spell that makes the person who said it <b>e x p l o -</b></i> */
    public static void spontaniumCombustum(){
        if(Utils.noCheat()){
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
                    if(settings.getBool("tu-instakill")){
                        u.elevation(0);
                        u.health(-1);
                        u.dead(true);
                    }
                    u.kill();
                }
            }
        }
    }

    public static void mitosis(){
        if(Utils.noCheat()){
            if(net.client()){
                Utils.runCommandPlayer("p.unit().type.spawn(p.team(), p.x, p.y);");
            }else{
                Unit u = player.unit();
                if(u != null){
                    u.type.spawn(u.team, u.x, u.y).rotation(u.rotation);
                    Fx.spawn.at(u);
                }
            }
        }
    }

    public static Cell<ImageButton> seppuku(Table t, boolean label){
        Cell<ImageButton> i = t.button(Icon.units, TUStyles.tuRedImageStyle, () -> {
            if(sTimer > TUVars.longPress) return;
            spontaniumCombustum();
        }).growX().tooltip("@tu-tooltip.button-seppuku");
        ImageButton b = i.get();
        b.setDisabled(() -> player.unit() == null || player.unit().type == UnitTypes.block);
        if(!mobile && label) b.label(() -> "[" + (b.isDisabled() ? "gray" : "white") + "]" + bundle.get("tu-ui-button.death")).growX().padLeft(6);
        b.resizeImage(40f);
        b.update(() -> {
            if(b.isPressed()){
                sTimer += graphics.getDeltaTime() * 60f;
                if(sTimer > TUVars.longPress){
                    spontaniumCombustum();
                }
            }else{
                sTimer = 0f;
            }

            kill.clearChildren();
            kill.add(unit1);
            kill.add(knife);
            b.replaceImage(kill);
        });
        b.setColor(player.team().color != null ? player.team().color : TUVars.curTeam.color);

        return i;
    }

    public static Cell<ImageButton> clone(Table t, boolean label){
        Cell<ImageButton> i = t.button(Icon.units, TUStyles.tuRedImageStyle, () -> {
            if(cTimer > TUVars.longPress) return;
            mitosis();
        }).growX().tooltip("@tu-tooltip.button-clone");
        ImageButton b = i.get();
        b.setDisabled(() -> player.unit() == null || player.unit().type == UnitTypes.block || state.isCampaign());
        if(!mobile && label) b.label(() -> "[" + (b.isDisabled() ? "gray" : "white") + "]" + bundle.get("tu-ui-button.clone")).growX().padLeft(6);
        b.resizeImage(40f);
        b.update(() -> {
            if(b.isPressed()){
                cTimer += graphics.getDeltaTime() * 60f;
                if(cTimer > TUVars.longPress){
                    mitosis();
                }
            }else{
                cTimer = 0f;
            }

            b.setColor(player.team().color != null ? player.team().color : TUVars.curTeam.color);

            dupe.clearChildren();
            dupe.add(unit2);
            dupe.add(plus);
            b.replaceImage(dupe);
        });

        return i;
    }

    public static void add(Table[] tables){
        tables[0].table(Tex.buttonEdge3, t -> {
            clone(t, true).size(mobile ? TUVars.iconWidth : 104, 40);
            seppuku(t, true).size(mobile ? TUVars.iconWidth : 140, 40);
        }).padBottom(TUVars.TCOffset + 2 * TUVars.buttonHeight).padLeft(mobile ? TUVars.iconWidth + 20 : 176);

        tables[1].table(Tex.pane, t -> {
            clone(t, false).size(TUVars.iconWidth, 40);
            seppuku(t, false).size(TUVars.iconWidth, 40);
        }).padBottom(TUVars.TCOffset + (mobile ? TUVars.buttonHeight : 0)).padLeft(mobile ? 44 : 120);
    }
}
