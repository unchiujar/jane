package org.unchiujar.jane.location;

import android.location.Location;

public class Waypoint extends ApproximateLocation implements Comparable<Waypoint> {
	private int index;
	private boolean reached;
	private String info;

	public Waypoint(Location location, int index, boolean reached) {
		this(location, index, reached, "");
	}

	public Waypoint(Location location, int index, boolean reached, String info) {
		super(location);
		this.index = index;
		this.reached = reached;
		this.info = info;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public boolean isReached() {
		return reached;
	}

	public void setReached(boolean reached) {
		this.reached = reached;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + index;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Waypoint other = (Waypoint) obj;
		// waypoints are the same if they have the same index
		if (index != other.index)
			return false;
		return true;
	}

	@Override
	public int compareTo(Waypoint another) {
		return this.index - (((Waypoint) another).getIndex());
	}

	@Override
	public String toString() {
		return super.toString() + " Waypoint [index=" + index + ", reached=" + reached + ", info="
				+ info + "]";
	}

	
}
