package testing.dialogs;

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
    }

    protected void rebuild(){
    }
}
