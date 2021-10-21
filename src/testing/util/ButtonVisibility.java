package testing.util;

import arc.func.*;
import mindustry.input.*;

import static mindustry.Vars.*;

public class ButtonVisibility{
    /** Extra checks for mobile (disappear when the cancel button appears) */
    public static Boolp mobileChecks = () -> {
        if(player.unit().isBuilding()) return false;
        if(control.input instanceof MobileInput m){
            return m.block == null &&
                m.mode != PlaceMode.breaking &&
                (m.selectRequests.isEmpty() || m.lastSchematic == null);
        }
        return true;
    };

    public static Boolp unfoldedVisibility = () -> {
        if(
            TUVars.folded ||
            !ui.hudfrag.shown ||
            ui.minimapfrag.shown()
        ) return false;

        if(!mobile) return true;

        return mobileChecks.get();
    };

    public static Boolp foldedVisibility = () -> {
        if(
            !TUVars.folded ||
            !ui.hudfrag.shown ||
            ui.minimapfrag.shown()
        ) return false;

        if(!mobile) return true;

        return mobileChecks.get();
    };
}