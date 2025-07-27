package testing.util;

import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.scene.utils.*;
import arc.util.*;
import blui.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.ui.dialogs.SettingsMenuDialog.*;
import mindustry.ui.dialogs.SettingsMenuDialog.SettingsTable.*;
import mindustry.world.meta.*;

import static arc.Core.*;
import static mindustry.Vars.*;
import static testing.ui.TUDialogs.*;

public class TUSettings{
    public static void init(){
        ui.settings.addCategory(bundle.get("setting.tu-title"), "test-utils-settings-icon", t -> {
            t.pref(new Banner("test-utils-settings-banner", -1));
            t.checkPref("tu-instakill", true);
            t.checkPref("tu-death-effect", true);
            t.checkPref("tu-despawns", true);
            t.checkPref("tu-permanent", false);
            t.checkPref("tu-show-hidden", false);
            t.checkPref("tu-fill-all", false);
            t.checkPref("tu-wu-coords", true);
            t.checkPref("tu-tile-info", false);
            t.pref(new TeamSetting("tu-default-team"));
            t.pref(new Separator(8));
            t.pref(new ButtonSetting("tu-interp", TUIcons.get(Icon.line), () -> interpDialog.show()));
            t.sliderPref("tu-lerp-time", 8, 0, 40, s -> Strings.autoFixed(s / 4f, 2) + " " + StatUnit.seconds.localized());
            t.pref(new Separator(8));
            t.pref(new ButtonSetting("tu-sounds", TUIcons.get(Icon.effect), () -> soundDialog.show()));
            t.checkPref("tu-music-enabled", false);
            t.checkPref("tu-allow-filters", false);

            if(OS.username.startsWith("MEEP")){
                t.pref(new Separator(8));
                t.checkPref("tu-mobile-test", false);
            }
        });

        if(mobile) ui.settings.game.checkPref("console", true);
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
            b.resizeImage(BLVars.iconSize);
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
            ImageButton b = table.button(TUIcons.get(Icon.defense), BLVars.iconSize, () -> teamDialog.show(getTeam(), team -> settings.put("tu-default-team", team.id))).left().padTop(3f).get();
            b.label(() -> bundle.format("setting." + name + ".name", "[#" + getTeam().color + "]" + teamDialog.teamName(getTeam()) + "[]")).padLeft(6).growX();
            table.row();

            addDesc(b);
        }

        public Team getTeam(){
            return Team.get(settings.getInt("tu-default-team", Team.sharded.id));
        }
    }
}
