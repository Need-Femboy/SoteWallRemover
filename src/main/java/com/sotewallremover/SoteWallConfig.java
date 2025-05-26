package com.sotewallremover;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import static com.sotewallremover.SoteWallConfig.configName;

@ConfigGroup(configName)
public interface SoteWallConfig extends Config
{
	String configName = "BossWallConfig";
	@ConfigItem(
			keyName = "hideSote",
			name = "Hide Walls At Soteseg",
			description = "",
			position = 0
	)
	default boolean HideSote()
	{
		return true;
	}
	@ConfigItem(
			keyName = "hideNex",
			name = "Hide Walls At Nex",
			description = "",
			position = 1
	)
	default boolean HideNex()
	{
		return false;
	}
	@ConfigItem(
			keyName = "hideGauntlet",
			name = "Hide Walls At Gauntlet",
			description = "",
			position = 2
	)
	default boolean HideGauntlet()
	{
		return false;
	}
	
}
