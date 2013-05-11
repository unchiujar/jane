/*******************************************************************************
 * This file is part of Jane.
 * 
 *     Jane is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     Jane is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with Jane.  If not, see <http://www.gnu.org/licenses/>.
 * 
 *     Copyright (c) 2011 Vasile Jureschi <vasile.jureschi@gmail.com>.
 *     All rights reserved. This program and the accompanying materials
 *     are made available under the terms of the GNU Public License v3.0
 *     which accompanies this distribution, and is available at
 *     
 *    http://www.gnu.org/licenses/gpl-3.0.html
 * 
 *     Contributors:
 *        Vasile Jureschi <vasile.jureschi@gmail.com> - initial API and implementation
 ******************************************************************************/
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
		Waypoint waypoint = new Waypoint(new Location(TEST_PROVIDER), false);
		cache.addWaypoint(waypoint);
		// sanity checks
		assertEquals(waypoint, cache.getWaypoint(0));
		assertFalse(cache.getWaypoint(0).isReached());

		cache.markReached(0);
		assertTrue(cache.getWaypoint(0).isReached());
	}

	@Test(expected = WaypointNotFoundException.class)
	public void markReachedNotFound() throws Exception {
		WaypointManager cache = new WaypointManager();
		cache.markReached(1);
	}

}
