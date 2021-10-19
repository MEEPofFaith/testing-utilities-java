package testing.content;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import mindustry.entities.*;
import mindustry.graphics.*;

public class TUFx{
    public static Effect

    iconEffect = new Effect(60f, e -> {
        if(e.data instanceof String s){
            float rise = e.finpow() * 28f;
            float opacity = Mathf.curve(e.fin(), 0f, 0.2f) - Mathf.curve(e.fin(), 0.9f, 1f);
            Draw.alpha(opacity);
            Draw.rect(Core.atlas.find(s), e.x, e.y + rise);
        }
    }).layer(Layer.flyingUnit + 1);
}
