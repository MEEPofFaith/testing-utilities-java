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
import mindustry.*;
import mindustry.core.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import testing.ui.*;

import static arc.Core.*;

public class MusicsTable extends STable{
    private static Seq<Music> vanillaMusic;
    private static Seq<Music> modMusic;
    private static final String[] vanillaMusicNames = {
        "menu", "launch", "land", "editor",
        "game1", "game2", "game3", "game4", "fine", "game5",
        "game6", "game7", "game8", "game8", "game9",
        "boss1", "boss2"
    };

    private final Table selection = new Table();
    private TextField search;
    private MusicProgressBar progressBar;
    private boolean paused;
    private float targetTime = 0f;
    private boolean queued = false;
    private Music selectedMusic = Musics.menu;
    protected Music playingMusic = null;

    public MusicsTable(){
        if(modMusic == null){ //Only grab musics once
            modMusic = new Seq<>();
            Core.assets.getAll(Music.class, modMusic);
            vanillaMusic = new Seq<>();
            for(String m : vanillaMusicNames){
                int index = modMusic.indexOf(mus -> mus.toString().contains(m));
                if(index == -1) continue;
                vanillaMusic.add(modMusic.get(index));
                modMusic.remove(index);
            }
        }
    }

    public void createSelection(Table t, TextField search){
        this.search = search;

        t.label(() -> bundle.get("tu-menu.selection") + getName(selectedMusic)).padBottom(6).left().row();

        t.pane(all -> all.add(selection).growX()).row();

        rebuild();
    }

    public void createPlay(Table t){
        TUElements.divider(t, "@tu-sound-menu.music", Pal.accent);
        t.table(s -> {
            s.label(() -> "Now playing: " + getName(playingMusic)).left();
            s.row();
            s.add(progressBar = new MusicProgressBar(this)).growX();
            s.row();
            s.table(p -> {
                p.button("@tu-sound-menu.play", () -> play(selectedMusic)).wrapLabel(false).grow();
                p.button("@tu-sound-menu.pause", this::pause).wrapLabel(false).grow();
                p.button("@tu-sound-menu.stop", this::stopSounds).wrapLabel(false).grow();
            });
        }).growX();
    }

    public void rebuild(){
        selection.clear();
        String text = search.getText();

        selection.table(list -> {
            Seq<Music> vSounds = vanillaMusic.select(s -> getName(s).toLowerCase().contains(text.toLowerCase()));
            if(vSounds.size > 0){
                TUElements.divider(list, "@tu-sound-menu.vanilla", Pal.accent);

                list.table(v -> {
                    musicList(v, vSounds);
                }).growX();
                list.row();
            }

            Seq<Music> mSounds = modMusic.select(s -> getName(s).toLowerCase().contains(text.toLowerCase()));
            if(mSounds.size > 0){
                TUElements.divider(list, "@tu-sound-menu.modded", Pal.accent);

                list.table(m -> {
                    musicList(m, mSounds);
                }).growX();
            }
        }).growX().padBottom(10);
    }

    public void musicList(Table t, Seq<Music> sounds){
        int cols = 4;
        int count = 0;
        for(Music s : sounds){
            t.button(getName(s), () -> {
                selectedMusic = s;
            }).uniform().grow().wrapLabel(false);

            if((++count) % cols == 0){
                t.row();
            }
        }
    }

    public String getName(Music s){
        if(s == null) return "none";
        String full = s.toString();
        return full.substring(full.lastIndexOf("/") + 1);
    }

    private void play(Music music){
        if(playingMusic != null) playingMusic.stop();
        if(playingMusic != music) paused = false;
        playingMusic = music;

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
        progressBar.musicLength = length;
    }

    private void pause(){
        if(playingMusic != null){
            paused = true;
            playingMusic.pause(true);
            targetTime = playingMusic.getPosition();
        }
    }

    public void stopSounds(){
        play(null);
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
        if(!paused && !queued) targetTime = playingMusic.getPosition();
    }

    private void setTime(Music m){
        if(!m.isPlaying() && !paused) m.play();
        m.setPosition(targetTime);
        if(!queued && !Mathf.equal(m.getPosition(), targetTime)){
            queued = true;
            app.post(() -> {
                queued = false;
                setTime(m);
            });
        }
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
                    float prog = x / width;
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
