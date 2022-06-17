package testing.dialogs;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.TextureAtlas.*;
import arc.graphics.g2d.*;
import arc.scene.ui.*;
import arc.scene.ui.TreeElement.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.ctype.Content.*;
import mindustry.ctype.*;
import mindustry.entities.*;
import mindustry.entities.effect.*;
import mindustry.gen.*;
import mindustry.ui.dialogs.*;
import mindustry.world.meta.*;

import java.lang.reflect.*;

import static mindustry.Vars.*;

/**
 * @author Anuke
 * Modified by me (MEEP) to add more functionaliyy.
 * */
public class FieldEditor extends BaseDialog{
    static Seq<Class<?>> skipFields = Seq.with(ModContentInfo.class, Stats.class);
    static Seq<String> skipFieldNames = Seq.with("name", "iconId");
    static ContentType[] editableContent = {
        ContentType.block,
        ContentType.unit,
        //ContentType.bullet, //TODO make separate editor
        ContentType.item,
        ContentType.liquid,
        //ContentType.weather,
        ContentType.status,
        //ContentType.planet,
        ContentType.sector
    };
    static float height = 50f;

    public FieldEditor(){
        super("a weird sort of editor");

        shouldPause = false;
        addCloseButton();
        closeOnBack();

        shown(this::setup);
    }

    void setup(){
        cont.clear();

        //TODO better menu
        /*TreeElement t = new TreeElement();

        for(ContentType type : editableContent){
            t.add(new TreeElementNode(new Label(type.name())).children(tp -> {
                for(UnlockableContent un : Vars.content.getBy(type).<UnlockableContent>as().select(c -> !c.isHidden())){
                    tp.get(new TreeElementNode(new Label(un.emoji() + " " + un.localizedName)).children(cp -> {
                        Table all = new Table();
                        Class<?> c = un.getClass();

                        for(Field field : c.getFields()){
                            addField(un, all, field);
                        }
                        cp.get(new TreeElementNode(all).hoverable(false));
                    }));
                }
            }));
        }

        cont.pane(t).top().left().grow();*/

        cont.table(Tex.button, t -> {
            for(ContentType type : editableContent){
                //TODO ui similar to  settings ui?
            }
        });
    }

    void addField(Object obj, Table all, Field field){
        if(skipFields.contains(field.getType()) || skipFieldNames.contains(field.getName())) return;

        Class<?> fieldType = field.getType();
        String head = Strings.insertSpaces(Strings.capitalize(field.getName()))/* + "\n[lightgray][[" + fieldType.getSimpleName() + "][] "*/;
        Table res = new Table();
        res.left();
        res.defaults().fillX();
        makeFieldEditor(obj, fieldType, field, res);

        all.defaults().padBottom(2);
        all.add(head).left().uniformY().padRight(8);
        all.add(res).growX().uniformY();

        all.row();
    }

    void makeFieldEditor(Object content, Class<?> type, Field field, Table table){
        if(type == int.class){
            table.field(Reflect.get(content, field) + "", out -> {
                if(Strings.canParseInt(out)){
                    Reflect.set(content, field, Strings.parseInt(out));
                }
            }).size(250f, height).valid(Strings::canParseInt);
        }else if(type == float.class){
            table.field(Reflect.get(content, field) + "", out -> {
                if(Strings.canParseFloat(out)){
                    Reflect.set(content, field, Strings.parseFloat(out));
                }
            }).size(250f, height).valid(Strings::canParseFloat);
        }else if(type == String.class){
            table.field(Reflect.get(content, field), out -> Reflect.set(content, field, out)).size(250f, height);
        }else if(type == boolean.class){
            table.check("", Reflect.get(content, field), val -> Reflect.set(content, field, val)).left();
        }else if(type == Color.class){
            Color out = Reflect.get(content, field);
            if(out == null) return;
            table.table(Tex.pane, in -> {
                in.stack(new Image(Tex.alphaBg), new Image(Tex.whiteui){{
                    update(() -> setColor(out));
                }}).grow();
            }).margin(4).size(height).padRight(10).get().tapped(() -> {
                ui.picker.show(out, out::set);
            });
        }else if(type == TextureRegion.class){
            TextureRegion[] out = {Reflect.get(content, field)};
            if(out[0] == null) out[0] = Core.atlas.find("error");
            table.image(() -> out[0]).padRight(4).size(50f).scaling(Scaling.fit).visible(() -> out[0].found());
            table.field(out[0].found() && out[0] instanceof AtlasRegion ? ((AtlasRegion)out[0]).name : "", res -> {
                if(!res.isEmpty()){
                    out[0] = Core.atlas.find(res);
                    Reflect.set(content, field, out[0]);
                }
            }).valid(t -> Core.atlas.has(t) || t.isEmpty()).size(250f, height).padLeft(4f);
        }else if(type == Effect.class){ //Untested, and I can already tell that this won't work.
            Effect effect = Reflect.get(content, field);
            table.table(t -> {
                for(Field f : effect.getClass().getFields()){
                    addField(effect, t, f);
                }
            });
        }else if(type == Effect[].class){
            Effect[] effects = Reflect.get(content, field);
            for(Effect e : effects){
                table.table(t -> {
                    for(Field f: e.getClass().getFields()){
                        addField(e, t, f);
                    }
                }).padLeft(4f);
            }
        }
    }


}
