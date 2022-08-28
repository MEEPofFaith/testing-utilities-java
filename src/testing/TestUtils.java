package testing;

import arc.*;
import arc.func.*;
import arc.graphics.g2d.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.scene.utils.*;
import arc.util.*;
import mindustry.game.EventType.*;
import mindustry.game.*;
import mindustry.gen.*;
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
    boolean teleport, hasProc;

    public TestUtils(){
        if(!headless){
            if(settings.getBool("tu-mobile-test", false)) mobile = testMobile = true;

            if(mobile){
                loadLogger();
            }

            experimental = true; //Also dev mode
            renderer.minZoom = 0.667f; //Zoom out farther
            renderer.maxZoom = 24f; //Get a closer look at yourself

            Events.on(ClientLoadEvent.class, e -> {
                TUIcons.init();
                TUStyles.init();
                loadSettings();
                Setup.init();
            });
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

            //position drawing + sk7725/whynotteleport
            if(mobile) return;
            Events.on(WorldLoadEvent.class, e -> {
                Spawn.spawnHover = Spawn.blockHover = false;

                //reset
                hasProc = Groups.build.contains(b -> b.block.privileged);
                renderer.minZoom = 0.667f;
                renderer.maxZoom = 24f;
            });
            Events.run(Trigger.draw, () -> {
                unitDialog.drawPos();
                blockDialog.drawPos();
                Draw.reset();
            });

            Events.run(Trigger.update, () -> {
                if(state.isGame()){
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

                    //sk7725/whynotteleport
                    if(!disableCampaign() && !player.unit().type.internal && input.ctrl() && input.alt() && input.isTouched()){
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
            t.sliderPref("tu-long-press", 2, 1, 12, s -> Strings.autoFixed(s / 4f, 2) + " " + StatUnit.seconds.localized());
            t.checkPref("tu-instakill", true);
            t.checkPref("tu-despawns", true);
            t.checkPref("tu-permanent", false);
            t.checkPref("tu-show-hidden", false);
            t.checkPref("tu-fill-all", false);
            t.pref(new TeamSetting("tu-default-team"));
            t.pref(new Separator(8));
            t.pref(new ButtonSetting("tu-interp", TUIcons.get(Icon.line), () -> interpDialog.show()));
            t.sliderPref("tu-lerp-time", 8, 0, 40, s -> Strings.autoFixed(s / 4f, 2) + " " + StatUnit.seconds.localized());

            if(OS.username.equals("MEEP")){
                t.pref(new Separator(8));
                t.checkPref("tu-mobile-test", false);
            }
        });

        ui.settings.game.checkPref("console", true); //Dev Mode
    }

    public static boolean disableCampaign(){
        return state.isCampaign() && !OS.username.equals("MEEP");
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
