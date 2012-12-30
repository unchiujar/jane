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

package org.unchiujar.jane.utils;

import org.unchiujar.jane.location.ApproximateLocation;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Utility class used for transforming between various representations for
 * locations.
 * 
 * @author vasile
 */
public class LocationUtilities {

	private static final String TAG = LocationUtilities.class.getName();

	private static final double METER_IN_FEET = 3.2808399;
	private static final String FEET_UNIT = " ft";
	private static final String METERS_UNIT = " m";

	public static LatLng locationToLatLng(Location location) {
		return new LatLng(location.getLatitude(),
				location.getLongitude());
	}

	public static ApproximateLocation LatLngToLocation(LatLng latLng) {
		return coordinatesToLocation(latLng.latitude, latLng.longitude);
	}

	/**
	 * @param latitude
	 *            in decimal degrees
	 * @param longitude
	 *            in decimal degrees
	 * @return a ApproximateLocation with the coordinates
	 */
	public static ApproximateLocation coordinatesToLocation(double latitude,
			double longitude) {
		ApproximateLocation location = new ApproximateLocation("Translator");
		location.setLatitude(latitude);
		location.setLongitude(longitude);
		return location;
	}

	/**
	 * @param latitude
	 *            degrees
	 * @param longitude
	 *            degrees
	 * @return a ApproximateLocation with the coordinates
	 */
	public static ApproximateLocation coordinatesToLocation(int latitude,
			int longitude) {
		ApproximateLocation location = new ApproximateLocation("Translator");
		location.setLatitude(latitude);
		location.setLongitude(longitude);
		return location;
	}

	/**
	 * Gets the distance in the unit for the current settings. The measure value
	 * is false for metric and true for imperial.
	 * 
	 * @param meters
	 *            the distance in meters
	 * @param measure
	 *            metric or imperial system.
	 * @return
	 */
	public static String getFormattedDistance(double meters, boolean imperial) {
		if (imperial) {
			return Math.round(meters * METER_IN_FEET) + FEET_UNIT;
		}
		return Math.round(meters) + METERS_UNIT;
	}

	private static final double EARTH_RADIUS = 6317d;

	public static double haversineDistance(Location first, Location second) {
		double lat1 = first.getLatitude();
		double lat2 = second.getLatitude();
		double lon1 = first.getLongitude();
		double lon2 = second.getLongitude();
		double dLat = Math.toRadians(lat2 - lat1);
		double dLon = Math.toRadians(lon2 - lon1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
				* Math.sin(dLon / 2);
		double c = 2 * Math.asin(Math.sqrt(a));
		return EARTH_RADIUS * c * 1000;
	}
}
