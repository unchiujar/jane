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

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.TreeMap;

import org.unchiujar.jane.R;
import org.unchiujar.jane.location.Waypoint;
import org.unchiujar.jane.location.WaypointManager;
import org.unchiujar.jane.location.WaypointManager.MarkerMessage;
import org.unchiujar.jane.location.WaypointManager.MarkerMessage.State;
import org.unchiujar.jane.location.WaypointNotFoundException;
import org.unchiujar.jane.services.LocationService;
import org.unchiujar.jane.utils.LocationUtilities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.WindowManager.LayoutParams;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * Main activity for Jane application.
 * 
 * @author Vasile Jureschi
 * @see LocationService
 */
public class WaypointMap extends SherlockFragmentActivity implements Observer,
        TextToSpeech.OnInitListener {
    private static final int ANNOUNCEMENT_INTERVAL = 20000;
    /** Logger tag. */
    private static final String TAG = WaypointMap.class.getName();
    /** Initial map zoom. */
    private static final int INITIAL_ZOOM = 17;
    /** Interval between zoom checks for the zoom and pan handler. */
    public static final int ZOOM_CHECKING_DELAY = 500;
    /** Constant used for saving the accuracy value between screen rotations. */
    private static final String ACCURACY_SAVE = "org.unchiujar.jane.accuracy";
    /** Constant used for saving the latitude value between screen rotations. */
    private static final String LATITUDE_SAVE = "org.unchiujar.jane.latitude";
    /** Constant used for saving the longitude value between screen rotations. */
    private static final String LONGITUDE_SAVE = "org.unchiujar.jane.longitude";
    /** Constant used for saving the zoom level between screen rotations. */
    private static final String ZOOM_SAVE = "org.unchiujar.jane.zoom";
    private static final String WAYPOINT_EDITING_SAVE = "org.unchiujar.jane.waypoint_edit";

    /**
     * Intent named used for starting the location service
     * 
     * @see LocationService
     */
    private static final String SERVICE_INTENT_NAME = "org.com.unchiujar.LocationService";

    /**
     * Location service intent.
     * 
     * @see LocationService
     */
    private Intent mLocationServiceIntent;

    /** Current device latitude. Updated on every location change. */
    private double mCurrentLat;
    /** Current device longitude. Updated on every location change. */
    private double mCurrentLong;
    /** Current location accuracy . Updated on every location change. */
    private double mCurrentAccuracy;

    private Location mCurrentLocation;

    /**
     * Flag signaling if the application is visible. Used to stop overlay updates if the map is
     * currently not visible.
     */
    private boolean mVisible = true;
    /**
     * Flag signaling if the user is walking or driving. It is passed to the location service in
     * order to change location update frequency.
     * 
     * @see LocationService
     */
    private boolean mDrive;

    /** Messenger for communicating with service. */
    private Messenger mService = null;
    /** Flag indicating whether we have called bind on the service. */
    private boolean mIsBound;

    /** Target we publish for clients to send messages to IncomingHandler. */
    private final Messenger mMessenger = new Messenger(new IncomingHandler());

    private SharedPreferences mSettings;

    private final Handler handler = new Handler();

    private final Runnable announcer = new Runnable() {

        @Override
        public void run() {
            try {
                Waypoint nextPoint = waypointManager.getFirstUnreached();
                float speed = mCurrentLocation.getSpeed();
                // calculate distance
                float distance = nextPoint.distanceTo(mCurrentLocation);
                // calculate relative bearing [0, 360]
                Log.d(TAG, "Device bearing: " + mCurrentLocation.getBearing());
                // bearingTo(..) gets values in the range [-180, 180] while
                // getBearing(...) gets (0.0,360]
                // cause fuck you, that's why principle of least surprise
                Log.d(TAG,
                        "Current location to waypoint bearing : "
                                + mCurrentLocation.bearingTo(nextPoint));
                float bearing = 180 - (mCurrentLocation.getBearing() - mCurrentLocation
                        .bearingTo(nextPoint)) / 2;
                Log.d(TAG, "Relative bearing:" + bearing);
                // transform bearing from degrees to hours
                bearing = (int) (bearing / 360 * 12);
                // calculate ETA
                float eta = -1;
                if (speed != 0) {
                    eta = distance / speed;
                }

                // assemble text
                String announcement = constructAnnouncement(distance, bearing, eta, speed);

                tts.speak(announcement, TextToSpeech.QUEUE_FLUSH, null);
            } catch (WaypointNotFoundException e) {
                Log.d(TAG, "No next point found, doing nothing");
            }

            handler.postDelayed(announcer, ANNOUNCEMENT_INTERVAL);
        }

        private String constructAnnouncement(float distance, float bearing, float eta, float speed) {
            // Waypoint is at [bearing] o'clock.
            // Distance to waypoint is [distance] with an E.T.A of
            // [hours, minutes, seconds] at the current speed of
            // [] kilometers per hour
            StringBuilder announcement = new StringBuilder();
            announcement.append("Waypoint is at " + ((int) bearing + 1) + " o'clock. ");

            // calculate kilometers
            int kilometers = (int) (distance / 1000);
            int meters = (int) (distance - kilometers * 1000);
            //
            announcement.append("Distance to waypoint is "
                    + ((kilometers > 0) ? kilometers + " kilometers" : "")
                    + ((meters > 0) ? meters + " meters" : ""));
            // only add eta if we have a speed
            if (eta > -1) {
                // calculate hours, minutes, seconds
                int hours = (int) (eta / 60 / 60);
                int minutes = (int) ((eta - hours * 60 * 60) / 60);
                int seconds = (int) (eta - hours * 60 * 60 - minutes * 60);

                announcement.append(" with an E.T.A. of " + ((hours > 0) ? hours + " hours " : "")
                        + ((minutes > 0) ? minutes + " minutes " : "")
                        + ((seconds > 0) ? seconds + " seconds " : ""));

                DecimalFormat formattedSpeed = new DecimalFormat("###.##");
                formattedSpeed.setRoundingMode(RoundingMode.FLOOR);
                // calculate speed in km/h
                announcement.append(" at the current speed of "
                        + formattedSpeed.format(speed * 3.6) + " kilometers per hour.");
            } else {
                announcement.append(" with an unknown E.T.A. as there is no speed information.");
            }
            return announcement.toString();
        }

    };

    /**
     * Handler of incoming messages from service.
     */
    private class IncomingHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case LocationService.MSG_LOCATION_CHANGED:
                if (msg.obj != null) {
                    Log.d(TAG, ((Location) msg.obj).toString());
                    mCurrentLocation = ((Location) msg.obj);
                    mCurrentLat = ((Location) msg.obj).getLatitude();
                    mCurrentLong = ((Location) msg.obj).getLongitude();
                    mCurrentAccuracy = ((Location) msg.obj).getAccuracy();
                    // TODO redraw the overlay ???
                    drawPathToNextWaypoint();
                    waypointController.update((Location) msg.obj);

                } else {
                    Log.d(TAG, "Null object received");
                }
                break;
            default:
                super.handleMessage(msg);
            }
        }
    }

    /**
     * Class for interacting with the main interface of the service.
     */
    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            Log.d(TAG, "Location service attached.");
            // register client
            sendMessage(LocationService.MSG_REGISTER_CLIENT);
            // register interface
            sendMessage(LocationService.MSG_REGISTER_INTERFACE);

            // send walk or drive mode
            sendMessage(mDrive ? LocationService.MSG_DRIVE : LocationService.MSG_WALK);
        }

        /*
         * (non-Javadoc)
         * @see android.content.ServiceConnection#onServiceDisconnected(android.content
         * .ComponentName)
         */
        @Override
        public void onServiceDisconnected(ComponentName className) {
            // Called when the connection with the service has been
            // unexpectedly disconnected / process crashed.
            mService = null;
            Log.d(TAG, "Disconnected from location service");
        }
    };

    /**
     * Drive or walk preference listener. A listener is necessary for this option as the location
     * service needs to be notified of the change in order to change location update frequency. The
     * preference is sent when the activity comes into view and rebinds to the location service.
     */
    private final SharedPreferences.OnSharedPreferenceChangeListener mPrefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Log.d(TAG, "Settings changed :" + sharedPreferences + " " + key);
            mDrive = mSettings.getBoolean(Preferences.DRIVE_MODE, false);
        }
    };
    private WaypointManager waypointManager;
    private WaypointController waypointController;

    private GoogleMap mMap;
    private boolean waypointModeActive;

    private final OnMapClickListener waypointAddListener = new OnMapClickListener() {

        @Override
        public void onMapClick(LatLng latLng) {
            Log.d(TAG, "Map clicked at location: " + latLng.toString());
            if (waypointModeActive) {
                // TODO replace location with latlng ?
                Location location = new Location("fake");
                location.setLatitude(latLng.latitude);
                location.setLongitude(latLng.longitude);
                waypointManager.addWaypoint(new Waypoint(location, false));
            }
        }
    };
    private TextToSpeech tts;

    // ==================== LIFECYCLE METHODS ====================

    /*
     * (non-Javadoc)
     * @see com.google.android.maps.MapActivity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);
        mSettings.registerOnSharedPreferenceChangeListener(mPrefListener);
        setContentView(R.layout.main);

        // get map handle

        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapview))
                .getMap();

        mMap.setOnMapClickListener(waypointAddListener);
        waypointManager = new WaypointManager();
        waypointController = new WaypointController(waypointManager);
        waypointManager.addObserver(this);
        // TODO set initial/remembered map zoom

        Log.d(TAG, "onCreate completed: Activity created");
        mLocationServiceIntent = new Intent(SERVICE_INTENT_NAME);
        startService(mLocationServiceIntent);
        loadFileFromIntent();
        // check we still have access to GPS info
        checkConnectivity();

        update(waypointManager, new MarkerMessage(null, 0, MarkerMessage.State.ADD_MULTIPLE));
        // initialize text to speech
        tts = new TextToSpeech(this, this);

        // announce at fixed intervals
        handler.postDelayed(announcer, ANNOUNCEMENT_INTERVAL);

        mMap.setMyLocationEnabled(true);
        moveToLastLocation(savedInstanceState);
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

            @Override
            public void onMarkerDragStart(Marker arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onMarkerDragEnd(Marker arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onMarkerDrag(Marker arg0) {
                drawPathsBetweenWaypoints();
            }
        });

    }

    private void moveToLastLocation(Bundle savedInstanceState) {
        // if the saved instance state is null
        // try to get the values from shared preferences
        // as the application is not being restored but started
        float zoom = 5;
        if (savedInstanceState == null) {
            // load from preferences
            SharedPreferences prefs = getSharedPreferences(Preferences.JANE_PREFS,
                    Context.MODE_PRIVATE);
            // restore accuracy and coordinates from saved state
            mCurrentAccuracy = prefs.getFloat(ACCURACY_SAVE, 0);
            mCurrentLat = prefs.getFloat(LATITUDE_SAVE, 0);
            mCurrentLong = prefs.getFloat(LONGITUDE_SAVE, 0);
            zoom = prefs.getFloat(ZOOM_SAVE, 5f);
        } else {
            // restore accuracy and coordinates from saved state
            mCurrentAccuracy = savedInstanceState.getDouble(ACCURACY_SAVE, 0);
            mCurrentLat = savedInstanceState.getDouble(LATITUDE_SAVE, 0);
            mCurrentLong = savedInstanceState.getDouble(LONGITUDE_SAVE, 0);
            waypointModeActive = savedInstanceState.getBoolean(WAYPOINT_EDITING_SAVE);
            zoom = savedInstanceState.getFloat(ZOOM_SAVE, 5f);
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentLat, mCurrentLong),
                zoom));
    }

    /**
     * Loads waypoints data from a file path sent through an intent.
     */
    private void loadFileFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {

            Uri data = intent.getData();

            if (data != null) {

                final String filePath = data.getEncodedPath();

                final ProgressDialog progress = new ProgressDialog(this);

                progress.setCancelable(false);
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setMessage(getString(R.string.importing_locations));
                progress.show();

                Runnable importer = new Runnable() {

                    @Override
                    public void run() {
                        progress.dismiss();
                        // TODO add file import support
                        throw new UnsupportedOperationException("file import not supported");
                    }
                };
                new Thread(importer).start();
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onRestoreInstanceState(android.os.Bundle)
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        moveToLastLocation(savedInstanceState);
        super.onRestoreInstanceState(savedInstanceState);
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        saveStateOnSystemExit(outState);
        super.onSaveInstanceState(outState);
    }

    private void saveStateOnSystemExit(Bundle outState) {
        // save accuracy and coordinates
        outState.putDouble(ACCURACY_SAVE, mCurrentAccuracy);
        outState.putDouble(LATITUDE_SAVE, mCurrentLat);
        outState.putDouble(LONGITUDE_SAVE, mCurrentLong);
        outState.putBoolean(WAYPOINT_EDITING_SAVE, waypointModeActive);
        outState.putFloat(ZOOM_SAVE, 5f);

    }

    private void saveStateOnUserExit() {
        SharedPreferences prefs = getSharedPreferences(Preferences.JANE_PREFS, Context.MODE_PRIVATE);
        Editor edit = prefs.edit();
        edit.putFloat(ACCURACY_SAVE, (float) mCurrentAccuracy);
        edit.putFloat(LATITUDE_SAVE, (float) mCurrentLat);
        edit.putFloat(LONGITUDE_SAVE, (float) mCurrentLong);
        edit.putFloat(ZOOM_SAVE, 5f);
        edit.commit();
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onStart()
     */
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart completed: Activity started");
    }

    /*
     * (non-Javadoc)
     * @see com.google.android.maps.MapActivity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();
        // register zoom && pan mZoomPanHandler
        // mZoomPanHandler.postDelayed(mZoomChecker, ZOOM_CHECKING_DELAY);
        // set the visibility flag to start overlay updates
        mVisible = true;

        // TOOD maybe redraw ?
        Log.d(TAG, "onResume completed.");
        // bind to location service
        doBindService();

    }

    /*
     * (non-Javadoc)
     * @see com.google.android.maps.MapActivity#onPause()
     */
    @Override
    protected void onPause() {
        super.onPause();

        // mZoomPanHandler.removeCallbacks(mZoomChecker);
        mVisible = false;
        // unbind from service as the activity does
        // not display location info (is hidden or stopped)
        doUnbindService();
        Log.d(TAG, "onPause completed.");
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onStop()
     */
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop completed.");
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onRestart()
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart completed.");
    }

    /*
     * (non-Javadoc)
     * @see com.google.android.maps.MapActivity#onDestroy()
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        Log.d(TAG, "onDestroy completed.");
    }

    // ================= END LIFECYCLE METHODS ====================

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        displayMenu(menu);
        return result;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean result = super.onPrepareOptionsMenu(menu);
        displayMenu(menu);

        return result;
    }

    private void displayMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        menu.clear();

        if (waypointModeActive) {
            inflater.inflate(R.layout.add_waypoint, menu);
        } else {
            inflater.inflate(R.layout.menu, menu);
        }
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.add_waypoint:
            Log.d(TAG, "Add waypoint clicked in action bar.");
            waypointModeActive = true;
            invalidateOptionsMenu();
            return true;

        case R.id.done_waypoints:
            Log.d(TAG, "Add waypoint clicked in action bar.");
            waypointModeActive = false;
            invalidateOptionsMenu();
            return true;

        case R.id.help:
            Log.d(TAG, "Showing help...");
            Intent helpIntent = new Intent(this, Help.class);
            startActivity(helpIntent);
            return true;
        case R.id.exit:
            Log.d(TAG, "Exit requested...");
            doUnbindService();
            // cleanup
            stopService(mLocationServiceIntent);
            saveStateOnUserExit();
            finish();
            return true;
        case R.id.settings:
            Intent settingsIntent = new Intent(this, Preferences.class);
            startActivity(settingsIntent);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Checks GPS and network connectivity. Displays a dialog asking the user to start the GPS if
     * not started and also displays a toast warning it no network connectivity is available.
     */
    private void checkConnectivity() {

        boolean isGPS = ((LocationManager) getSystemService(LOCATION_SERVICE))
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!isGPS) {
            createGPSDialog().show();
        }
        displayConnectivityWarning();
    }

    /**
     * Displays a toast warning if no network is available.
     */
    private void displayConnectivityWarning() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean connected = false;
        for (NetworkInfo info : connectivityManager.getAllNetworkInfo()) {
            if (info.getState() == NetworkInfo.State.CONNECTED
                    || info.getState() == NetworkInfo.State.CONNECTING) {
                connected = true;
                break;
            }
        }

        if (!connected) {
            Toast.makeText(getApplicationContext(), R.string.connectivity_warning,
                    Toast.LENGTH_LONG).show();

        }
    }

    /**
     * Creates the GPS dialog displayed if the GPS is not started.
     * 
     * @return the GPS Dialog
     */
    private Dialog createGPSDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.gps_dialog).setCancelable(false);

        final AlertDialog alert = builder.create();

        alert.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.start_gps_btn),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        alert.dismiss();
                        startActivity(new Intent(
                                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                });

        alert.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.continue_no_gps),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        alert.dismiss();
                    }
                });
        return alert;
    }

    /**
     * Binds to the location service. Called when the activity becomes visible.
     */
    private void doBindService() {
        bindService(mLocationServiceIntent, mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
        Log.d(TAG, "Binding to location service");
    }

    /**
     * Unbinds from the location service. Called when the activity is stopped or closed.
     */
    private void doUnbindService() {
        if (mIsBound) {
            // test if we have a valid service registration
            if (mService != null) {
                sendMessage(LocationService.MSG_UNREGISTER_INTERFACE);
            }

            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
            Log.d(TAG, "Unbinding map from location service.");
        }
    }

    private void sendMessage(int message) {
        // TODO check message
        try {
            Message msg = Message.obtain(null, message);
            msg.replyTo = mMessenger;
            mService.send(msg);
        } catch (RemoteException e) {
            // NO-OP
            // Nothing special to do if the service
            // has crashed.
        }
    }

    private final TreeMap<Integer, Marker> markers = new TreeMap<Integer, Marker>();
    private Polyline mReachedPath;
    private Polyline mUnreachedPath;
    private Polyline lineToWaypoint;

    @Override
    public void update(Observable observable, Object data) {
        Log.d(TAG, "Message received" + data.toString());
        Waypoint waypoint = ((MarkerMessage) data).getWaypoint();
        State state = ((MarkerMessage) data).getState();
        int index = ((MarkerMessage) data).getIndex();
        switch (state) {
        case DELETE_ALL:
            for (Marker marker : markers.values()) {
                // remove from map
                marker.remove();
            }
            markers.clear();
        case ADD_MULTIPLE:
            ArrayList<Waypoint> waypoints = waypointManager.getWaypoints();
            // iterate through all the waypoints and if there is
            // a waypoint that does not have its index in the markers
            // list, create a marker for it and add it to the list
            for (Waypoint point : waypoints) {

                if (markers.get(index) == null)
                    markers.put(index, createMarkerFromWaypoint(point));
            }
            break;
        case ADD:
            markers.put(index, createMarkerFromWaypoint(waypoint));
            break;
        case DELETE:
            // remove from map
            markers.get(index).remove();
            // remove from list
            markers.remove(index);
            break;
        case REACH:
            // remove and add as the icon cannot be changed after creation
            markers.get(index).remove();
            // remove from list
            markers.remove(index);
            markers.put(index, createMarkerFromWaypoint(waypoint));
            Log.d(TAG, "Marker reached  changing colour");
            announceReached();
            break;
        default:
            Log.e(TAG, "Unknown message received from observable" + data.toString());
            break;
        }
        drawPathsBetweenWaypoints();
    }

    private void drawPathsBetweenWaypoints() {
        // only remove if previously has been added to map
        // XXX hacky fix
        if (mReachedPath != null) {
            mUnreachedPath.remove();
            mReachedPath.remove();
        }
        // redraw mWaypointPath between markers
        mReachedPath = mMap.addPolyline(new PolylineOptions().width(5).color(Color.GREEN));

        mUnreachedPath = mMap.addPolyline(new PolylineOptions().width(5).color(Color.RED));

        // create LatLng list from markers
        ArrayList<LatLng> latLngsReached = new ArrayList<LatLng>();
        ArrayList<LatLng> latLngsUnreached = new ArrayList<LatLng>();

        Set<Integer> keys = markers.keySet();

        for (Integer key : keys) {

            try {
                if (waypointManager.getWaypoint(key).isReached()) {
                    latLngsReached.add(markers.get(key).getPosition());
                } else {
                    latLngsUnreached.add(markers.get(key).getPosition());
                }
            } catch (WaypointNotFoundException e) {
                Log.d(TAG, "Waypoint could not be found for index " + key);
            }
        }
        mReachedPath.setPoints(latLngsReached);
        // add the first reached point to the list of unreached in order to
        // have
        // continous line
        if (latLngsReached.size() > 0) {
            latLngsUnreached.add(0, latLngsReached.get(latLngsReached.size() - 1));
        }
        mUnreachedPath.setPoints(latLngsUnreached);

        // also redraw line to current location
        drawPathToNextWaypoint();
    }

    private void announceReached() {
        tts.speak("Waypoint reached.", TextToSpeech.QUEUE_FLUSH, null);
    }

    private Marker createMarkerFromWaypoint(Waypoint waypoint) {
        BitmapDescriptor icon;
        if (waypoint.isReached()) {
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
        } else {
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
        }
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(LocationUtilities.locationToLatLng(waypoint)).title(waypoint.getInfo())
                .icon(icon));
        marker.setDraggable(true);
        return marker;
    }

    private void drawPathToNextWaypoint() {
        Log.d(TAG, "Drawing path to first unreached waypoint.");
        LatLng current = new LatLng(mCurrentLat, mCurrentLong);
        if (lineToWaypoint != null) {
            lineToWaypoint.remove();
        }

        try {
            final Waypoint point = waypointManager.getFirstUnreached();
            final LatLng unreached = new LatLng(point.getLatitude(), point.getLongitude());
            Log.d(TAG, "First unreached waypoint is at " + unreached.toString());

            lineToWaypoint = mMap.addPolyline(new PolylineOptions().add(current, unreached)
                    .width(5).color(Color.BLUE));

        } catch (WaypointNotFoundException e) {
            Log.d(TAG, "No unexplored waypoint found, not drawing line.");
        }
    }

    // Text to speech initialization

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // DISPLAY dialog maybe ?

                Log.e(TAG, "This Language is not supported");
            } else {
                Log.d(TAG, "TTS Initialization succesful");
            }

        } else {
            Log.e(TAG, "TTS Initialization failed!");
        }
    }
}
