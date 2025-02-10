package testing;

import arc.*;
import arc.func.*;
import arc.graphics.g2d.*;
import arc.input.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import mindustry.game.EventType.*;
import mindustry.graphics.*;
import mindustry.mod.*;
import mindustry.mod.Mods.*;
import testing.buttons.*;
import testing.content.*;
import testing.content.TUFx.*;
import testing.ui.*;
import testing.util.*;

import static arc.Core.*;
import static mindustry.Vars.*;
import static testing.ui.TUDialogs.*;

public class TestUtils extends Mod{
    private static boolean teleport, hasProc;

    public TestUtils(){
        if(settings.getBool("tu-mobile-test", false)) mobile = testMobile = true;

        if(mobile) loadLogger();

        //Add campaign maps to custom maps list
        Seq<String> mapNames = new Seq<>();
        mapNames.addAll( //Sectors aren't loaded yet, need to hardcode
            "groundZero",
            "craters", "biomassFacility", "frozenForest", "ruinousShores", "windsweptIslands", "stainedMountains", "tarFields",
            "fungalPass", "extractionOutpost", "saltFlats", "overgrowth",
            "impact0078", "desolateRift", "nuclearComplex", "planetaryTerminal",
            "coastline", "navalFortress",

            "onset", "aegis", "lake", "intersect", "basin", "atlas", "split", "marsh", "peaks", "ravine", "caldera-erekir",
            "stronghold", "crevice", "siege", "crossroads", "karst", "origin"
        );
        mapNames.addAll((String[])Reflect.get(maps.getClass(), "defaultMapNames"));
        Reflect.set(maps.getClass(), "defaultMapNames", mapNames.toArray(String.class));
    }

    @Override
    public void init(){
        TUIcons.init();
        TUStyles.init();
        TUSettings.init();
        Setup.init();

        LoadedMod tu = mods.locateMod("test-utils");

        Func<String, String> getModBundle = value -> bundle.get("mod." + value);

        tu.meta.displayName = "[#FCC21B]" + tu.meta.displayName;
        tu.meta.author = "[#FCC21B]" + tu.meta.author;

        StringBuilder tools = new StringBuilder(getModBundle.get(tu.meta.name + ".description"));
        tools.append("\n\n");
        int i = 0;
        while(bundle.has("mod." + tu.meta.name + "-tool." + i)){
            tools.append("\n    ").append(getModBundle.get(tu.meta.name + "-tool." + i));
            i++;
        }
        tu.meta.description = tools.toString();

        setupZoom();

        //Spawn position drawing and sk7725/whynotteleport. (Anything beyond here does not have mobile support.)
        if(mobile) return;
        Events.on(WorldLoadEvent.class, e -> {
            Spawn.spawnHover = Spawn.blockHover = false;
        });
        Events.run(Trigger.draw, () -> {
            Draw.z(Layer.overlayUI + 0.04f);
            unitDialog.drawPos();
            blockDialog.drawPos();
            if(!teleport && canTeleport()){
                Draw.z(Layer.effect);
                Lines.stroke(2f, Pal.accent);
                float x1 = player.x, y1 = player.y,
                    x2 = input.mouseWorldX(), y2 = input.mouseWorldY();

                Lines.line(x1, y1, x2, y2, false);
                Fill.circle(x1, y1, 1f);
                Fill.circle(x2, y2, 1f);

                for(int j = 0; j < 4; j++){
                    float rot = j * 90f + 45f + (-Time.time) % 360f;
                    float length = 8f;
                    Draw.rect("select-arrow", x2 + Angles.trnsx(rot, length), y2 + Angles.trnsy(rot, length), length / 1.9f, length / 1.9f, rot - 135f);
                }
            }
            Draw.reset();
        });
        Events.run(Trigger.update, () -> {
            if(state.isGame()){
                //sk7725/whynotteleport
                if(canTeleport() && click()){
                    player.shooting(false);
                    if(teleport) return;
                    teleport = true;

                    float oldX = player.x, oldY = player.y;

                    player.unit().set(input.mouseWorld());
                    player.snapInterpolation();

                    TUFx.teleport.at(
                        input.mouseWorldX(), input.mouseWorldY(),
                        player.unit().rotation - 90f, player.team().color,
                        new TPData(player.unit().type, oldX, oldY)
                    );
                }else{
                    teleport = false;
                }
            }
        });
    }

    private static void setupZoom(){
        if(settings.getBool("tu-disable-zoom", false)){
            settings.put("tu-disable-zoom", false);
            return;
        }

        //Increase zoom range
        renderer.minZoom = 0.667f; //Zoom out farther
        renderer.maxZoom = 24f; //Zoom in closer
        Events.run(Trigger.update, () -> {
            if(state.isGame()){
                if(control.input.logicCutscene){ //Dynamically change zoom range to not break cutscene zoom
                    renderer.minZoom = 1.5f;
                    renderer.maxZoom = 6f;
                }else{
                    renderer.minZoom = 0.667f;
                    renderer.maxZoom = 24f;
                }
            }
        });
    }

    public static boolean disableTeleport(){
        return TUVars.foos || net.client() || disableCampaign();
    }

    public static boolean canTeleport(){
        return !mobile && !disableTeleport() && !player.unit().type.internal && input.alt();
    }

    public static boolean disableCampaign(){
        return state.isCampaign() && !(settings.getBool("tu-cheating"));
    }

    public static boolean click(){
        return mobile ? input.isTouched() : input.keyDown(KeyCode.mouseLeft);
    }

    public static boolean anyClick(){
        return mobile ? input.isTouched() : (input.keyDown(KeyCode.mouseLeft) || input.keyDown(KeyCode.mouseRight) || input.keyDown(KeyCode.mouseMiddle));
    }

    public static KeyCode getClick(){
        if(input.keyDown(KeyCode.mouseLeft)) return KeyCode.mouseLeft;
        if(input.keyDown(KeyCode.mouseRight)) return KeyCode.mouseRight;
        if(input.keyDown(KeyCode.mouseMiddle)) return KeyCode.mouseLeft;
        return null;
    }
}
