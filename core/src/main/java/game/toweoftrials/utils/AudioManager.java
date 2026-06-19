package game.toweoftrials.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.ObjectMap;

public class AudioManager {
    private static Music currentMusic;
    private static final ObjectMap<String, Sound> sounds = new ObjectMap<>();
    private static final ObjectMap<String, Music> musicTracks = new ObjectMap<>();
    private static float soundVolume = 1.0f;
    private static float musicVolume = 0.5f;

    public static void load() {
        // Music
        musicTracks.put("menu", Gdx.audio.newMusic(Gdx.files.internal("audio/background_music/menu.ogg")));
        musicTracks.put("dungeon", Gdx.audio.newMusic(Gdx.files.internal("audio/background_music/dungeon.ogg")));
        musicTracks.put("boss", Gdx.audio.newMusic(Gdx.files.internal("audio/background_music/boss.ogg")));

        // Sounds
        sounds.put("attack", Gdx.audio.newSound(Gdx.files.internal("audio/battle/22_Slash_04.wav")));
        sounds.put("death", Gdx.audio.newSound(Gdx.files.internal("audio/battle/69_Enemy_death_01.wav")));
        sounds.put("flee", Gdx.audio.newSound(Gdx.files.internal("audio/battle/51_Flee_02.wav")));
        sounds.put("hover", Gdx.audio.newSound(Gdx.files.internal("audio/ui/001_Hover_01.wav")));
        sounds.put("confirm", Gdx.audio.newSound(Gdx.files.internal("audio/ui/013_Confirm_03.wav")));
        sounds.put("decline", Gdx.audio.newSound(Gdx.files.internal("audio/ui/029_Decline_09.wav")));
        sounds.put("denied", Gdx.audio.newSound(Gdx.files.internal("audio/ui/033_Denied_03.wav")));
        sounds.put("equip", Gdx.audio.newSound(Gdx.files.internal("audio/ui/070_Equip_10.wav")));
        sounds.put("unequip", Gdx.audio.newSound(Gdx.files.internal("audio/ui/071_Unequip_01.wav")));
        sounds.put("heal", Gdx.audio.newSound(Gdx.files.internal("audio/skills/02_Heal_02.wav")));
        sounds.put("buff", Gdx.audio.newSound(Gdx.files.internal("audio/skills/16_Atk_buff_04.wav")));
        sounds.put("debuff", Gdx.audio.newSound(Gdx.files.internal("audio/skills/21_Debuff_01.wav")));
    }

    public static void playSound(String name) {
        if (sounds.containsKey(name)) {
            sounds.get(name).play(soundVolume);
        }
    }

    public static void playMusic(String name) {
        if (musicTracks.containsKey(name)) {
            if (currentMusic != null && currentMusic.isPlaying()) {
                currentMusic.stop();
            }
            currentMusic = musicTracks.get(name);
            currentMusic.setVolume(musicVolume);
            currentMusic.setLooping(true);
            currentMusic.play();
        }
    }

    public static void stopMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
        }
    }

    public static void setSoundVolume(float volume) {
        soundVolume = volume;
    }

    public static float getSoundVolume() {
        return soundVolume;
    }

    public static void setMusicVolume(float volume) {
        musicVolume = volume;
        if (currentMusic != null) {
            currentMusic.setVolume(musicVolume);
        }
    }

    public static float getMusicVolume() {
        return musicVolume;
    }

    public static void dispose() {
        for (Sound sound : sounds.values()) {
            sound.dispose();
        }
        for (Music music : musicTracks.values()) {
            music.dispose();
        }
        sounds.clear();
        musicTracks.clear();
    }
}
