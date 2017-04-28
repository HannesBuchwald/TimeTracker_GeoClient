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
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class RequestManager {


    private static final String TAG = RequestManager.class.getSimpleName();


    private static final int TIMEOUT = 15 * 1000;
    private static SharedPreferences preferences;


    public interface RequestHandler {
        void onComplete(boolean success);
    }


    private static class RequestAsyncTask extends AsyncTask<String, Void, Boolean> {

        private RequestHandler handler;


        public RequestAsyncTask(Context context, RequestHandler handler) {
            this.handler = handler;
            preferences = PreferenceManager.getDefaultSharedPreferences(context);
        }

        @Override
        protected Boolean doInBackground(String... request) {
            return sendRequest(request[0]);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            handler.onComplete(result);
        }
    }



    public static boolean sendRequest(String request) {

        InputStream inputStream = null;
        OutputStream outputStream = null;

        BufferedWriter bufferedWriter = null;
        BufferedReader bufferedReader = null;

        String address = preferences.getString(MainActivity.KEY_ADDRESS, null);

//        address = "https://radiant-temple-85392.herokuapp.com/api/status/";

        // !!!New Code!!!
        // Own implementation of PUT Service

        try {
            // Init and config request, then connect to server
            URL url = new URL(address);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(TIMEOUT);
            urlConnection.setConnectTimeout(TIMEOUT);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json"); //set header
            urlConnection.connect();


            // Write to Sever
            outputStream = urlConnection.getOutputStream();
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
            bufferedWriter.write(request);
            bufferedWriter.flush();


            // Read respond
            inputStream = urlConnection.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = bufferedReader.readLine()) != null) {
                response.append(line).append("\n");
            }

            // ToDo Check the result and if true do not send again
//            if(request != response.toString()) {
//                return false;
//            }


            Log.d(TAG, "send ddddd: " + request);

            return true;


            // Old code
//            URL url = new URL(request);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setReadTimeout(TIMEOUT);
//            connection.setConnectTimeout(TIMEOUT);
//            connection.connect();
//            inputStream = connection.getInputStream();
//            while (inputStream.read() != -1);
//            return true;

        } catch (IOException error) {
            return false;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (bufferedReader != null) bufferedReader.close();
                if (bufferedWriter != null) bufferedWriter.close();
            } catch (IOException secondError) {
                return false;
            }
        }
    }

    public static void sendRequestAsync(Context context, String request, RequestHandler handler) {
        RequestAsyncTask task = new RequestAsyncTask(context, handler);
        task.execute(request);
    }

}
