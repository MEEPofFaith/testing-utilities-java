package testing.util;

import arc.func.*;
import mindustry.input.*;

import static mindustry.Vars.*;

public class ButtonVisibility{
    public static Boolp unfoldedVisibility = () -> {
        if(
            !TUVars.folded ||
            ui.hudfrag.shown ||
            ui.minimapfrag.shown()
        ) return false;

        //if desktop, then all is set. mobile needs some extra conditions checked.
        if(!mobile) return true;

        if(control.input instanceof MobileInput m){
            return !player.unit().isBuilding() &&
                !m.selectedBlock() && !m.isBreaking() &&
                (!m.selectRequests.isEmpty() && m.lastSchematic != null);
        }
        return true;
    };

    public static Boolp foldedVisibility = () -> {
        if(
            TUVars.folded ||
            ui.hudfrag.shown ||
            ui.minimapfrag.shown()
        ) return false;

        //if desktop, then all is set. mobile needs some extra conditions checked.
        if(!mobile) return true;

        if(control.input instanceof MobileInput m){
            return !player.unit().isBuilding() &&
                !m.selectedBlock() && !m.isBreaking() &&
                (!m.selectRequests.isEmpty() && m.lastSchematic != null);
        }
        return true;
    };
}
