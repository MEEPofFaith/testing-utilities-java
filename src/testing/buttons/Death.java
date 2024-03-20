package testing.buttons;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.gen.*;
import mindustry.type.*;
import testing.content.*;
import testing.ui.*;
import testing.util.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class Death{
    /** <i><b>SPONTANIUM COMBUSTUM!</b> That's a spell that makes the person who said it <b>e x p l o -</b></i> */
    public static void spontaniumCombustum(){
        Unit u = player.unit();
        if(net.client()){ //For 2r2t
            Utils.runCommand("die");
            killLightning();
        }else{
            boolean insta = settings.getBool("tu-instakill");
            if(input.shift()){
                if(insta){
                    Utils.copyJS("""
                        let u = Vars.player.unit();
                        u.elevation = 0;
                        u.health = -1;
                        u.dead = true;
                        u.kill();"""
                    );
                }else{
                    Utils.copyJS("Vars.player.unit().kill();");
                }
                return;
            }
            if(u != null){
                if(insta){
                    u.elevation(0);
                    u.health(-1);
                    u.dead(true);
                }
                u.kill();
                killLightning();
            }
        }
    }

    public static void killLightning(){
        Unit u = player.unit();
        if(u != null){
            for(int i = 0; i < Math.max(1f, u.hitSize / 4f); i++){
                TUFx.deathLightning.at(u, true);
            }
        }
    }

    public static void mitosis(){
        if(input.shift()){
            Utils.copyJS("""
                let u = Vars.player.unit();
                u.type.spawn(u.team, u).rotation = u.rotation;"""
            );
            return;
        }

        Unit u = player.unit();
        if(u != null){
            u.type.spawn(u.team, u).rotation(u.rotation);
            Fx.spawn.at(u);
        }
    }

    public static void seppuku(Table t){
        Cell<ImageButton> i = t.button(Icon.units, TUStyles.tuImageStyle, TUVars.iconSize, () -> {
            if(TUVars.pressTimer > TUVars.longPress) return;
            spontaniumCombustum();
        });

        ImageButton b = i.get();

        TUElements.boxTooltip(b, "@tu-tooltip.button-seppuku");
        UnitStack kill = new UnitStack(TUIcons.seppuku);
        b.replaceImage(kill);
        b.getStyle().disabled = TUStyles.buttonCenterDisabled;

        b.setDisabled(() -> player.unit() == null || player.unit().type.internal);
        b.update(() -> {
            if(b.isPressed() && !b.isDisabled() && !net.client()){
                TUVars.pressTimer += TUVars.delta();
                if(TUVars.pressTimer > TUVars.longPress){
                    spontaniumCombustum();
                }
            }
            updateIcon(kill);
            kill.setColor(b.isDisabled() ? Color.gray : Color.white);
        });
        b.released(() -> TUVars.pressTimer = 0);
    }

    public static void clone(Table t){
        Cell<ImageButton> i = t.button(Icon.units, TUStyles.tuImageStyle, TUVars.iconSize, () -> {
            if(TUVars.pressTimer > TUVars.longPress) return;
            mitosis();
        });

        ImageButton b = i.get();

        TUElements.boxTooltip(b, "@tu-tooltip.button-clone");
        UnitStack dupe = new UnitStack(TUIcons.clone);
        b.replaceImage(dupe);
        b.getStyle().disabled = TUStyles.buttonCenterDisabled;

        b.setDisabled(() -> player.unit() == null || player.unit().type.internal);
        b.update(() -> {
            if(b.isPressed() && !b.isDisabled() && !net.client()){
                TUVars.pressTimer += TUVars.delta();
                if(TUVars.pressTimer > TUVars.longPress){
                    mitosis();
                }
            }
            updateIcon(dupe);
            dupe.setColor(b.isDisabled() ? Color.gray : Color.white);
        });
        b.released(() -> TUVars.pressTimer = 0);
        b.replaceImage(dupe);
    }

    public static void addButtons(Table t){
        clone(t);
        seppuku(t);
    }

    private static void updateIcon(UnitStack stack){
        Unit u = player.unit();
        if(u != null && u.type != stack.lastType && !u.type.internal){
            stack.setImage(u.type.uiIcon);
            stack.lastType = u.type;
        }
    }

    private static class UnitStack extends Stack{
        private final Image image, icon;
        public UnitType lastType;

        public UnitStack(TextureRegionDrawable icon){
            image = new Image(UnitTypes.alpha.uiIcon).setScaling(Scaling.fit);
            add(image);
            this.icon = new Image(new TextureRegionDrawable(icon)).setScaling(Scaling.fit);
            add(this.icon);
        }

        public void setImage(TextureRegion unit){
            image.setDrawable(new TextureRegionDrawable(unit));
        }

        @Override
        public void setColor(Color color){
            super.setColor(color);
            image.setColor(color);
            icon.setColor(color);
        }
    }
}
