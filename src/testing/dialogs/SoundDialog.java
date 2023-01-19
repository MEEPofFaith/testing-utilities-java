package testing.dialogs;

import arc.*;
import arc.audio.*;
import arc.graphics.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import mindustry.gen.*;
import mindustry.graphics.*;

import static arc.Core.bundle;

public class SoundDialog extends TUBaseDialog{
    static Seq<Sound> vanillaSounds;
    static Seq<Sound> modSounds;

    TextField search;
    Table selection = new Table();
    Sound sound = Sounds.pew;

    public SoundDialog(){
        super("@tu-sound-menu.name");

        if(modSounds == null){ //Only grab sounds once
            vanillaSounds = new Seq<>();
            int i = 0;
            while(true){ //Put vanilla sounds first
                Sound found = Sounds.getSound(i);
                if(found == null || found == Sounds.none) break;

                vanillaSounds.addUnique(found);
                i++;
            }

            modSounds = new Seq<>();
            Core.assets.getAll(Sound.class, modSounds);
            modSounds.removeAll(vanillaSounds);
        }

        cont.table(s -> {
            s.image(Icon.zoom).padRight(8);
            search = s.field(null, text -> rebuild()).growX().get();
            search.setMessageText("@players.search");
        }).fillX().padBottom(4).row();

        cont.label(() -> bundle.get("tu-menu.selection") + getName(sound)).padBottom(6).row();

        cont.pane(all -> all.add(selection)).row();

        cont.table(t -> {
            t.button("a", () -> sound.play(69420));
        }).padTop(6);
    }

    @Override
    protected void rebuild(){
        selection.clear();
        String text = search.getText();

        selection.table(list -> {
            Seq<Sound> vSounds = vanillaSounds.select(s -> getName(s).toLowerCase().contains(text.toLowerCase()));
            if(vSounds.size > 0){
                list.add("@tu-sound-menu.vanilla").growX().left().color(Pal.accent);
                list.row();
                list.image().growX().pad(5f).padLeft(0f).padRight(0f).height(3f).color(Pal.accent);
                list.row();

                list.table(v -> {
                    soundList(v, vSounds);
                });
                list.row();
            }

            Seq<Sound> mSounds = modSounds.select(s -> getName(s).toLowerCase().contains(text.toLowerCase()));
            if(mSounds.size > 0){
                list.add("@tu-sound-menu.modded").growX().left().color(Pal.accent);
                list.row();
                list.image().growX().pad(5f).padLeft(0f).padRight(0f).height(3f).color(Pal.accent);
                list.row();

                list.table(m -> {
                    soundList(m, mSounds);
                });
            }
        }).growX().left().padBottom(10);
    }

    void soundList(Table t, Seq<Sound> sounds){
        int cols = 4;
        int count = 0;
        for(Sound s : sounds){
            t.button(getName(s), () -> {
                sound.stop();
                sound = s;
            }).uniform().grow().wrapLabel(false);

            if((++count) % cols == 0){
                t.row();
            }
        }
    }

    String getName(Sound s){
        String full = s.toString();
        while(full.contains("/")){
            full = full.substring(full.indexOf("/") + 1);
        }
        return full;
    }
}
