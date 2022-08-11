package testing.dialogs;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import arc.util.pooling.*;
import mindustry.gen.*;
import mindustry.ui.*;

public class InterpGraph extends Table{
    static final float lerpTime = 120f;
    int points;
    Interp oldInterp = Interp.linear;
    Interp interp = Interp.linear;
    float oldMinVal = 0f, oldMaxVal = 1f,
        minVal = 0f, maxVal = 1f;
    float lerp = 1;

    public InterpGraph(){
        background(Tex.pane);

        rect((x, y, width, height) -> {
            Lines.stroke(Scl.scl(3f));

            GlyphLayout lay = Pools.obtain(GlyphLayout.class, GlyphLayout::new);
            Font font = Fonts.outline;

            lay.setText(font, "-0.00");

            float min = min(), max = max(), range = max - min;
            float offsetX = Scl.scl(lay.width + 6f), offsetY = Scl.scl(5f);

            float graphX = x + offsetX, graphW = width - offsetX;
            float baseY = y + offsetY, baseH = height - offsetY;
            float graphY = baseY + baseH * (-min / range), graphH = baseH - baseH * ((-min + (max - 1)) / range);
            points = Mathf.round(graphW / 10, 2) + 1; //Ensure a center (0.5) point
            float spacing = graphW / (points - 1);

            Draw.color(Color.lightGray);
            Lines.line(graphX, graphY, graphX + graphW, graphY);
            Lines.line(graphX, graphY + graphH, graphX + graphW, graphY + graphH);

            if(range != 1){
                Lines.line(graphX, baseY, graphX + graphW, baseY);
                Lines.line(graphX, baseY + baseH, graphX + graphW, baseY + baseH);
            }

            Draw.color(Color.red);
            Lines.beginLine();
            for(int i = 0; i < points; i++){
                float a = i / (points - 1f);
                float cx = graphX + i * spacing, cy = graphY + applyInterp(a) * graphH;
                Lines.linePoint(cx, cy);
            }
            Lines.endLine();

            lay.setText(font, "0.00");
            font.draw("0.00", graphX, graphY + lay.height / 2f, Align.right);
            lay.setText(font, "1.00");
            font.draw("1.00", graphX, graphY + graphH + lay.height / 2f, Align.right);

            if(range != 1){
                String s = Strings.fixed(min, 2);
                lay.setText(font, s);
                font.draw(s, graphX, baseY + lay.height / 2f, Align.right);
                s = Strings.fixed(max, 2);
                lay.setText(font, s);
                font.draw(s, graphX, baseY + baseH + lay.height / 2f, Align.right);
            }

            font.setColor(Color.white);
            Pools.free(lay);

            Draw.reset();
        }).pad(4).padBottom(10).grow();

        update(() -> {
            if(lerp < 1){
                float t = Core.settings.getInt("tu-lerp-time") / 4f * 60f;
                if(t <= 0){
                    lerp = 1;
                }else{
                    lerp = Mathf.clamp(lerp + Time.delta / t);
                }
            }
        });
    }

    float applyInterp(float a){
        if(lerp >= 1){
            return interp.apply(a);
        }
        return Mathf.lerp(oldInterp.apply(a), interp.apply(a), lerp());
    }

    float min(){
        if(lerp >= 1){
            return minVal;
        }
        return Mathf.lerp(oldMinVal, minVal, lerp());
    }

    float max(){
        if(lerp >= 1){
            return maxVal;
        }
        return Mathf.lerp(oldMaxVal, maxVal, lerp());
    }

    float lerp(){
        return Interp.smoother.apply(lerp);
    }

    public void setInterp(Interp newInterp){
        if(lerp < 1){
            Interp o = oldInterp;
            Interp i = interp;
            float l = Interp.smoother.apply(lerp);

            oldInterp = a -> Mathf.lerp(o.apply(a), i.apply(a), l);

            oldMinVal = min();
            oldMaxVal = max();
        }else{
            oldInterp = interp;
            oldMinVal = minVal;
            oldMaxVal = maxVal;
        }

        interp = newInterp;
        lerp = 0;

        minVal = Float.MAX_VALUE;
        maxVal = Float.MIN_VALUE;
        for(int i = 0; i < points; i++){
            float v = newInterp.apply(i / (points - 1f));
            if(v < minVal){
                minVal = v;
            }
            if(v > maxVal){
                maxVal = v;
            }
        }
    }
}
