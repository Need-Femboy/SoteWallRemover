package com.sotewallremover;

import com.google.common.collect.ImmutableSet;
import lombok.AllArgsConstructor;
import java.util.Set;

@AllArgsConstructor
public enum Bosses {
	NEX(ImmutableSet.of(11600, 11601), 0, 3, false, ImmutableSet.of(26493, 26438, 26435, 37491, 26425, 26423, 42945), ImmutableSet.of(26423, 26425, 26426, 26424, 26435, 26438, 26439, 26437, 26436, 6926)),
	SOTE(ImmutableSet.of(13123), 0, 1, true, ImmutableSet.of(33044, 33046, 33047, 33048, 33049, 33050, 33051, 33052, 33059, 33058, 33057, 33056, 33055, 33054, 33053), null),
	GAUNTLET(ImmutableSet.of(7512), 1, 2, true, ImmutableSet.of(36095, 36097, 36098, 36099, 36100, 36103, 36104, 36105), null),
	CORRUPTED_GAUNTLET(ImmutableSet.of(7768), 1, 2, true, ImmutableSet.of(35992, 35994, 35995, 35996, 35997, 36002), null);

	private final Set<Integer> regions;
	public final int minZ;
	public final int maxZ;
	public final boolean instanceOnly;
	public final Set<Integer> gameObj;
	public final Set<Integer> wallObj;

	public static Bosses inRegion(int regionId) {
		for (Bosses b : Bosses.values())
		{
			if (b.regions.contains(regionId))
				return b;
		}
		return null;
	}
}
