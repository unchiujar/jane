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

	@Test
	public void altitudeInequality() throws Exception {
		Location location = new Location(TEST_PROVIDER);
		location.setLatitude(32);
		location.setLongitude(42);
		location.setAltitude(32.4);
		Waypoint wayPoint1 = new Waypoint(location, false);
		location.setAltitude(12);
		Waypoint wayPoint2 = new Waypoint(location, false);
		assertFalse(wayPoint1.equals(wayPoint2));
	}

}
