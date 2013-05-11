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
package org.unchiujar.jane.preferences;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unchiujar.jane.location.Waypoint;

import android.content.Context;
import android.location.Location;

import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class FileSerializerTest {

    private static final String FILE_NAME = "tests.data";
    private static final String TEST_PROVIDER = "test_provider";

    private final Context CONTEXT = Robolectric.getShadowApplication()
	    .getApplicationContext();

    // @After
    public void teardown() throws Exception {
	File file = new File(FILE_NAME);
	file.delete();
    }

    @Test
    @Ignore
    public void writeWaypointList() throws Exception {

	ArrayList<Waypoint> waypoints = new ArrayList<Waypoint>();
	waypoints.add(new Waypoint(new Location(TEST_PROVIDER), false));
	waypoints.add(new Waypoint(new Location(TEST_PROVIDER), false));
	waypoints.add(new Waypoint(new Location(TEST_PROVIDER), false));
	waypoints.add(new Waypoint(new Location(TEST_PROVIDER), false));

	FileSerializer.writeObjectToFile(CONTEXT, waypoints, FILE_NAME);

	@SuppressWarnings("unchecked")
	ArrayList<Waypoint> readWaypoints = (ArrayList<Waypoint>) FileSerializer
		.readObjectFromFile(CONTEXT, FILE_NAME);

	assertEquals(waypoints, readWaypoints);
    }
}
