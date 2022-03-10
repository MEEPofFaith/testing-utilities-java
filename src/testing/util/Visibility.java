package testing.util;

import arc.func.*;
import mindustry.input.*;

import static mindustry.Vars.*;

public class Visibility{
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

    public static Boolp buttonVisibility = () -> {
        if(
            !ui.hudfrag.shown ||
            ui.minimapfrag.shown()
        ) return false;

        if(!mobile) return true;

        return mobileChecks.get();
    };
}
