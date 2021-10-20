package testing.content;

import arc.*;
import arc.scene.*;
import arc.scene.ui.layout.*;
import mindustry.game.EventType.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.*;
import mindustry.ui.dialogs.SettingsMenuDialog.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class TUSettings{
    public static SettingsTable tuSettings;

    public static void init(){
        BaseDialog dialog = new BaseDialog("@setting.tu-title");
        dialog.addCloseButton();

        tuSettings = new SettingsTable();
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