package testing;

import arc.*;
import arc.func.*;
import arc.graphics.g2d.*;
import arc.input.*;
import arc.math.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.scene.utils.*;
import arc.struct.*;
import arc.util.*;
import mindustry.game.EventType.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.mod.*;
import mindustry.mod.Mods.*;
import mindustry.ui.dialogs.SettingsMenuDialog.*;
import mindustry.ui.dialogs.SettingsMenuDialog.SettingsTable.*;
import mindustry.world.meta.*;
import testing.buttons.*;
import testing.content.*;
import testing.content.TUFx.*;
import testing.ui.*;
import testing.util.*;

import static arc.Core.*;
import static mindustry.Vars.*;
import static testing.ui.TUDialogs.*;

public class TestUtils extends Mod{
    static boolean teleport, hasProc;

    public TestUtils(){
        if(!headless){
            if(settings.getBool("tu-mobile-test", false)) mobile = testMobile = true;

            if(mobile){
                loadLogger();
            }

            experimental = true; //Also dev mode

            Events.on(ClientLoadEvent.class, e -> {
                TUIcons.init();
                TUStyles.init();
                loadSettings();
                Setup.init();
            });

            //Add campaign maps to custom maps list
            Seq<String> mapNames = new Seq<>();
            mapNames.addAll( //Sectors aren't loaded yet, need to hardcode
                "groundZero",
                "craters", "biomassFacility", "frozenForest", "ruinousShores", "windsweptIslands", "stainedMountains", "tarFields",
                "fungalPass", "extractionOutpost", "saltFlats", "overgrowth",
                "impact0078", "desolateRift", "nuclearComplex", "planetaryTerminal",
                "coastline", "navalFortress",

                "onset", "aegis", "lake", "intersect", "basin", "atlas", "split", "marsh", "peaks", "ravine",
                "stronghold", "crevice", "siege", "crossroads", "karst", "origin"
            );
            mapNames.addAll((String[])Reflect.get(maps.getClass(), "defaultMapNames"));
            Reflect.set(maps.getClass(), "defaultMapNames", mapNames.toArray(String.class));
        }
    }

    @Override
    public void init(){
        if(!headless){
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

            //Increase zoom range
            renderer.minZoom = 0.667f; //Zoom out farther
            renderer.maxZoom = 24f; //Get a closer look at yourself
            Events.on(WorldLoadEvent.class, e -> {
                //reset
                hasProc = Groups.build.contains(b -> b.block.privileged);
                renderer.minZoom = 0.667f;
                renderer.maxZoom = 24f;
            });
            Events.run(Trigger.update, () -> {
                //zomm range
                if(hasProc){
                    if(control.input.logicCutscene){ //Dynamically change zoom range to not break cutscene zoom
                        renderer.minZoom = 1.5f;
                        renderer.maxZoom = 6f;
                    }else{
                        renderer.minZoom = 0.667f;
                        renderer.maxZoom = 24f;
                    }
                }
            });

            //position drawing + sk7725/whynotteleport
            if(mobile) return;
            Events.on(WorldLoadEvent.class, e -> {
                Spawn.spawnHover = Spawn.blockHover = false;
            });
            Events.run(Trigger.draw, () -> {
                Draw.z(Layer.endPixeled);
                unitDialog.drawPos();
                blockDialog.drawPos();
                Setup.terrainFrag.drawPos();
                if(!teleport && !disableTeleport() && !player.unit().type.internal && input.alt()){
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
                    if(!disableTeleport() && !player.unit().type.internal && input.alt() && click()){
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
    }

    void loadSettings(){
        ui.settings.addCategory(bundle.get("setting.tu-title"), "test-utils-settings-icon", t -> {
            t.pref(new Banner("test-utils-settings-banner", -1));
            t.sliderPref("tu-long-press", 2, 1, 12, s -> {
                TUVars.longPress = s * 60f / 4f;
                return Strings.autoFixed(s / 4f, 2) + " " + StatUnit.seconds.localized();
            });
            t.checkPref("tu-instakill", true);
            t.checkPref("tu-despawns", true);
            t.checkPref("tu-permanent", false);
            t.checkPref("tu-show-hidden", false);
            t.checkPref("tu-fill-all", false);
            t.pref(new TeamSetting("tu-default-team"));
            t.pref(new Separator(8));
            t.pref(new ButtonSetting("tu-interp", TUIcons.get(Icon.line), () -> interpDialog.show()));
            t.sliderPref("tu-lerp-time", 8, 0, 40, s -> Strings.autoFixed(s / 4f, 2) + " " + StatUnit.seconds.localized());
            t.pref(new Separator(8));
            t.pref(new ButtonSetting("tu-sounds", TUIcons.get(Icon.effect), () -> soundDialog.show()));

            if(OS.username.equals("MEEP")){
                t.pref(new Separator(8));
                t.checkPref("tu-meep-privileges", true);
                t.checkPref("tu-mobile-test", false);
            }
        });

        if(mobile) ui.settings.game.checkPref("console", true);
    }

    public static boolean disableTeleport(){
        return net.client() ? !Setup.on2r2t : disableCampaign();
    }

    public static boolean disableCampaign(){
        return state.isCampaign() && !(OS.username.equals("MEEP") && settings.getBool("tu-meep-privileges"));
    }

    public static boolean click(){
        return mobile ? input.isTouched() : input.keyDown(KeyCode.mouseLeft);
    }

    /** Not a setting, but rather adds an image to the settings menu. */
    static class Banner extends Setting{
        float width;

        public Banner(String name, float width){
            super(name);
            this.width = width;
        }

        @Override
        public void add(SettingsTable table){
            Image i = new Image(new TextureRegionDrawable(atlas.find(name)), Scaling.fit);
            Cell<Image> ci = table.add(i).padTop(3f);

            if(width > 0){
                ci.width(width);
            }else{
                ci.grow();
            }

            table.row();
        }
    }

    /** Not a setting, but rather a space between settings. */
    static class Separator extends Setting{
        float height;

        public Separator(float height){
            super("");
            this.height = height;
        }

        @Override
        public void add(SettingsTable table){
            table.image(Tex.clear).height(height).padTop(3f);
            table.row();
        }
    }

    /** Not a setting, but rather a button in the settings menu. */
    static class ButtonSetting extends Setting{
        Drawable icon;
        Runnable listener;

        public ButtonSetting(String name, Drawable icon, Runnable listener){
            super(name);
            this.icon = icon;
            this.listener = listener;
        }

        @Override
        public void add(SettingsTable table){
            ImageButton b = Elem.newImageButton(icon, listener);
            b.resizeImage(TUVars.iconSize);
            b.label(() -> title).padLeft(6).growX();
            b.left();

            addDesc(table.add(b).left().padTop(3f).get());
            table.row();
        }
    }

    static class TeamSetting extends Setting{
        public TeamSetting(String name){
            super(name);
        }

        @Override
        public void add(SettingsTable table){
            ImageButton b = table.button(TUIcons.get(Icon.defense), TUVars.iconSize, () -> teamDialog.show(getTeam(), team -> settings.put("tu-default-team", team.id))).left().padTop(3f).get();
            b.label(() -> bundle.format("setting." + name + ".name", "[#" + getTeam().color + "]" + teamDialog.teamName(getTeam()) + "[]")).padLeft(6).growX();
            table.row();

            addDesc(b);
        }

        public Team getTeam(){
            return Team.get(settings.getInt("tu-default-team", Team.sharded.id));
        }
    }
}
