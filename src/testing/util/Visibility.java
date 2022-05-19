package testing.util;

import arc.func.*;

import static mindustry.Vars.*;

public class Visibility{
    public static Boolp buttonVisibility = () -> ui.hudfrag.shown && !ui.minimapfrag.shown();
}
