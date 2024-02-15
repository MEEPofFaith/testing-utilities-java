package testing.util;

import arc.*;
import arc.scene.style.*;
import mindustry.content.*;

public class TUIcons{
    public static TextureRegionDrawable
    clone, seppuku,
    core, dump,
    survival, sandbox,
    heal, invincibility,
    weather,
    sounds, musics, stop,
    alpha;

    public static void init(){
        clone = get("clone");
        seppuku = get("seppuku");
        core = get("core");
        dump = get("dump");
        survival = get("survival");
        sandbox = get("sandbox");
        heal = get("heal");
        invincibility = get("invincibility");
        weather = get("weather");
        sounds = get("sounds");
        musics = get("musics");
        stop = get("stop");
        alpha = new TextureRegionDrawable(UnitTypes.alpha.uiIcon);
    }

    static TextureRegionDrawable get(String name){
        return new TextureRegionDrawable(Core.atlas.find("test-utils-" + name));
    }

    public static TextureRegionDrawable get(TextureRegionDrawable icon){
        return new TextureRegionDrawable(icon);
    }
}
