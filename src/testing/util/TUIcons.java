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
    alpha, shard;

    public static void init(){
        clone = get("clone");
        seppuku = get("seppuku");
        core = get("core");
        dump = get("dump");
        survival = get("survival");
        sandbox = get("sandbox");
        heal = get("heal");
        invincibility = get("invincibility");
        alpha = new TextureRegionDrawable(UnitTypes.alpha.uiIcon);
        shard = new TextureRegionDrawable(Blocks.coreShard.uiIcon);
    }

    static TextureRegionDrawable get(String name){
        return new TextureRegionDrawable(Core.atlas.find("test-utils-" + name));
    }
}
