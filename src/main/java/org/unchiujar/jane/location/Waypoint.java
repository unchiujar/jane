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

public class Waypoint extends ApproximateLocation {

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
