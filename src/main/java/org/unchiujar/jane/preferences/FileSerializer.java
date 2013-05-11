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
package org.unchiujar.jane.preferences;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

/**
 * Writes and reads an object from a private local file.
 */
public class FileSerializer {
    private static final String TAG = FileSerializer.class.getName();

    /**
     * Writes an object to a file.
     * 
     * @param context
     * @param object
     *            the object to write
     * @param filename
     *            the filename to write to
     */
    public static void writeObjectToFile(Context context, Object object,
	    String filename) {

	ObjectOutputStream objectOut = null;
	try {

	    FileOutputStream fileOut = context.openFileOutput(filename,
		    Activity.MODE_PRIVATE);
	    Log.d(TAG, "Output stream is " + fileOut);
	    objectOut = new ObjectOutputStream(fileOut);
	    objectOut.writeObject(object);
	} catch (IOException e) {
	    Log.e(TAG, "Error writing object to file", e);
	} finally {
	    if (objectOut != null) {
		try {
		    objectOut.close();
		} catch (IOException ignored) {
		}
	    }
	}
    }

    /**
     * Reads an object from a file.
     * 
     * @param context
     * @param filename
     *            the name of the file to read from
     * @return the object read from the file
     */
    public static Object readObjectFromFile(Context context, String filename) {

	ObjectInputStream objectIn = null;
	Object object = null;
	try {

	    FileInputStream fileIn = context.getApplicationContext()
		    .openFileInput(filename);
	    objectIn = new ObjectInputStream(fileIn);
	    object = objectIn.readObject();

	} catch (FileNotFoundException e) {
	    Log.e(TAG, "File could not be found", e);
	} catch (IOException e) {
	    Log.e(TAG, "Error reading waypoints from file", e);
	} catch (ClassNotFoundException e) {
	    Log.e(TAG, "Error reading waypoints from file", e);
	} finally {
	    if (objectIn != null) {
		try {
		    objectIn.close();
		} catch (IOException e) {
		    // giving up
		}
	    }
	}
	return object;
    }

}