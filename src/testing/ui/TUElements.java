package testing.ui;

import arc.func.*;
import arc.graphics.*;
import arc.scene.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.TextField.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.gen.*;
import testing.util.*;

public class TUElements{
    public static void sliderSet(Table t, Cons<String> changed, Prov<String> fieldText, TextFieldFilter filter, TextFieldValidator valid, float min, float max, float step, float def, Cons2<Float, TextField> sliderChanged, String title, String tooltip){
        TextField field = textField(String.valueOf(def), changed, fieldText, filter, valid);

        Label tab = t.add(title).right().padRight(6f).get();
        Slider sl = t.slider(min, max, step, def, s -> sliderChanged.get(s, field)).right().width(TUVars.sliderWidth).get();
        TextField f = t.add(field).left().padLeft(6f).width(TUVars.fieldWidth).get();

        if(tooltip != null){
            Tooltip tip = new Tooltip(to -> to.background(Tex.button).add(tooltip));
            tab.addListener(tip);
            sl.addListener(tip);
            f.addListener(tip);
        }
    }

    public static TextField textField(String text, Cons<String> changed, Prov<String> setText, TextFieldFilter filter, TextFieldValidator valid){
        TextField field = new TextField(text);
        if(filter != null) field.setFilter(filter);
        if(valid != null) field.setValidator(valid);
        field.changed(() -> changed.get(field.getText()));
        if(setText != null){
            field.update(() -> {
                Scene stage = field.getScene();
                if(!(stage != null && stage.getKeyboardFocus() == field))
                    field.setText(setText.get());
            });
        }

        return field;
    }

    public static Stack itemImage(TextureRegionDrawable region, Prov<CharSequence> text){
        return itemImage(region, text, Color.white, Color.white, 1f, Align.bottomLeft);
    }

    public static Stack itemImage(TextureRegionDrawable region, Prov<CharSequence> text, Color icolor, Color tcolor, float fontScl, int align){
        Stack stack = new Stack();

        Table t = new Table().align(align);
        t.label(text).color(tcolor).fontScale(fontScl);

        Image i = new Image(region).setScaling(Scaling.fit);
        i.setColor(icolor);

        stack.add(i);
        stack.add(t);
        return stack;
    }

    public static void boxTooltip(Element e, Prov<CharSequence> text){
        e.addListener(new Tooltip(t -> t.background(Tex.button).label(text)));
    }

    public static void boxTooltip(Element e, String text){
        e.addListener(new Tooltip(t -> t.background(Tex.button).add(text)));
    }
}
