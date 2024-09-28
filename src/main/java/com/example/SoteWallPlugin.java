package com.example;

import javax.inject.Inject;

import com.google.common.collect.ImmutableSet;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import java.util.Set;

@Slf4j
@PluginDescriptor(
		name = "Sote Wall Remover",
		description = "Removes the annoying wall behind Sotetseg in ToB",
		tags = {"tob", "theatre of blood"}
)
public class SoteWallPlugin extends Plugin
{
	@Inject
	private Client client;
	
	
	Set<Integer> WALL_IDS = ImmutableSet.of(33044, 33046, 33047, 33048, 33049, 33050, 33051, 33052, 33059, 33058, 33057, 33056, 33055, 33054, 33053);
	
	@Override
	protected void startUp() throws Exception
	{
		//In-case someone starts the plugin whilst already in the room I guess?
		if (correctRegion())
		{
			removeWall();
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
		
		if (!correctRegion())
		{
			return;
		}
		
		removeWall();
	}
	
	private void removeWall()
	{
		Scene scene = client.getScene();
		Tile[][][] tiles = scene.getTiles();
		
		for (int x = 0; x < Constants.SCENE_SIZE; ++x)
			for (int y = 0; y < Constants.SCENE_SIZE; ++y) {
				Tile tile = tiles[0][x][y];
				
				if (tile == null) {
					continue;
				}
				
				for (GameObject gameObject: tile.getGameObjects()) {
					if (gameObject == null) {
						continue;
					}
					
					if (WALL_IDS.contains(gameObject.getId())) {
						scene.removeGameObject(gameObject);
						break;
					}
				}
			}
	}
	
	private boolean correctRegion()
	{
		if (!client.isInInstancedRegion())
		{
			return false;
		}
		
		int regionId = WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation()).getRegionID();
		
		if (regionId != 13123) {
			return false;
		}
		return true;
	}
}
