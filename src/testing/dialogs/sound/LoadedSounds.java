package testing.dialogs.sound;

import arc.audio.*;
import arc.files.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.gen.*;

import static arc.Core.settings;
import static mindustry.Vars.*;

public class LoadedSounds{
    protected static final Seq<Music> vanillaMusic = Seq.with(
        Musics.menu, Musics.launch, Musics.land, Musics.editor,
        Musics.game1, Musics.game2, Musics.game3, Musics.game4, Musics.fine,
        Musics.game5, Musics.game6, Musics.game7, Musics.game8, Musics.game9,
        Musics.boss1, Musics.boss2
    );

    protected static Seq<Sound> vanillaSounds;
    protected static Seq<Sound> modSounds;
    protected static ObjectMap<Sound, String> soundOverrides;
    protected static ObjectMap<Sound, String> soundMods;

    protected static Seq<Music> modMusic;
    protected static ObjectMap<Music, String> musicOverrides;
    protected static ObjectMap<Music, String> musicMods;

    protected static void init(){
        if(vanillaSounds != null) return;

        vanillaSounds = new Seq<>();
        int i = 0;
        while(true){ //Put vanilla sounds first
            Sound found = Sounds.getSound(i);
            if(found == null || found == Sounds.none) break;

            vanillaSounds.addUnique(found);
            i++;
        }

        modSounds = new Seq<>();
        soundOverrides = new ObjectMap<>();
        soundMods = new ObjectMap<>();

        String sDir = "sounds/";
        Vars.mods.eachEnabled(m -> {
            Fi musicFolder = m.root.child("sounds");
            String mName = m.meta.displayName;
            if(musicFolder.exists() && musicFolder.isDirectory()){
                musicFolder.walk(f -> {
                    String ext = f.extension();
                    if(ext.equals("mp3") || ext.equals("ogg")){
                        //Check for override
                        int vanillaIndex = vanillaSounds.indexOf(s -> getSoundName(s).equals(f.nameWithoutExtension()));
                        if(vanillaIndex != -1){
                            Sound overwritten = vanillaSounds.get(vanillaIndex);
                            modSounds.addUnique(overwritten);
                            soundOverrides.put(overwritten, mName);
                            soundMods.put(overwritten, mName);
                        }else{ //Add
                            String path = f.pathWithoutExtension();
                            int folderIndex = f.pathWithoutExtension().indexOf(sDir);
                            String loc = path.substring(folderIndex + sDir.length());
                            //assets.getAssetType(loc) seems to always be null for some reason. Use a try/catch instead I guess.
                            try{ //Prevent crash if non-Sound file is in sounds/
                                Sound sou = tree.loadSound(loc);
                                modSounds.addUnique(sou);
                                soundMods.put(sou, mName);
                            }catch(Exception ignored){
                                Log.warn("[TU] File @ is not a Sound", path);
                            }
                        }
                    }
                });
            }
        });
        modSounds.sort(Structs.comparing(o -> soundMods.get(o)));

        if(!settings.getBool("tu-music-enabled", false)) return;

        //For some reason modded music is not included in assets.getAll. Walk through mod files instead.
        modMusic = new Seq<>();
        musicOverrides = new ObjectMap<>();
        musicMods = new ObjectMap<>();

        String mDir = "music/";
        Vars.mods.eachEnabled(m -> {
            Fi musicFolder = m.root.child("music");
            String mName = m.meta.displayName;
            if(musicFolder.exists() && musicFolder.isDirectory()){
                musicFolder.walk(f -> {
                    String ext = f.extension();
                    if(ext.equals("mp3") || ext.equals("ogg")){
                        //Check for override
                        int vanillaIndex = vanillaMusic.indexOf(s -> getMusicName(s).equals(f.nameWithoutExtension()));
                        if(vanillaIndex != -1){
                            Music overwritten = vanillaMusic.get(vanillaIndex);
                            modMusic.addUnique(overwritten);
                            musicOverrides.put(overwritten, mName);
                            musicMods.put(overwritten, mName);
                        }else{ //Add
                            String path = f.pathWithoutExtension();
                            int folderIndex = f.pathWithoutExtension().indexOf(mDir);
                            String loc = path.substring(folderIndex + mDir.length());
                            try{ //Why a non-music file would be in musics is beyond me, but just in case.
                                Music mus = tree.loadMusic(loc);
                                modMusic.addUnique(mus);
                                musicMods.put(mus, mName);
                            }catch(Exception ignored){
                                Log.warn("[TU] File @ is not a Music", path);
                            }
                        }
                    }
                });
            }
        });
        modMusic.sort(Structs.comparing(o -> musicMods.get(o)));
    }

    protected static String getSoundName(Sound s){
        String full = s.toString();
        return full.substring(full.lastIndexOf("/") + 1, full.length() - 4);
    }

    protected static String getMusicName(Music s){
        if(s == null) return "none";
        String full = s.toString();
        return full.substring(full.lastIndexOf("/") + 1, full.length() - 4);
    }
}
