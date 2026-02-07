package com.sotewallremover;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.GameObject;
import net.runelite.api.Scene;
import net.runelite.api.Tile;
import net.runelite.api.TileObject;
import net.runelite.client.callback.RenderCallback;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

public class NewObjectHider implements RenderCallback {

    public Set<Integer> gameObjectReference = new HashSet<>();
    public Set<Integer> wallObjectReference = new HashSet<>();
    public Bosses boss;

    @Inject
    NewObjectHider(){}

    @Override
    public boolean drawTile(Scene scene, Tile tile)
    {
        if (boss != null && tile.getWallObject() != null)
        {
            boolean isMarkedForDeletion = wallObjectReference.contains(tile.getWallObject().getId());
            if (isMarkedForDeletion)
                scene.removeTile(tile);
            return !isMarkedForDeletion;
        }
        return true;
    }

    @Override
    public boolean drawObject(Scene scene, TileObject object)
    {
        if (boss != null && object instanceof GameObject)
        {
            return !gameObjectReference.contains(object.getId());
        }
        return true;
    }
}
