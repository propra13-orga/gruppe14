import java.applet.*;
import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.*;

import javax.imageio.ImageIO;

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
		//andernfalls wird Sound geladen
		URL sound_url = getClass().getClassLoader().getResource(path);
		/*if(sound_url == null){
			try{
				sound_url = SoundIO.read(new File(path)); //Über ImageIO das Bild lesen
			} catch (IOException e1){
				System.out.println("Fehler beim Musik laden: " +e1);
				return;
			}
		}*/
		
		System.out.println(sound_url);
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
