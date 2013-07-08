import java.applet.*;
import java.net.*;
import java.util.*;


public class SoundLib {
	Hashtable<String, AudioClip> sounds; //speichert AudioClips
	Vector<AudioClip> loopingClips; //speichert die AudioClips die in Schleife laufen
	
	public SoundLib(){
		sounds = new Hashtable<String, AudioClip>(); //Instanzierungen
		loopingClips = new Vector<AudioClip>();
	}
	/**
	 * Lädt einen Sound in die Soundlib
	 * @param name Name der zu übergebenen Sounddatei
	 * @param path Speicherort der Sounddatei
	 */
	public void loadSound(String name, String path){ //lädt die audioClips
		if(sounds.containsKey(name)){//Wenn Sound schon geladen, Abbruch
			return;
		}
		URL sound_url = getClass().getClassLoader().getResource(path);
		sounds.put(name, (AudioClip)Applet.newAudioClip(sound_url));
	}
	/**
	 * Spielt einen einzelnen Sound ab
	 * @param name Name der Sounddatei
	 */
	public void playSound(String name){ //Methode zum einmaligen Abspielen eines Sounds
		AudioClip audio = sounds.get(name);
		audio.play();
	}
	/**
	 * Spielt einen Sound in Dauerschleife ab.
	 * @param name Name der Tondatei
	 */
	public void loopSound(String name){ //Methode zum permanenten Abspielen eines AudioClips
		AudioClip audio = sounds.get(name);
		loopingClips.add(audio);
		audio.loop();
	}
	/**
	 * Beendet die Dauerschleife der Hintergrundmusik
	 */
	public void stopLoopingSound(){ //AudioClips können beendet werden
		for(AudioClip c:loopingClips){
			c.stop();
		}
	}
	
}
