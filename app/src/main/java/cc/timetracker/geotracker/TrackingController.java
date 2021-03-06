/*
 * Copyright 2015 Anton Tananaev (anton.tananaev@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cc.timetracker.geotracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;

public class TrackingController implements PositionProvider.PositionListener, NetworkManager.NetworkHandler {

    private static final String TAG = TrackingController.class.getSimpleName();
    private static final int RETRY_DELAY = 30 * 1000;
    private static final int WAKE_LOCK_TIMEOUT = 120 * 1000;

    private boolean isOnline;
    private boolean isWaiting;

    private Context context;
    private Handler handler;
    private SharedPreferences preferences;

    private String address;
    private int port;
    private boolean secure;

    private PositionProvider positionProvider;
    private DatabaseHelper databaseHelper;
    private NetworkManager networkManager;

    private PowerManager.WakeLock wakeLock;

    private void lock() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            wakeLock.acquire();
        } else {
            wakeLock.acquire(WAKE_LOCK_TIMEOUT);
        }
    }

    private void unlock() {
        if (wakeLock.isHeld()) {
            wakeLock.release();
        }
    }

    public TrackingController(Context context) {
        this.context = context;
        handler = new Handler();
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences.getString(MainActivity.KEY_PROVIDER, "gps").equals("mixed")) {
            positionProvider = new MixedPositionProvider(context, this);
        } else {
            positionProvider = new SimplePositionProvider(context, this);
        }
        databaseHelper = new DatabaseHelper(context);
        networkManager = new NetworkManager(context, this);
        isOnline = networkManager.isOnline();


        address = preferences.getString(MainActivity.KEY_ADDRESS, null);
        port = 0;
        secure = false;
//        port = Integer.parseInt(preferences.getString(MainActivity.KEY_PORT, null));
//        secure = preferences.getBoolean(MainActivity.KEY_SECURE, false);

        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
    }

    public void start() {
        if (isOnline) {
            read();
        }
        try {
            positionProvider.startUpdates();
        } catch (SecurityException e) {
            Log.w(TAG, e);
        }
        networkManager.start();
    }

    public void stop() {
        networkManager.stop();
        try {
            positionProvider.stopUpdates();
        } catch (SecurityException e) {
            Log.w(TAG, e);
        }
        handler.removeCallbacksAndMessages(null);
    }


    // TODO new Code - add lat & lon to Positionupdate
    @Override
    public void onPositionUpdate(Position position) {
        StatusActivity.addMessage(
                context.getString(R.string.status_location_update) +
                "\n" +
                " lat: " + position.getLatitude() +
                " lon: " + position.getLongitude()
        );

        if (position != null) {
            write(position);
            Log.d(TAG, "position != null " + position.getLatitude());
        }
    }

    @Override
    public void onNetworkUpdate(boolean isOnline) {
        StatusActivity.addMessage(context.getString(R.string.status_connectivity_change));
        if (!this.isOnline && isOnline) {
            read();
        }
        this.isOnline = isOnline;

    }

    //
    // State transition examples:
    //
    // write -> read -> send -> delete -> read
    //
    // read -> send -> retry -> read -> send
    //

    private void log(String action, Position position) {
        if (position != null) {
            action += " (" +
                    "id:" + position.getId() +
                    " time:" + position.getTime().getTime() / 1000 +
                    " lat:" + position.getLatitude() +
                    " lon:" + position.getLongitude() +
                    " device:" + position.getDeviceId() + ")";
        }
        Log.d(TAG, action);
    }




    private void write(Position position) {
        log("write", position);

      //  String request = ProtocolFormatter.formatRequest(address, port, secure, position);
      //  Log.d("senddddddd", request);

      //ToDo here write Data in Database - Correct JsonFormat

        // ToDo check Übertragung wenn keine Übertragbung dann sende bei nächster Übertragung
        // ToDo implement Butten zum analogen senden
        // ToDo implement Anzeige von Lat und Lon bei Positionsupdate
        // ToDo implement richtiges Json Format
        // ToDo Gerlicher schreiben wegen Anmeldung des Innovationsprojektes
        // ToDo Hurry up
        lock();

        databaseHelper.insertPositionAsync(position, new DatabaseHelper.DatabaseHandler<Void>() {
            @Override
            public void onComplete(boolean success, Void result) {
                log("sucess: " + success + " isOnline: " + isOnline + " waiting: " + isWaiting, null);

                if (success) {
                    if (isOnline && isWaiting) {
                        read();
                        isWaiting = false;
                    }

                }
                unlock();
            }
        });
    }


    private void read() {
        log("read", null);
        // Update GPS Signal Sign

        lock();
        databaseHelper.selectPositionAsync(new DatabaseHelper.DatabaseHandler<Position>() {
            @Override
            public void onComplete(boolean success, Position result) {
                if (success) {
                    if (result != null) {
                        if (result.getDeviceId().equals(preferences.getString(MainActivity.KEY_DEVICE, null))) {
                            send(result);
                        } else {
                            delete(result);
                        }
                    } else {
                        isWaiting = true;
                    }
                } else {
                    retry();
                }
                unlock();
            }
        });
    }

    private void delete(Position position) {
        log("delete", position);
        lock();
        databaseHelper.deletePositionAsync(position.getId(), new DatabaseHelper.DatabaseHandler<Void>() {
            @Override
            public void onComplete(boolean success, Void result) {
                if (success) {
                    read();
                } else {
                    retry();
                }
                unlock();
            }
        });
    }

    // ToDo: Changes - when sending create a new Jsonformant

    private void send(final Position position) {
        log("send", position);
        lock();
        String request = ProtocolFormatter.formatRequest(address, port, secure, position);


        // !!! New Code !!! - Create a new JsonFormat to send out
        CreateJson createJson = new CreateJson(position);
        createJson.create();
        request = createJson.getJsonAsString();

        RequestManager.sendRequestAsync(context, request, new RequestManager.RequestHandler() {
            @Override
            public void onComplete(boolean success) {
                if (success) {
                    delete(position);
                } else {
                    StatusActivity.addMessage(context.getString(R.string.status_send_fail));
                    retry();
                }
                unlock();
            }
        });
    }

    private void retry() {
        log("retry", null);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isOnline) {
                    read();
                }
            }
        }, RETRY_DELAY);
    }





}
