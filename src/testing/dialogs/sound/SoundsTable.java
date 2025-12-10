package testing.dialogs.sound;

import arc.*;
import arc.audio.*;
import arc.graphics.*;
import arc.math.*;
import arc.scene.ui.*;
import arc.scene.ui.TextField.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import blui.ui.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import testing.ui.*;
import testing.util.*;

import static arc.Core.*;
import static testing.dialogs.sound.LoadedSounds.*;

public class SoundsTable extends STable{
    private final AudioBus soundRoomBus;
    private final Table selection = new Table();
    private TextField search;
    private Sound sound = Sounds.none;
    private int loopSoundID = -1;

    private float minVol = 1, maxVol = 1, minPitch = 0.8f, maxPitch = 1.2f;
    private float loopVol = 1, loopPitch = 1;

    public SoundsTable(AudioBus soundRoomBus){
        this.soundRoomBus = soundRoomBus;
    }

    public void createSelection(Table t, TextField search){
        this.search = search;

        t.label(() -> bundle.get("tu-menu.selection") + getSoundName(sound)).padBottom(6).left().row();

        t.pane(all -> all.add(selection).growX());

        rebuild();
    }

    public void createPlay(Table t){
        BLElements.divider(t, "@tu-sound-menu.sound", Pal.accent);
        t.table(s -> {
            s.button(Icon.play, () -> {
                AudioBus prev = sound.bus;
                sound.setBus(soundRoomBus);
                sound.play(Mathf.random(minVol, maxVol), Mathf.random(minPitch, maxPitch), 0f, false, false);
                sound.setBus(prev);
            }).grow().center().right();
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
            }).padLeft(6f).left();
        }).row();
        BLElements.divider(t, "@tu-sound-menu.sound-loop", Pal.accent);
        t.table(l -> {
            l.defaults().left();

            l.button(Icon.play, () -> {
                AudioBus prev = sound.bus;
                sound.setBus(soundRoomBus);
                loopSoundID = sound.loop(loopVol, loopPitch, 0);
                sound.setBus(prev);
            }).disabled(b -> loopSoundID >= 0).uniform().grow();
            l.button(TUIcons.stop, () -> {
                Core.audio.stop(loopSoundID);
                loopSoundID = -1;
            }).disabled(b -> loopSoundID < 0).uniform().grow();

            l.add("@tu-sound-menu.vol").padLeft(6f).growX();
            l.field("" + loopVol, TextFieldFilter.floatsOnly, v -> {
                loopVol = Strings.parseFloat(v);
                if(loopSoundID >= 0){
                    Core.audio.setVolume(loopSoundID, loopVol);
                }
            }).padLeft(6f).growX();
            l.add("@tu-sound-menu.pitch").padLeft(6f).growX();
            l.field("" + loopPitch, TextFieldFilter.floatsOnly, v -> {
                loopPitch = Strings.parseFloat(v);
                if(loopSoundID >= 0){
                    Core.audio.setPitch(loopSoundID, loopPitch);
                }
            }).padLeft(6f).growX();
        });
    }

    public void rebuild(){
        selection.clear();
        String text = search.getText();

        selection.table(list -> {
            Seq<Sound> vSounds = vanillaSounds.select(s -> getSoundName(s).toLowerCase().contains(text.toLowerCase()));
            if(vSounds.size > 0){
                BLElements.divider(list, "@tu-sound-menu.vanilla", Pal.accent);

                list.table(v -> vanillaSoundList(v, vSounds)).growX();
                list.row();
            }

            Seq<Sound> mSounds = modSounds.select(s -> getSoundName(s).toLowerCase().contains(text.toLowerCase()));
            if(mSounds.size > 0){
                BLElements.divider(list, "@tu-sound-menu.modded", Pal.accent);

                list.table(m -> modSoundList(m, mSounds)).growX();
            }
        }).growX().padBottom(10);
    }

    public void vanillaSoundList(Table t, Seq<Sound> sounds){
        int cols = 4;
        int count = 0;
        for(Sound s : sounds){
            TextButton sb = t.button(getSoundName(s), () -> {
                stopSounds();
                sound = s;
            }).uniformX().grow().checked(b -> sound == s).get();
            sb.setStyle(TUStyles.toggleCentert);

            if(soundOverrides.containsKey(s)){
                sb.setDisabled(true);
                BLElements.boxTooltip(sb, bundle.format("tu-sound-menu.sound-overwritten", soundOverrides.get(s)));
            }

            if((++count) % cols == 0){
                t.row();
            }
        }
    }

    public void modSoundList(Table t, Seq<Sound> sounds){
        int cols = 4;
        int count = 0;
        String lastMod = null;
        for(Sound s : sounds){
            String curMod = soundMods.get(s);
            if(!curMod.equals(lastMod)){
                lastMod = curMod;
                if(count % cols != 0) t.row();
                count = 0;
                BLElements.divider(t, curMod, Color.lightGray, 4);
                t.row();
            }

            t.button(getSoundName(s), () -> {
                stopSounds();
                sound = s;
            }).uniformX().grow().checked(b -> sound == s)
                .get().setStyle(TUStyles.toggleCentert);

            if((++count) % cols == 0){
                t.row();
            }
        }
    }

    public void stopSounds(){
        sound.stop();

        if(loopSoundID >= 0){
            Core.audio.stop(loopSoundID);
            loopSoundID = -1;
        }
    }
}
