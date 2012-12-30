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

package org.unchiujar.jane.location;

import android.location.Location;

public class ApproximateLocation extends Location {

	public ApproximateLocation(String provider) {
		super(provider);
	}

	public ApproximateLocation(Location location) {
		super(location);
		//XXX  this should work through the super() call ?!?!!
		super.setLatitude(location.getLatitude());
		super.setLongitude(location.getLongitude());
		super.setAltitude(location.getAltitude());
	}

	
	
	@Override
	public boolean equals(Object obj) {
		// sanity checks
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		// check if it is outside the preset radius
		return this.distanceTo((ApproximateLocation) obj) <= LocationOrder.METERS_RADIUS;

	}

	@Override
	public int hashCode() {
		int randomPrime = 47;
		int result = 42;
		long hashLong = Double.doubleToLongBits(this.getLongitude());
		long hashLat = Double.doubleToLongBits(this.getLatitude());
		result = (int) (randomPrime * result + hashLong);
		result = (int) (randomPrime * result + hashLat);
		return result;
	}
	
}
