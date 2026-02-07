package com.sotewallremover;

import com.google.inject.Provides;
import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.WallObjectSpawned;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.callback.RenderCallbackManager;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
		name = "Boss Wall Remover",
		description = "Removes annoying walls at Sotetseg and Nex",
		tags = {"tob", "theatre of blood", "nex", "bossing"}
)
public class SoteWallPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private SoteWallConfig config;

	@Inject
	private ClientThread clientThread;

    @Inject
    private NewObjectHider callback;

    @Inject
    private RenderCallbackManager renderCallbackManager;

	@Override
	protected void startUp() throws Exception
	{
        renderCallbackManager.register(callback);
		clientThread.invoke(() ->
		{
			if (client.getGameState() == GameState.LOGGED_IN)
			{
				client.setGameState(GameState.LOADING);
			}
		});
	}

	@Override
	protected void shutDown() throws Exception
	{
        renderCallbackManager.unregister(callback);
		clientThread.invoke(() ->
		{
			if (client.getGameState() == GameState.LOGGED_IN)
			{
				client.setGameState(GameState.LOADING);
			}
		});
		callback.gameObjectReference.clear();
		callback.wallObjectReference.clear();
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		int regionId = WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation()).getRegionID();
		Bosses boss = Bosses.inRegion(regionId, config);

		if (boss == null)
		{
			callback.boss = boss;
			callback.gameObjectReference.clear();
			callback.wallObjectReference.clear();
			return;
		}

		if (callback.boss == boss) //Boss hasn't changed so ignore
		{
			return;
		}

		callback.boss = boss;
		callback.gameObjectReference.clear();
		callback.wallObjectReference.clear();
		if (boss.gameObj != null)
			callback.gameObjectReference.addAll(boss.gameObj);
		if (boss.wallObj != null)
			callback.wallObjectReference.addAll(boss.wallObj);

		client.setGameState(GameState.LOADING); //This is necessary to properly hide scenery, the callback doesn't seem to run until I do this for some reason
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (event.getGroup().equals(SoteWallConfig.configName))
		{
			int regionId = WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation()).getRegionID();
			Bosses boss = Bosses.inRegion(regionId);

			if (boss == null)
			{
				return;
			}

			//Just forces a map load to hide stuff
			clientThread.invoke(() ->
			{
				if (client.getGameState() == GameState.LOGGED_IN)
				{
					client.setGameState(GameState.LOADING);
				}
			});
		}
	}

	@Provides
	SoteWallConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(SoteWallConfig.class);
	}
}
