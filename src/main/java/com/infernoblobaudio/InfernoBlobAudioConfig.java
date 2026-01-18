package com.infernoblobaudio;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup("infernoblobaudio")
public interface InfernoBlobAudioConfig extends Config
{
	@ConfigItem(
		keyName = "volume",
		name = "Volume",
		description = "Audio volume (0-100%)"
	)
	@Range(
		min = 0,
		max = 100
	)
	default int volume()
	{
		return 75;
	}

	@ConfigItem(
		keyName = "enabled",
		name = "Sound Enabled",
		description = "Enable or disable the sound effect"
	)
	default boolean soundEnabled()
	{
		return true;
	}
}
