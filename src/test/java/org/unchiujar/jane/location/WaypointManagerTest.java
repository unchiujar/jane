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
		waypoints.add(new Waypoint(new Location(TEST_PROVIDER), false));
		waypoints.add(new Waypoint(new Location(TEST_PROVIDER), false));
		waypoints.add(new Waypoint(new Location(TEST_PROVIDER), false));
		waypoints.add(new Waypoint(new Location(TEST_PROVIDER), false));
		WaypointManager cache = new WaypointManager(waypoints);
		assertEquals(waypoints.size(), cache.getSize());
	}

	@Test
	public void setGetWaypoints() throws Exception {
		ArrayList<Waypoint> waypoints = new ArrayList<Waypoint>();
		waypoints.add(new Waypoint(new Location(TEST_PROVIDER), false));
		waypoints.add(new Waypoint(new Location(TEST_PROVIDER), false));
		waypoints.add(new Waypoint(new Location(TEST_PROVIDER), false));
		waypoints.add(new Waypoint(new Location(TEST_PROVIDER), false));
		WaypointManager cache = new WaypointManager();
		cache.setWaypoints(waypoints);

		assertArrayEquals(waypoints.toArray(), cache.getWaypoints().toArray());
	}

	@Test
	public void addGetWaypoints() throws Exception {
		ArrayList<Waypoint> waypoints = new ArrayList<Waypoint>();
		waypoints.add(new Waypoint(new Location(TEST_PROVIDER), false));
		waypoints.add(new Waypoint(new Location(TEST_PROVIDER), false));
		waypoints.add(new Waypoint(new Location(TEST_PROVIDER), false));
		waypoints.add(new Waypoint(new Location(TEST_PROVIDER), false));
		WaypointManager cache = new WaypointManager();
		cache.addWaypoints(waypoints);
		assertArrayEquals(waypoints.toArray(), cache.getWaypoints().toArray());
	}

	@Test
	public void addGetWaypoint() throws Exception {
		WaypointManager cache = new WaypointManager();
		Waypoint waypoint = new Waypoint(new Location(TEST_PROVIDER), false);
		cache.addWaypoint(waypoint);
		assertEquals(waypoint, cache.getWaypoint(0));
	}

	@Test(expected = WaypointNotFoundException.class)
	public void addGetDeleteWaypoint() throws Exception {
		WaypointManager cache = new WaypointManager();
		Waypoint waypoint = new Waypoint(new Location(TEST_PROVIDER), false);
		cache.addWaypoint(waypoint);
		cache.deleteWaypoint(0);
		cache.getWaypoint(0);
	}

	@Test
	public void markReached() throws Exception {
		WaypointManager cache = new WaypointManager();
		final int index = 3;
		Waypoint waypoint = new Waypoint(new Location(TEST_PROVIDER), false);
		cache.addWaypoint(waypoint);
		// sanity checks
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
