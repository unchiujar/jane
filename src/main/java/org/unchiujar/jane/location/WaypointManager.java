package org.unchiujar.jane.location;

import static org.unchiujar.jane.location.WaypointManager.MarkerMessage.State.ADD;
import static org.unchiujar.jane.location.WaypointManager.MarkerMessage.State.ADD_MULTIPLE;
import static org.unchiujar.jane.location.WaypointManager.MarkerMessage.State.DELETE;
import static org.unchiujar.jane.location.WaypointManager.MarkerMessage.State.DELETE_ALL;
import static org.unchiujar.jane.location.WaypointManager.MarkerMessage.State.REACH;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Set;
import java.util.TreeMap;

import android.util.Log;

public class WaypointManager extends Observable {
	private static final String TAG = WaypointManager.class.getName();
	private TreeMap<Integer, Waypoint> waypoints;

	public WaypointManager(List<Waypoint> waypoints) {
		this();
		addWaypoints(waypoints);
	}

	public WaypointManager() {
		this.waypoints = new TreeMap<Integer, Waypoint>();
	}

	public ArrayList<Waypoint> getWaypoints() {
		return new ArrayList<Waypoint>(waypoints.values());
	}

	public void setWaypoints(List<Waypoint> waypoints) {
		this.waypoints.clear();
		setChanged();
		addWaypoints(waypoints);
	}

	public void addWaypoints(List<Waypoint> waypoints) {
		int i = 0;
		for (Waypoint waypoint : waypoints) {
			this.waypoints.put(i++, waypoint);
		}
		setChanged();
		notifyObservers(new MarkerMessage(null, -1, ADD_MULTIPLE));
	}

	public Waypoint getWaypoint(int index) throws WaypointNotFoundException {
		return getWaypointSafely(index);
	}

	public void addWaypoint(Waypoint waypoint) {
		Log.d(TAG, "Adding waypoint: " + waypoint.toString());
		// check if there is at least an element, if not add one
		// if there is at least an element get the highest key and increment
		if (waypoints.size() > 0) {
			waypoints.put(waypoints.lastKey() + 1, waypoint);
		} else {
			waypoints.put(0, waypoint);

		}
		setChanged();
		notifyObservers(new MarkerMessage(waypoint, waypoints.lastKey(), ADD));
	}

	public void deleteWaypoint(int index) throws WaypointNotFoundException {
		setChanged();
		notifyObservers(new MarkerMessage(getWaypoint(index), index, DELETE));
		waypoints.remove(index);
	}

	public void markReached(int index) throws WaypointNotFoundException {
		getWaypointSafely(index).setReached(true);
		setChanged();
		notifyObservers(new MarkerMessage(getWaypoint(index), index, REACH));
	}

	/**
	 * Marks the first unreached waypoint as reached.
	 */
	public void markCurrentReached() {
		Set<Integer> keys = waypoints.keySet();
		Iterator<Integer> iterator = keys.iterator();
		for (Waypoint point : waypoints.values()) {
			if (!point.isReached()) {
				point.setReached(true);
				setChanged();
				notifyObservers(new MarkerMessage(point, iterator.next(), REACH));
				break;
			}
			iterator.next();
		}

	}

	public Waypoint getFirstUnreached() throws WaypointNotFoundException {
		for (Waypoint point : waypoints.values()) {
			if (!point.isReached()) {
				return point;
			}
		}
		throw new WaypointNotFoundException(
				"No unreached waypoint has been found.");
	}

	public void deleteAllWaypoints() {
		waypoints.clear();
		setChanged();
		// send a message with no waypoint data stating all markers have been
		// deleted
		notifyObservers(new MarkerMessage(null, -1, DELETE_ALL));
	}

	public int getSize() {
		return waypoints.size();
	}

	private Waypoint getWaypointSafely(int index)
			throws WaypointNotFoundException {
		Waypoint waypoint = waypoints.get(index);
		if (waypoint != null) {
			return waypoint;
		}
		throw new WaypointNotFoundException(
				"Waypoint with the specified index could not be found:" + index);
	}

	public static class MarkerMessage {
		public enum State {
			DELETE, ADD, REACH, DELETE_ALL, ADD_MULTIPLE
		}

		private Waypoint waypoint;
		private int index;
		private State state;

		public MarkerMessage(Waypoint waypoint, int index, State state) {
			this.waypoint = waypoint;
			this.state = state;
			this.index = index;
		}

		public Waypoint getWaypoint() {
			return waypoint;
		}

		public State getState() {
			return state;
		}

		public int getIndex() {
			return index;
		}

		@Override
		public String toString() {
			return "MarkerMessage [waypoint=" + waypoint + ", index=" + index
					+ ", state=" + state + "]";
		}

	}

}
