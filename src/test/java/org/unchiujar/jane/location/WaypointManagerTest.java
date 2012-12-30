package org.unchiujar.jane.location;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;

import android.location.Location;

import com.xtremelabs.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class WaypointManagerTest {

	private static final String TEST_PROVIDER = "test_provider";

	@Test
	public void creation() throws Exception {
		ArrayList<Waypoint> waypoints = new ArrayList<Waypoint>();
		waypoints.add(new Waypoint(new Location(TEST_PROVIDER), 1, false));
		waypoints.add(new Waypoint(new Location(TEST_PROVIDER), 2, false));
		waypoints.add(new Waypoint(new Location(TEST_PROVIDER), 3, false));
		waypoints.add(new Waypoint(new Location(TEST_PROVIDER), 4, false));
		WaypointManager cache = new WaypointManager(waypoints);
		assertEquals(waypoints.size(), cache.getSize());
	}

	@Test
	public void setGetWaypoints() throws Exception {
		ArrayList<Waypoint> waypoints = new ArrayList<Waypoint>();
		waypoints.add(new Waypoint(new Location(TEST_PROVIDER), 1, false));
		waypoints.add(new Waypoint(new Location(TEST_PROVIDER), 2, false));
		waypoints.add(new Waypoint(new Location(TEST_PROVIDER), 3, false));
		waypoints.add(new Waypoint(new Location(TEST_PROVIDER), 4, false));
		WaypointManager cache = new WaypointManager();
		cache.setWaypoints(waypoints);

		assertArrayEquals(waypoints.toArray(), cache.getWaypoints().toArray());
	}

	@Test
	public void addGetWaypoints() throws Exception {
		ArrayList<Waypoint> waypoints = new ArrayList<Waypoint>();
		waypoints.add(new Waypoint(new Location(TEST_PROVIDER), 1, false));
		waypoints.add(new Waypoint(new Location(TEST_PROVIDER), 2, false));
		waypoints.add(new Waypoint(new Location(TEST_PROVIDER), 3, false));
		waypoints.add(new Waypoint(new Location(TEST_PROVIDER), 4, false));
		WaypointManager cache = new WaypointManager();
		cache.addWaypoints(waypoints);
		assertArrayEquals(waypoints.toArray(), cache.getWaypoints().toArray());
	}

	@Test
	public void addGetWaypoint() throws Exception {
		WaypointManager cache = new WaypointManager();
		Waypoint waypoint = new Waypoint(new Location(TEST_PROVIDER), 1, false);
		cache.addWaypoint(waypoint);
		assertEquals(waypoint, cache.getWaypoint(waypoint.getIndex()));
	}

	@Test(expected = WaypointNotFoundException.class)
	public void addGetDeleteWaypoint() throws Exception {
		WaypointManager cache = new WaypointManager();
		final int index = 3;
		Waypoint waypoint = new Waypoint(new Location(TEST_PROVIDER), index,
				false);
		cache.addWaypoint(waypoint);
		assertEquals(waypoint, cache.getWaypoint(index));
		cache.deleteWaypoint(index);
		cache.getWaypoint(index);
	}

	@Test
	public void markReached() throws Exception {
		WaypointManager cache = new WaypointManager();
		final int index = 3;
		Waypoint waypoint = new Waypoint(new Location(TEST_PROVIDER), index,
				false);
		cache.addWaypoint(waypoint);
		//sanity checks
		assertEquals(waypoint, cache.getWaypoint(index));
		assertFalse(cache.getWaypoint(index).isReached());

		cache.markReached(index);
		assertTrue(cache.getWaypoint(index).isReached());
	}

	@Test(expected = WaypointNotFoundException.class)
	public void markReachedNotFound() throws Exception {
		WaypointManager cache = new WaypointManager();
		cache.markReached(1);
	}

}
