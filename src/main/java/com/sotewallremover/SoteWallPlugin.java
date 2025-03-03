package com.sotewallremover;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
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
	private ClientThread clientThread;
	
	@Override
	protected void startUp() throws Exception
	{
		//In-case someone starts the plugin whilst already in the room I guess?
		if (client.getGameState() == GameState.LOGGED_IN)
		{
			clientThread.invoke(this::regionAndWallCheck);
		}
	}
	
	@Override
	protected void shutDown() throws Exception
	{
	
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
	
	private void regionAndWallCheck()
	{
		int regionId = WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation()).getRegionID();
		Bosses boss = Bosses.inRegion(regionId);
		
		if (boss != null)
		{
			if (boss.instanceOnly && !client.getTopLevelWorldView().isInstance())
			{
				return;
			}
			removeWall(boss);
		}
	}
	
	private void removeWall(Bosses boss)
	{
		Scene scene = client.getTopLevelWorldView().getScene();
		Tile[][][] tiles = scene.getTiles();
		for (int z = 0; z < boss.maxZ; ++z)
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
}
