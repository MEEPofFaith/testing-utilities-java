package testing.content;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;

public class TUFx{
    private static final Rand rand = new Rand();

    public static Effect

    iconEffect = new Effect(60f, e -> {
        if(e.data instanceof String s){
            float rise = e.finpow() * 28f;
            float opacity = Mathf.curve(e.fin(), 0f, 0.2f) - Mathf.curve(e.fin(), 0.9f, 1f);
            Draw.alpha(opacity);
            Draw.rect(Core.atlas.find(s), e.x, e.y + rise);
        }
    }).layer(Layer.flyingUnit + 1),

    deathLightning = new Effect(20f, 300f, e -> {
        if(!(e.data instanceof Unit u)) return;
        rand.setSeed(e.id);
        Tmp.v1.setToRandomDirection(rand).setLength(u.hitSize * 0.75f * Mathf.sqrt(rand.random(1f)));
        Tmp.v2.trns(rand.random(-45f, 45f) + 90f, u.hitSize * (1f + rand.random(1f)));
        float tx = u.x + Tmp.v1.x, ty = u.y + Tmp.v1.y,
            ex = tx + Tmp.v2.x, ey = ty + Tmp.v2.y,
            dst = Mathf.dst(ex, ey, tx, ty);
        Tmp.v1.set(u).sub(ex, ey).nor();

        float normx = Tmp.v1.x, normy = Tmp.v1.y;
        float range = 6f;
        int links = Mathf.ceil(dst / range);
        float spacing = dst / links;

        Lines.stroke(2.5f * e.fout());
        Draw.color(Color.white, e.color, e.fin());

        Lines.beginLine();

        Lines.linePoint(ex, ey);

        rand.setSeed(e.id + 1L);

        for(int i = 0; i < links; i++){
            float nx, ny;
            if(i == links - 1){
                nx = tx;
                ny = ty;
            }else{
                float len = (i + 1) * spacing;
                Tmp.v1.setToRandomDirection(rand).scl(range/2f);
                nx = ex + normx * len + Tmp.v1.x;
                ny = ey + normy * len + Tmp.v1.y;
            }

            Lines.linePoint(nx, ny);
        }

        Lines.endLine();
    }).followParent(false);
}
