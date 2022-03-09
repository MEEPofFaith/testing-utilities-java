package testing.content;

import arc.*;
import arc.scene.*;
import arc.scene.ui.layout.*;
import mindustry.game.EventType.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.*;
import mindustry.ui.dialogs.SettingsMenuDialog.*;

import static arc.Core.*;
import static mindustry.Vars.*;
import static testing.ui.TUDialogs.*;

public class TUSettings{
    public static SettingsTable tuSettings;

    public static void init(){
        BaseDialog dialog = new BaseDialog("@setting.tu-title");
        dialog.addCloseButton();

        tuSettings = new SettingsTable(){
            @Override
            public void rebuild(){
                clearChildren();

                for(Setting setting : list){
                    setting.add(this);
                }

                button(Icon.defense, () -> teamDialog.show(getTeam(), t -> settings.put("tu-default-team", t.id))).get()
                    .label(() -> bundle.format("tu-unit-menu.team-default", "[#" + getTeam().color + "]" + teamDialog.teamName(getTeam()) + "[]")).padLeft(6).growX();
                row().center();

                button(bundle.get("settings.reset", "Reset to Defaults"), () -> {
                    for(Setting setting : list){
                        if(setting.name == null || setting.title == null) continue;
                        settings.put(setting.name, settings.getDefault(setting.name));
                    }
                    rebuild();
                }).margin(14).width(240f).pad(6);
            }
        };
        tuSettings.checkPref("tu-startfolded", false);
        tuSettings.checkPref("tu-instakill", true);
        tuSettings.checkPref("tu-despawns", true);
        tuSettings.checkPref("tu-permanent", false);

        dialog.cont.center().add(tuSettings);

        Events.on(ResizeEvent.class, event -> {
            if(dialog.isShown() && scene.getDialog() == dialog){
                dialog.updateScrollFocus();
            }
        });

        ui.settings.shown(() -> {
            Table settingUi = (Table)((Group)((Group)(ui.settings.getChildren().get(1))).getChildren().get(0)).getChildren().get(0); //This looks so stupid lol
            settingUi.row();
            settingUi.button("@setting.tu-title", Styles.cleart, dialog::show);
        });
    }

    public static Team getTeam(){
        return Team.get(settings.getInt("tu-default-team", 1));
    }
}
