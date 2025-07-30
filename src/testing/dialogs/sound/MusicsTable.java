package testing.dialogs.sound;

import arc.*;
import arc.audio.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.input.*;
import arc.math.*;
import arc.scene.*;
import arc.scene.event.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import blui.ui.*;
import mindustry.*;
import mindustry.core.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import testing.ui.*;

import static arc.Core.*;
import static testing.dialogs.sound.LoadedSounds.*;

public class MusicsTable extends STable{
    private final Table selection = new Table();
    private TextField search;
    private MusicProgressBar progressBar;
    private boolean paused;
    private float targetTime = 0f;
    private Music selectedMusic = Musics.menu;
    protected Music playingMusic = null;

    public MusicsTable(){}

    public void createSelection(Table t, TextField search){
        this.search = search;

        t.label(() -> bundle.get("tu-menu.selection") + getMusicName(selectedMusic)).padBottom(6).left().row();

        t.pane(all -> all.add(selection).growX());

        rebuild();
    }

    public void createPlay(Table t){
        BLElements.divider(t, "@tu-sound-menu.music", Pal.accent);
        t.table(s -> {
            s.label(() -> Core.bundle.format("tu-sound-menu.now-playing", getMusicName(playingMusic))).left().colspan(3).padBottom(6f);
            s.row();
            s.button("@tu-sound-menu.switch", () -> switchMusic(selectedMusic)).wrapLabel(false)
                .left().colspan(3).disabled(b -> selectedMusic == playingMusic).get().setStyle(TUStyles.round);
            s.row();
            s.add(progressBar = new MusicProgressBar(this)).growX();
            s.button(Icon.play, () -> play(playingMusic)).disabled(b -> !paused);
            s.button(Icon.pause, this::pause).disabled(b -> paused);
        }).growX();
    }

    public void rebuild(){
        selection.clear();
        String text = search.getText();

        selection.table(list -> {
            Seq<Music> vSounds = vanillaMusic.select(s -> getMusicName(s).toLowerCase().contains(text.toLowerCase()));
            if(vSounds.size > 0){
                BLElements.divider(list, "@tu-sound-menu.vanilla", Pal.accent);

                list.table(v -> vanillaMusicList(v, vSounds)).growX();
                list.row();
            }

            Seq<Music> mSounds = modMusic.select(s -> getMusicName(s).toLowerCase().contains(text.toLowerCase()));
            if(mSounds.size > 0){
                BLElements.divider(list, "@tu-sound-menu.modded", Pal.accent);

                list.table(m -> modMusicList(m, mSounds)).growX();
            }
        }).growX().padBottom(10);
    }

    public void vanillaMusicList(Table t, Seq<Music> musics){
        int cols = 4;
        int count = 0;
        for(Music m : musics){
            TextButton mb = t.button(getMusicName(m), () -> selectedMusic = m)
                .uniformX().grow().checked(b -> selectedMusic == m).get();
            mb.setStyle(TUStyles.toggleCentert);

            if(musicOverrides.containsKey(m)){
                mb.setDisabled(true);
                BLElements.boxTooltip(mb, bundle.format("tu-sound-menu.music-overwritten", musicOverrides.get(m)));
            }

            if((++count) % cols == 0){
                t.row();
            }
        }
    }

    public void modMusicList(Table t, Seq<Music> musics){
        int cols = 4;
        int count = 0;
        String lastMod = null;
        for(Music m : musics){
            String curMod = musicMods.get(m);
            if(!curMod.equals(lastMod)){
                lastMod = curMod;
                if(count % cols != 0) t.row();
                count = 0;
                BLElements.divider(t, curMod, Color.lightGray, 4);
                t.row();
            }

            t.button(getMusicName(m), () -> selectedMusic = m)
                .uniformX().grow().checked(b -> selectedMusic == m)
                .get().setStyle(TUStyles.toggleCentert);

            if((++count) % cols == 0){
                t.row();
            }
        }
    }

    private void switchMusic(Music music){
        if(playingMusic != null) playingMusic.stop();
        if(playingMusic != music) paused = false;
        playingMusic = music;
        play(music);
    }

    private void play(Music music){
        float length = 1f;
        if(music != null){
            music.play();
            music.setVolume(1f);
            music.setLooping(false);
            if(paused){
                paused = false;
                setTime(music);
            }

            length = musicLength(music);
        }
        if(progressBar != null) progressBar.musicLength = length;
    }

    private void pause(){
        if(playingMusic != null){
            paused = true;
            playingMusic.pause(true);
            targetTime = playingMusic.getPosition();
        }
    }

    public void stopSounds(){
        switchMusic(null);
    }

    public void update(){
        Music playing = SoundDialog.soundControlPlaying();
        if(playingMusic != playing){
            Reflect.invoke(Vars.control.sound, "silence");
            if(playing != null) Reflect.invoke(Vars.control.sound, "silence"); //Counteract fade in
        }else{
            if(paused && playing != null) playing.pause(true);
            Reflect.set(Vars.control.sound, "fade", 1f);
        }

        if(playingMusic == null) return;
        playingMusic.setVolume(1f);
        playingMusic.setLooping(false);
        if(!paused) targetTime = playingMusic.getPosition();
    }

    private void setTime(Music m){
        if(!m.isPlaying() && !paused) m.play();
        m.setPosition(targetTime);
    }

    public static float musicLength(Music music){
        return (float)(double)Reflect.invoke(Soloud.class, "streamLength",
            new Object[]{Reflect.get(AudioSource.class, music, "handle")},
            long.class
        );
    }

    private static class MusicProgressBar extends Table{
        public float musicLength = 1f;

        public MusicProgressBar(MusicsTable musicsTable){
            background(Tex.pane);

            Element bar = rect((x, y, width, height) -> {

                Music m = musicsTable.playingMusic;
                float progress = m != null ? musicsTable.targetTime : 0;
                float fin = progress / musicLength;

                Lines.stroke(Scl.scl(3f));
                float mid = y + height / 2f;

                Draw.color(Color.lightGray);
                Lines.line(x, mid, x + width, mid);

                Draw.color(Color.red);
                Lines.line(x, mid, x + width * fin, mid);
                Fill.circle(x + width * fin, mid, 4f);
            }).grow().left().get();
            bar.addListener(new InputListener(){
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, KeyCode button){
                    if(musicsTable.playingMusic == null) return false;
                    calcPos(x);
                    return true;
                }

                @Override
                public void touchDragged(InputEvent event, float x, float y, int pointer){
                    calcPos(x);
                }

                private void calcPos(float x){
                    if(musicsTable.playingMusic == null) musicsTable.play(musicsTable.selectedMusic);

                    float width = bar.getWidth();
                    float prog = Mathf.clamp(x / width);
                    Music m = musicsTable.playingMusic;
                    musicsTable.targetTime = prog * musicLength;
                    musicsTable.setTime(m);
                }
            });
            bar.addListener(new HandCursorListener());

            label(() -> {
                Music m = musicsTable.playingMusic;
                return m != null ?
                    UI.formatTime(musicsTable.targetTime * 60f) + " / " + UI.formatTime(musicLength * 60f) :
                    "x:xx / x:xx";
            }).padLeft(6f).width(128).right().labelAlign(Align.right);
        }
    }
}
