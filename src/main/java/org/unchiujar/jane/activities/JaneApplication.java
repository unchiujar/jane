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

package org.unchiujar.jane.activities;

import org.unchiujar.jane.location.WaypointManager;

import android.app.Application;

public class JaneApplication extends Application {

    @Override
    public void onCreate() {
    }

	public WaypointManager getWaypointManager() {
		// TODO Auto-generated method stub
		return null;
	};

}
