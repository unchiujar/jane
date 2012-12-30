package org.unchiujar.jane.location;

import java.util.ArrayList;
import static org.unchiujar.jane.location.WaypointManager.MarkerMessage.State.*;
import java.util.List;
import java.util.Observable;
import java.util.TreeMap;

public class WaypointManager extends Observable {
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
		addWaypoints(waypoints);
	}

	public void addWaypoints(List<Waypoint> waypoints) {
		for (Waypoint waypoint : waypoints) {
			this.waypoints.put(waypoint.getIndex(), waypoint);
		}
		notifyObservers(new MarkerMessage(null, ADD_MULTIPLE));
	}

	public Waypoint getWaypoint(int index) throws WaypointNotFoundException {
		return getWaypointSafely(index);
	}

	public void addWaypoint(Waypoint waypoint) {
		waypoints.put(waypoint.getIndex(), waypoint);
		notifyObservers(new MarkerMessage(waypoint, ADD));
	}

	public void deleteWaypoint(int index) throws WaypointNotFoundException {
		notifyObservers(new MarkerMessage(getWaypoint(index), DELETE));
		waypoints.remove(index);
	}

	public void markReached(int index) throws WaypointNotFoundException {
		getWaypointSafely(index).setReached(true);
		notifyObservers(new MarkerMessage(getWaypoint(index), REACH));
	}

	public void deleteAllWaypoints() {
		waypoints.clear();
		//send a message with no waypoint data stating all markers have been deleted
		notifyObservers(new MarkerMessage(null, DELETE_ALL));
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
		private State state;

		public MarkerMessage(Waypoint waypoint, State state) {
			this.waypoint = waypoint;
			this.state = state;
		}

		public Waypoint getWaypoint() {
			return waypoint;
		}
		
		public State getState() {
			return state;
		}

		@Override
		public String toString() {
			return "MarkerMessage [waypoint=" + waypoint.toString() + ", state=" + state
					+ "]";
		}
	
		
	}

}
