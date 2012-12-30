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
		Waypoint wayPoint = new Waypoint(location, 1, false);

		assertEquals(location.getLatitude(), wayPoint.getLatitude(), 0);
		assertEquals(location.getLongitude(), wayPoint.getLongitude(), 0);
		assertEquals(location.getAltitude(), wayPoint.getAltitude(), 0);
		assertEquals(1, wayPoint.getIndex());
		assertFalse(wayPoint.isReached());
	}

	@Test
	public void reaching() throws Exception {
		Location location = new Location(TEST_PROVIDER);
		Waypoint wayPoint = new Waypoint(location, 1, false);
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
		Waypoint wayPoint1 = new Waypoint(location, 1, false);
		Waypoint wayPoint2 = new Waypoint(location, 1, false);

		assertEquals(wayPoint1, wayPoint2);

	}

	@Test
	public void indexInequality() throws Exception {
		Location location = new Location(TEST_PROVIDER);
		location.setLatitude(32);
		location.setLongitude(42);
		location.setAltitude(32.4);
		Waypoint wayPoint1 = new Waypoint(location, 1, false);
		Waypoint wayPoint2 = new Waypoint(location, 2, false);
		assertFalse(wayPoint1.equals(wayPoint2));
	}

	@Test
	public void latitudeInequality() throws Exception {
		Location location = new Location(TEST_PROVIDER);
		location.setLatitude(32);
		location.setLongitude(42);
		location.setAltitude(32.4);
		Waypoint wayPoint1 = new Waypoint(location, 1, false);
		location.setLatitude(12);
		Waypoint wayPoint2 = new Waypoint(location, 2, false);
		assertFalse(wayPoint1.equals(wayPoint2));
	}

	@Test
	public void longitudeInequality() throws Exception {
		Location location = new Location(TEST_PROVIDER);
		location.setLatitude(32);
		location.setLongitude(42);
		location.setAltitude(32.4);
		Waypoint wayPoint1 = new Waypoint(location, 1, false);
		location.setLongitude(12);
		Waypoint wayPoint2 = new Waypoint(location, 2, false);
		assertFalse(wayPoint1.equals(wayPoint2));
	}

	@Test
	public void altitudeInequality() throws Exception {
		Location location = new Location(TEST_PROVIDER);
		location.setLatitude(32);
		location.setLongitude(42);
		location.setAltitude(32.4);
		Waypoint wayPoint1 = new Waypoint(location, 1, false);
		location.setAltitude(12);
		Waypoint wayPoint2 = new Waypoint(location, 2, false);
		assertFalse(wayPoint1.equals(wayPoint2));
	}

	@Test
	public void compareEquals() throws Exception {
		Waypoint wayPoint1 = new Waypoint(new Location(TEST_PROVIDER), 2, false);
		Waypoint wayPoint2 = new Waypoint(new Location(TEST_PROVIDER), 2, false);
		assertEquals(0, wayPoint1.compareTo(wayPoint2));
	}

	@Test
	public void compareLess() throws Exception {
		Waypoint wayPoint1 = new Waypoint(new Location(TEST_PROVIDER), 1, false);
		Waypoint wayPoint2 = new Waypoint(new Location(TEST_PROVIDER), 2, false);
		assertEquals(-1, wayPoint1.compareTo(wayPoint2));
	}

	@Test
	public void compareMore() throws Exception {
		Waypoint wayPoint1 = new Waypoint(new Location(TEST_PROVIDER), 5, false);
		Waypoint wayPoint2 = new Waypoint(new Location(TEST_PROVIDER), 2, false);
		assertEquals(3, wayPoint1.compareTo(wayPoint2));
	}

}
