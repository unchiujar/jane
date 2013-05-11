package org.unchiujar.jane.preferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.unchiujar.jane.location.Waypoint;

import android.content.Context;
import android.util.Log;

public class WaypointSaver {
    private static final String TAG = WaypointSaver.class.getName();

    public static final String WAYPOINT_INFO = "waypoints.data";
    
    public static void save(Context context, List<Waypoint> waypoints) {
	// convert the list of waypoints to serializable list
	ArrayList<WaypointTO> wayTOs = new ArrayList<WaypointTO>();
	for (Waypoint waypoint : waypoints) {
	    wayTOs.add(new WaypointTO(waypoint));
	}
	Log.d(TAG, "Waypoints to be saved :" + wayTOs.size());
	FileSerializer.writeObjectToFile(context, wayTOs, WAYPOINT_INFO);

    }

    public static List<Waypoint> load(Context context) {
	// TODO convert the list serialized waypoints to a list of waypoints
	@SuppressWarnings("unchecked")
	ArrayList<WaypointTO> wayTOs = (ArrayList<WaypointTO>) FileSerializer
		.readObjectFromFile(context, WAYPOINT_INFO);
	//if nothing was loaded just return an empty list 
	if (wayTOs == null) {
	    return Collections.emptyList();
	}
	//otherwise create a list of waypoints
	ArrayList<Waypoint> waypoints = new ArrayList<Waypoint>();
	for (WaypointTO waypointTO : wayTOs) {
	    waypoints.add(waypointTO.getWaypoint());
	}
	Log.d(TAG, "Loaded waypoints, size:" + waypoints.size());
	
	return waypoints;
    }

}
