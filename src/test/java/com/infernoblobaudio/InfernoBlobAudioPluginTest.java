package com.infernoblobaudio;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class InfernoBlobAudioPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(InfernoBlobAudioPlugin.class);
		RuneLite.main(args);
	}
}
