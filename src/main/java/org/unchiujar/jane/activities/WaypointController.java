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
