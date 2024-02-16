package testing.dialogs.sound;

import arc.*;
import arc.audio.*;
import arc.files.*;
import arc.graphics.*;
import arc.math.*;
import arc.scene.ui.*;
import arc.scene.ui.TextField.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import testing.ui.*;
import testing.util.*;

import java.util.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class SoundsTable extends STable{
    private static Seq<Sound> vanillaSounds;
    private static Seq<Sound> modSounds;
    private static ObjectMap<Sound, String> overrides;
    private static ObjectMap<Sound, String> soundMods;

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
            overrides = new ObjectMap<>();
            soundMods = new ObjectMap<>();
            String mDir = "sounds/";
            Vars.mods.eachEnabled(m -> {
                Fi musicFolder = m.root.child("sounds");
                String mName = m.meta.displayName;
                if(musicFolder.exists() && musicFolder.isDirectory()){
                    musicFolder.walk(f -> {
                        String ext = f.extension();
                        if(ext.equals("mp3") || ext.equals("ogg")){
                            //Check for override
                            int vanillaIndex = vanillaSounds.indexOf(s -> getName(s).equals(f.name()));
                            if(vanillaIndex != -1){
                                Sound overwritten = vanillaSounds.get(vanillaIndex);
                                modSounds.addUnique(overwritten);
                                overrides.put(overwritten, mName);
                                soundMods.put(overwritten, mName);
                            }else{ //Add
                                String path = f.pathWithoutExtension();
                                int folderIndex = f.pathWithoutExtension().indexOf(mDir);
                                Sound sou = tree.loadSound(path.substring(folderIndex + mDir.length()));
                                modSounds.addUnique(sou);
                                soundMods.put(sou, mName);
                            }
                        }
                    });
                }
            });
            modSounds.sort(Comparator.comparing(o -> soundMods.get(o)));
        }
    }

    public void createSelection(Table t, TextField search){
        this.search = search;

        t.label(() -> bundle.get("tu-menu.selection") + getName(sound)).padBottom(6).left().row();

        t.pane(all -> all.add(selection).growX());

        rebuild();
    }

    public void createPlay(Table t){
        TUElements.divider(t, "@tu-sound-menu.sound", Pal.accent);
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
        TUElements.divider(t, "@tu-sound-menu.sound-loop", Pal.accent);
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
            Seq<Sound> vSounds = vanillaSounds.select(s -> getName(s).toLowerCase().contains(text.toLowerCase()));
            if(vSounds.size > 0){
                TUElements.divider(list, "@tu-sound-menu.vanilla", Pal.accent);

                list.table(v -> {
                    vanillaSoundList(v, vSounds);
                }).growX();
                list.row();
            }

            Seq<Sound> mSounds = modSounds.select(s -> getName(s).toLowerCase().contains(text.toLowerCase()));
            if(mSounds.size > 0){
                TUElements.divider(list, "@tu-sound-menu.modded", Pal.accent);

                list.table(m -> {
                    modSoundList(m, mSounds);
                }).growX();
            }
        }).growX().padBottom(10);
    }

    public void vanillaSoundList(Table t, Seq<Sound> sounds){
        int cols = 4;
        int count = 0;
        for(Sound s : sounds){
            TextButton sb = t.button(getName(s), () -> {
                stopSounds();
                sound = s;
            }).uniformX().grow().checked(b -> sound == s).get();
            sb.getStyle().checked = Tex.flatDownBase;

            if(overrides.containsKey(s)){
                sb.setDisabled(true);
                TUElements.boxTooltip(sb, bundle.format("tu-sound-menu.sound-overwritten", overrides.get(s)));
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
                TUElements.divider(t, curMod, Color.lightGray, 4);
                t.row();
            }

            t.button(getName(s), () -> {
                stopSounds();
                sound = s;
            }).uniformX().grow().checked(b -> sound == s)
                .get().getStyle().checked = Tex.flatDownBase;

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
