package org.unchiujar.jane.location;

public class WaypointNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	public WaypointNotFoundException() {
		super();
	}

	public WaypointNotFoundException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public WaypointNotFoundException(String detailMessage) {
		super(detailMessage);
	}

	public WaypointNotFoundException(Throwable throwable) {
		super(throwable);
	}
	

}
