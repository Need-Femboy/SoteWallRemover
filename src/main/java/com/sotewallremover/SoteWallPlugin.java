package com.sotewallremover;

import com.google.inject.Provides;
import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.callback.ClientThread;
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

	@Override
	protected void startUp() throws Exception
	{
		clientThread.invoke(() ->
		{
			if (client.getGameState() == GameState.LOGGED_IN)
			{
				regionAndWallCheck();
			}
		});
	}

	@Override
	protected void shutDown() throws Exception
	{
		clientThread.invoke(() ->
		{
			if (client.getGameState() == GameState.LOGGED_IN)
			{
				client.setGameState(GameState.LOADING);
			}
		});
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}
		regionAndWallCheck();
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event)
	{
		regionAndSpawnedWallCheck(event.getGameObject());
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
			
			if (event.getNewValue().equals("false"))
			{
				clientThread.invoke(() ->
				{
					if (client.getGameState() == GameState.LOGGED_IN)
					{
						client.setGameState(GameState.LOADING);
					}
				});
			}
			else if (event.getNewValue().equals("true"))
			{
				regionAndWallCheck();
			}
		}
	}

	private void regionAndSpawnedWallCheck(GameObject gameObject)
	{
		int regionId = WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation()).getRegionID();
		Bosses boss = Bosses.inRegion(regionId, config);
		if (boss == null)
		{
			return;
		}
		if (gameObject == null)
		{
			return;
		}
		removeSpawnedWall(boss, gameObject);
	}

	private void regionAndWallCheck()
	{
		int regionId = WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation()).getRegionID();
		Bosses boss = Bosses.inRegion(regionId, config);

		if (boss != null)
		{
			if (boss.instanceOnly && !client.getTopLevelWorldView().isInstance())
			{
				return;
			}
			removeWall(boss);
		}
	}

	private void removeSpawnedWall(Bosses boss, GameObject gameObject)
	{
		Scene scene = client.getTopLevelWorldView().getScene();
		if (gameObject != null && boss.gameObj.contains(gameObject.getId()))
		{
			scene.removeGameObject(gameObject);
		}
	}

	private void removeWall(Bosses boss)
	{
		Scene scene = client.getTopLevelWorldView().getScene();
		Tile[][][] tiles = scene.getTiles();
		for (int z = boss.minZ; z < boss.maxZ; ++z)
		{
			for (int x = 0; x < Constants.SCENE_SIZE; ++x)
			{
				for (int y = 0; y < Constants.SCENE_SIZE; ++y)
				{
					Tile tile = tiles[z][x][y];
					
					if (tile == null)
					{
						continue;
					}
					
					if (boss.gameObj != null)
					{
						GameObject[] gameObjects = tile.getGameObjects();
						if (gameObjects != null)
						{
							for (GameObject gameObject : gameObjects)
							{
								if (gameObject != null && boss.gameObj.contains(gameObject.getId()))
								{
									scene.removeGameObject(gameObject);
									break;
								}
							}
						}
					}
					
					
					if (boss.wallObj != null)
					{
						WallObject wo = tile.getWallObject();
						if (wo != null && boss.wallObj.contains(wo.getId()))
						{
							scene.removeTile(tile);
						}
					}
				}
			}
		}
	}
	
	@Provides
	SoteWallConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(SoteWallConfig.class);
	}
}
