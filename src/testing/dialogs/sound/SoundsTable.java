package testing.dialogs.sound;

import arc.*;
import arc.audio.*;
import arc.math.*;
import arc.scene.ui.*;
import arc.scene.ui.TextField.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import testing.ui.*;

import static arc.Core.*;

public class SoundsTable{
    private static Seq<Sound> vanillaSounds;
    private static Seq<Sound> modSounds;

    private final AudioBus soundRoomBus;
    private final Table selection = new Table();
    private TextField search;
    private Sound sound = Sounds.pew;
    private int loopSoundID = -1;

    private float minVol = 1, maxVol = 1, minPitch = 0.8f, maxPitch = 1.2f;
    private float loopVol = 1, loopPitch = 1;

    public SoundsTable(AudioBus soundRoomBus){
        this.soundRoomBus = soundRoomBus;
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
    }

    public void createSelection(Table t, TextField search){
        this.search = search;

        t.label(() -> bundle.get("tu-menu.selection") + getName(sound)).padBottom(6).row();

        t.pane(all -> all.add(selection)).row();
    }

    public void createPlay(Table t){
        t.defaults().left();
        TUElements.divider(t, "@tu-sound-menu.sound", Pal.accent);
        t.table(s -> {
            s.button("@tu-sound-menu.play", () -> {
                AudioBus prev = sound.bus;
                sound.setBus(soundRoomBus);
                sound.play(Mathf.random(minVol, maxVol), Mathf.random(minPitch, maxPitch), 0f, false, false);
                sound.setBus(prev);
            }).wrapLabel(false).grow();
            s.table(f -> {
                f.defaults().left().growX();
                f.add("@tu-sound-menu.min-vol");
                TextField[] maxVolF = {null};
                f.field("" + minVol, TextFieldFilter.floatsOnly, v -> {
                    minVol = Strings.parseFloat(v);
                    if(minVol > maxVol){
                        maxVol = minVol;
                        maxVolF[0].setText("" + maxVol);
                    }
                }).padLeft(6f);
                f.add("-").padLeft(6f).padRight(6f);
                f.add("@tu-sound-menu.max-vol").padLeft(6f);
                maxVolF[0] = f.field("" + maxVol, TextFieldFilter.floatsOnly, v -> maxVol = Strings.parseFloat(v)).get();
                maxVolF[0].setValidator(v -> Strings.parseFloat(v) >= minVol);
                f.row();
                f.add("@tu-sound-menu.min-pitch");
                TextField[] maxPitchF = {null};
                f.field("" + minPitch, TextFieldFilter.floatsOnly, v -> {
                    minPitch = Strings.parseFloat(v);
                    if(minPitch > maxPitch){
                        maxPitch = minPitch;
                        maxPitchF[0].setText("" + maxPitch);
                    }
                });
                f.add("-").padLeft(6f).padRight(6f);
                f.add("@tu-sound-menu.max-pitch").padLeft(6f);
                maxPitchF[0] = f.field("" + maxPitch, TextFieldFilter.floatsOnly, v -> maxPitch = Strings.parseFloat(v)).get();
                maxPitchF[0].setValidator(v -> Strings.parseFloat(v) >= minPitch);
            }).padLeft(6f);
        }).center().row();
        TUElements.divider(t, "@tu-sound-menu.sound-loop", Pal.accent);
        t.table(l -> {
            l.defaults().left();

            l.button("@tu-sound-menu.start", () -> {
                AudioBus prev = sound.bus;
                sound.setBus(soundRoomBus);
                loopSoundID = sound.loop(loopVol, loopPitch, 0);
                sound.setBus(prev);
            }).wrapLabel(false).disabled(b -> loopSoundID >= 0).uniform().grow();

            l.add("@tu-sound-menu.vol").padLeft(6f).growX();
            l.field("" + loopVol, TextFieldFilter.floatsOnly, v -> {
                loopVol = Strings.parseFloat(v);
                if(loopSoundID >= 0){
                    Core.audio.setVolume(loopSoundID, loopVol);
                }
            }).padLeft(6f).growX();

            l.row();

            l.button("@tu-sound-menu.stop", () -> {
                Core.audio.stop(loopSoundID);
                loopSoundID = -1;
            }).wrapLabel(false).disabled(b -> loopSoundID < 0).uniform().grow();

            l.add("@tu-sound-menu.pitch").padLeft(6f).growX();
            l.field("" + loopPitch, TextFieldFilter.floatsOnly, v -> {
                loopPitch = Strings.parseFloat(v);
                if(loopSoundID >= 0){
                    Core.audio.setPitch(loopSoundID, loopPitch);
                }
            }).padLeft(6f).growX();
        }).center().row();
    }

    public void rebuild(){
        selection.clear();
        String text = search.getText();

        selection.table(list -> {
            Seq<Sound> vSounds = vanillaSounds.select(s -> getName(s).toLowerCase().contains(text.toLowerCase()));
            if(vSounds.size > 0){
                TUElements.divider(list, "@tu-sound-menu.vanilla", Pal.accent);

                list.table(v -> {
                    soundList(v, vSounds);
                });
                list.row();
            }

            Seq<Sound> mSounds = modSounds.select(s -> getName(s).toLowerCase().contains(text.toLowerCase()));
            if(mSounds.size > 0){
                TUElements.divider(list, "@tu-sound-menu.modded", Pal.accent);

                list.table(m -> {
                    soundList(m, mSounds);
                });
            }
        }).growX().left().padBottom(10);
    }

    public void soundList(Table t, Seq<Sound> sounds){
        int cols = 4;
        int count = 0;
        for(Sound s : sounds){
            t.button(getName(s), () -> {
                stopSounds();
                sound = s;
            }).uniform().grow().wrapLabel(false);

            if((++count) % cols == 0){
                t.row();
            }
        }
    }

    public String getName(Sound s){
        String full = s.toString();
        return full.substring(full.lastIndexOf("/") + 1);
    }

    public void stopSounds(){
        sound.stop();

        if(loopSoundID >= 0){
            Core.audio.stop(loopSoundID);
            loopSoundID = -1;
        }
    }
}
