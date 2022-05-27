package testing;

import arc.*;
import arc.func.*;
import arc.graphics.g2d.*;
import arc.scene.ui.*;
import arc.util.*;
import mindustry.game.EventType.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.mod.*;
import mindustry.mod.Mods.*;
import mindustry.ui.dialogs.SettingsMenuDialog.*;
import mindustry.ui.dialogs.SettingsMenuDialog.SettingsTable.*;
import testing.buttons.*;
import testing.content.*;
import testing.content.TUFx.*;
import testing.ui.*;
import testing.util.*;

import static arc.Core.*;
import static mindustry.Vars.*;
import static testing.ui.TUDialogs.*;

public class TestUtils extends Mod{
    boolean teleport;

    public TestUtils(){
        if(!headless){
            enableConsole =  experimental = true; //Dev mode
            renderer.minZoom = 0.667f; //Zoom out farther
            renderer.maxZoom = 24f; //Get a closer look at yourself

            if(settings.getBool("tu-mobile-test", false)) mobile = testMobile = true;

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
            Events.on(WorldLoadEvent.class, e -> SpawnMenu.spawnHover = SpawnMenu.blockHover = false);
            Events.run(Trigger.draw, () -> {
                unitDialog.drawPos();
                blockDialog.drawPos();
                Draw.reset();
            });

            Events.run(Trigger.update, () -> {
                if(!disableCampaign() && state.isGame() && !player.unit().type.internal &&
                    input.ctrl() && input.alt() && input.isTouched()
                ){
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
            });
        }
    }

    void loadSettings(){
        ui.settings.addCategory(bundle.get("setting.tu-title"), "test-utils-settings-icon", t -> {
            t.pref(new TeamSetting("tu-default-team"));
            t.checkPref("tu-instakill", true);
            t.checkPref("tu-despawns", true);
            t.checkPref("tu-permanent", false);
            t.checkPref("tu-show-hidden", false);
            t.checkPref("tu-fill-all", false);
            t.sliderPref("tu-long-press", 2, 1, 12, s -> Strings.autoFixed(s / 4f, 2));

            if(OS.username.equals("MEEP")) t.checkPref("tu-mobile-test", false);
        });
    }

    public static boolean disableCampaign(){
        return state.isCampaign() && !OS.username.equals("MEEP");
    }

    static class TeamSetting extends Setting{
        public TeamSetting(String name){
            super(name);
            title = "setting." + name + ".name";
        }

        @Override
        public void add(SettingsTable table){
            ImageButton b = table.button(Icon.defense, () -> teamDialog.show(getTeam(), team -> settings.put("tu-default-team", team.id))).get();
            b.label(() -> bundle.format(title, "[#" + getTeam().color + "]" + teamDialog.teamName(getTeam()) + "[]")).padLeft(6).growX();
            table.row();

            addDesc(b);
        }

        public static Team getTeam(){
            return Team.get(settings.getInt("tu-default-team", Team.sharded.id));
        }
    }
}
