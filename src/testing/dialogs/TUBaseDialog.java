package testing.dialogs;

import mindustry.ui.dialogs.*;
import testing.util.*;

public class TUBaseDialog extends BaseDialog{
    public TUBaseDialog(String title){
        super(title);

        shown(() -> {
            TUVars.activeDialog = this;
        });
    }
}
