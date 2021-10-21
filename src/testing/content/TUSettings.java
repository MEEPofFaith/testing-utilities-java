package testing.content;

import arc.*;
import arc.scene.*;
import arc.scene.ui.layout.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.*;
import mindustry.ui.dialogs.SettingsMenuDialog.*;
import testing.buttons.dialogs.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class TUSettings{
    public static SettingsTable tuSettings;
    static TeamSettingDialog teamSettingDialog;

    public static void init(){
        BaseDialog dialog = new BaseDialog("@setting.tu-title");
        dialog.addCloseButton();

        teamSettingDialog = new TeamSettingDialog();

        tuSettings = new SettingsTable(){
            @Override
            public void rebuild(){
                clearChildren();

                for(Setting setting : list){
                    setting.add(this);
                }

                button(Icon.defense, teamSettingDialog::show).get()
                    .label(() -> bundle.format("tu-unit-menu.team-default", "[#" + teamSettingDialog.getTeam().color + "]" + teamSettingDialog.teamName() + "[]")).padLeft(6).growX();
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

        dialog.cont.center().add(tuSettings);

        Events.on(ResizeEvent.class, event -> {
            if(dialog.isShown() && Core.scene.getDialog() == dialog){
                dialog.updateScrollFocus();
            }
        });

        ui.settings.shown(() -> {
            Table settingUi = (Table)((Group)((Group)(ui.settings.getChildren().get(1))).getChildren().get(0)).getChildren().get(0); //This looks so stupid lol
            settingUi.row();
            settingUi.button("@setting.tu-title", Styles.cleart, dialog::show);
        });
    }
}