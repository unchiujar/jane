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

package org.unchiujar.jane.activities;

import java.util.Observable;
import java.util.Observer;

import org.unchiujar.jane.location.Waypoint;
import org.unchiujar.jane.location.WaypointManager;
import org.unchiujar.jane.location.WaypointNotFoundException;

import android.location.Location;
import android.util.Log;

public class WaypointController implements Observer {
	private static final String TAG = WaypointController.class.getName();

	private static final int REACHED_THRESHOLD_DISTANCE = 50;
	private final WaypointManager manager;
	private Waypoint currentWaypoint;

	public WaypointController(WaypointManager manager) {
		this.manager = manager;
		manager.addObserver(this);
	}

	public void update(Location location) {
		// if there is no current waypoint, do nothing
		if (currentWaypoint == null) {
			return;
		}

		// check if we are close enough to the current waypoint
		if (location.distanceTo(currentWaypoint) < REACHED_THRESHOLD_DISTANCE) {
			// marker the current waypoint as reached
			manager.markCurrentReached();
			// update the currentWaypoint
			updateCurrent();
		}
	}

	public void update(Observable arg0, Object arg1) {
		updateCurrent();
	}

	private void updateCurrent() {
		// update the currentWaypoint
		try {
			currentWaypoint = manager.getFirstUnreached();
		} catch (WaypointNotFoundException e) {
			Log.d(TAG, "No more unreached waypoints found", e);
			currentWaypoint = null;
		}

	}

	private void announce(Location location) {
		
	}
}
