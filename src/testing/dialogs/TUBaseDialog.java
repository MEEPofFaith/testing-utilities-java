package testing.dialogs;

import arc.*;
import mindustry.game.EventType.*;
import mindustry.ui.dialogs.*;
import testing.util.*;

public class TUBaseDialog extends BaseDialog{
    public TUBaseDialog(String title){
        super(title);

        shouldPause = false;
        addCloseButton();
        shown(this::rebuild);
        onResize(this::rebuild);
        shown(() -> {
            TUVars.activeDialog = this;
        });

        Events.on(GameOverEvent.class, e -> hide());
    }

    protected void rebuild(){
    }
}
