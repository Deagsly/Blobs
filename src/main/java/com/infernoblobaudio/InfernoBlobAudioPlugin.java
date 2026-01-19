package com.infernoblobaudio;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.NPC;
import net.runelite.api.events.ActorDeath;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;
import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@PluginDescriptor(
	name = "Inferno Blob Audio",
	description = "Plays random audio when killing blobs in the Inferno (or cows for testing)",
	tags = {"inferno", "audio", "sound", "blob", "jal-ak", "cow"}
)
public class InfernoBlobAudioPlugin extends Plugin
{
	// Jal-Ak (blob) NPC ID in the Inferno
	private static final int JAL_AK_NPC_ID = 7693;
	
	// Child blob NPC IDs (spawned when Jal-Ak dies)
	// Jal-AkRek-Mej = 7694, Jal-AkRek-Xil = 7695, Jal-AkRek-Ket = 7696
	private static final int[] CHILD_BLOB_NPC_IDS = {7694, 7695, 7696};
	
	// Cow NPC IDs for testing
	private static final int[] COW_NPC_IDS = {2790, 2791, 2792, 2793, 2794, 2795, 2796};
	
	// Calf NPC IDs for testing (child blob equivalent)
	private static final int[] CALF_NPC_IDS = {2793, 2794};
	
	// Audio files for main blobs (random selection)
	private static final String[] MAIN_AUDIO_FILES = {
		"youre-not-that-guy.wav",
		"akh.wav",
		"fart-with-reverb.wav",
		"kids-saying-yay-sound-effect_3.wav",
		"sad-meow-song.wav"
	};
	
	// Specific audio for child blobs
	private static final String CHILD_BLOB_AUDIO = "comedy_pop.wav";

	@Inject
	private InfernoBlobAudioConfig config;

	private List<Clip> mainAudioClips = new ArrayList<>();
	private Clip childBlobClip;
	private Random random = new Random();

	@Override
	protected void startUp() throws Exception
	{
		log.info("Inferno Blob Audio plugin started!");
		loadMainAudioClips();
		loadChildBlobClip();
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Inferno Blob Audio plugin stopped!");
		for (Clip clip : mainAudioClips)
		{
			if (clip != null)
			{
				clip.close();
			}
		}
		mainAudioClips.clear();
		
		if (childBlobClip != null)
		{
			childBlobClip.close();
			childBlobClip = null;
		}
	}

	@Subscribe
	public void onActorDeath(ActorDeath event)
	{
		Actor actor = event.getActor();
		
		if (!(actor instanceof NPC))
		{
			return;
		}
		
		NPC npc = (NPC) actor;
		int npcId = npc.getId();
		
		// Check NPC type
		boolean isMainBlob = npcId == JAL_AK_NPC_ID;
		boolean isChildBlob = isInArray(npcId, CHILD_BLOB_NPC_IDS);
		
		// Cow logic disabled for production
		// boolean isCow = isInArray(npcId, COW_NPC_IDS) && !isInArray(npcId, CALF_NPC_IDS);
		// boolean isCalf = isInArray(npcId, CALF_NPC_IDS);
		
		if (isMainBlob)
		{
			log.debug("Jal-Ak killed! Playing random audio...");
			playRandomMainAudio();
		}
		else if (isChildBlob)
		{
			log.debug("Child blob killed! Playing pop sound...");
			playChildBlobAudio();
		}
	}
	
	private boolean isInArray(int value, int[] array)
	{
		for (int i : array)
		{
			if (i == value)
			{
				return true;
			}
		}
		return false;
	}

	private void loadMainAudioClips()
	{
		for (String audioFile : MAIN_AUDIO_FILES)
		{
			Clip clip = loadAudioClip(audioFile);
			if (clip != null)
			{
				mainAudioClips.add(clip);
			}
		}
		log.info("Loaded {} main audio clips", mainAudioClips.size());
	}
	
	private void loadChildBlobClip()
	{
		childBlobClip = loadAudioClip(CHILD_BLOB_AUDIO);
		if (childBlobClip != null)
		{
			log.info("Loaded child blob audio clip");
		}
	}
	
	private Clip loadAudioClip(String audioFile)
	{
		try
		{
			InputStream audioStream = getClass().getResourceAsStream("/" + audioFile);
			if (audioStream == null)
			{
				log.warn("Could not find audio file: {}", audioFile);
				return null;
			}
			
			// Must use buffered input stream for mark/reset support
			BufferedInputStream bufferedStream = new BufferedInputStream(audioStream);
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bufferedStream);
			
			Clip clip = AudioSystem.getClip();
			clip.open(audioInputStream);
			
			log.info("Loaded audio clip: {}", audioFile);
			return clip;
		}
		catch (UnsupportedAudioFileException | IOException | LineUnavailableException e)
		{
			log.error("Failed to load audio clip: {}", audioFile, e);
			return null;
		}
	}

	private void playRandomMainAudio()
	{
		if (!config.soundEnabled() || mainAudioClips.isEmpty())
		{
			return;
		}
		
		Clip clip = mainAudioClips.get(random.nextInt(mainAudioClips.size()));
		playClip(clip);
	}
	
	private void playChildBlobAudio()
	{
		if (!config.soundEnabled() || childBlobClip == null)
		{
			return;
		}
		
		playClip(childBlobClip);
	}
	
	private void playClip(Clip clip)
	{
		try
		{
			// Set volume
			if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN))
			{
				FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
				float volume = config.volume() / 100.0f;
				float dB = (float) (Math.log(volume == 0 ? 0.0001 : volume) / Math.log(10.0) * 20.0);
				volumeControl.setValue(Math.max(dB, volumeControl.getMinimum()));
			}
			
			clip.setFramePosition(0);
			clip.start();
		}
		catch (Exception e)
		{
			log.error("Failed to play audio", e);
		}
	}

	@Provides
	InfernoBlobAudioConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(InfernoBlobAudioConfig.class);
	}
}
