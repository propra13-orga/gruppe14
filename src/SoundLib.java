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
	
	public void loadSound(String name, String path){ //lädt die audioClips
		if(sounds.containsKey(name)){//Wenn Sound schon geladen, Abbruch
			return;
		}
		URL sound_url = getClass().getClassLoader().getResource(path);
		sounds.put(name, (AudioClip)Applet.newAudioClip(sound_url));
	}
	
	public void playSound(String name){ //Methode zum einmaligen Abspielen eines Sounds
		AudioClip audio = sounds.get(name);
		audio.play();
	}
	
	public void loopSound(String name){ //Methode zum permanenten Abspielen eines AudioClips
		AudioClip audio = sounds.get(name);
		loopingClips.add(audio);
		audio.loop();
	}
	
	public void stopLoopingSound(){ //AudioClips können beendet werden
		for(AudioClip c:loopingClips){
			c.stop();
		}
	}
	
}
