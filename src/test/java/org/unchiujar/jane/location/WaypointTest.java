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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.xtremelabs.robolectric.RobolectricTestRunner;

import android.location.Location;

@RunWith(RobolectricTestRunner.class)
public class WaypointTest {

	private static final String TEST_PROVIDER = "test_provider";

	@Test
	public void creation() throws Exception {
		Location location = new Location(TEST_PROVIDER);
		location.setLatitude(32);
		location.setLongitude(42);
		location.setAltitude(32.4);
		Waypoint wayPoint = new Waypoint(location, false);

		assertEquals(location.getLatitude(), wayPoint.getLatitude(), 0);
		assertEquals(location.getLongitude(), wayPoint.getLongitude(), 0);
		assertEquals(location.getAltitude(), wayPoint.getAltitude(), 0);
		assertFalse(wayPoint.isReached());
	}

	@Test
	public void reaching() throws Exception {
		Location location = new Location(TEST_PROVIDER);
		Waypoint wayPoint = new Waypoint(location, false);
		// test for initial value
		assertFalse(wayPoint.isReached());
		wayPoint.setReached(true);
		assertTrue(wayPoint.isReached());
	}

	@Test
	public void equality() throws Exception {
		Location location = new Location(TEST_PROVIDER);
		location.setLatitude(32);
		location.setLongitude(42);
		location.setAltitude(32.4);
		Waypoint wayPoint1 = new Waypoint(location, false);
		Waypoint wayPoint2 = new Waypoint(location, false);

		assertEquals(wayPoint1, wayPoint2);

	}

	@Test
	public void latitudeInequality() throws Exception {
		Location location = new Location(TEST_PROVIDER);
		location.setLatitude(32);
		location.setLongitude(42);
		location.setAltitude(32.4);
		Waypoint wayPoint1 = new Waypoint(location, false);
		location.setLatitude(12);
		Waypoint wayPoint2 = new Waypoint(location, false);
		assertFalse(wayPoint1.equals(wayPoint2));
	}

	@Test
	public void longitudeInequality() throws Exception {
		Location location = new Location(TEST_PROVIDER);
		location.setLatitude(32);
		location.setLongitude(42);
		location.setAltitude(32.4);
		Waypoint wayPoint1 = new Waypoint(location, false);
		location.setLongitude(12);
		Waypoint wayPoint2 = new Waypoint(location, false);
		assertFalse(wayPoint1.equals(wayPoint2));
	}

	//TODO test close waypoints for equality

}
