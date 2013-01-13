package org.unchiujar.jane.location;

import android.location.Location;

public class Waypoint extends ApproximateLocation  {
	private boolean reached;
	private String info;

	
	public Waypoint(Location location, boolean reached) {
		this(location, reached, "");
	}

	public Waypoint(Location location, boolean reached, String info) {
		super(location);
		this.reached = reached;
		this.info = info;
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
		result = prime * result + ((info == null) ? 0 : info.hashCode());
		result = prime * result + (reached ? 1231 : 1237);
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
		if (info == null) {
			if (other.info != null)
				return false;
		} else if (!info.equals(other.info))
			return false;
		if (reached != other.reached)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Waypoint [reached=" + reached + ", info=" + info
				+ ", toString()=" + super.toString() + "]";
	}
	
}
