package com.infernoblobaudio;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.NPC;
import net.runelite.api.events.ActorDeath;
import net.runelite.client.audio.AudioPlayer;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;
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

	@Inject
	private AudioPlayer audioPlayer;

	private Random random = new Random();

	@Override
	protected void startUp() throws Exception
	{
		log.info("Inferno Blob Audio plugin started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Inferno Blob Audio plugin stopped!");
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

	private void playRandomMainAudio()
	{
		if (!config.soundEnabled())
		{
			return;
		}
		
		String randomAudio = MAIN_AUDIO_FILES[random.nextInt(MAIN_AUDIO_FILES.length)];
		// AudioPlayer.play(Class source, String path, float gain)
		audioPlayer.play(getClass(), randomAudio, calculateGain(config.volume()));
	}
	
	private void playChildBlobAudio()
	{
		if (!config.soundEnabled())
		{
			return;
		}
		
		audioPlayer.play(getClass(), CHILD_BLOB_AUDIO, calculateGain(config.volume()));
	}

	private float calculateGain(int volume)
	{
		if (volume <= 0)
		{
			return -80.0f;
		}
		return (float) (20.0 * Math.log10(volume / 100.0));
	}

	@Provides
	InfernoBlobAudioConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(InfernoBlobAudioConfig.class);
	}
}
