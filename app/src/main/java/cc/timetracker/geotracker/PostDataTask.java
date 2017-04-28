package org.traccar.client;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by admin on 24.04.17.
 */

public class PostDataTask extends AsyncTask<String, Void, String> {

    private static final String TAG = PostDataTask.class.getSimpleName();


    ProgressDialog progressDialog;

    Position position = null;




    public PostDataTask(Position position) {
        this.position = position;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        //progressDialog = new ProgressDialog(MainActivity.this);
        //progressDialog.setMessage("Inserting data ...");
        //progressDialog.show();

    }

    @Override
    protected String doInBackground(String... params) {

        try {
            return postData(params[0]);
        } catch (IOException ex) {
            return " Netowrk error ! ";
        } catch (JSONException ex) {
            return "Data Invalid !";
        }

    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        // set date response to textView
        // if (mResult != null) mResult.setText(result);

        Log.d(TAG, "send success " + result);

        // cancel ProgressDialog
        // if (progressDialog != null) progressDialog.dismiss();
    }


    private String postData(String urlPath) throws IOException, JSONException {

        StringBuilder result = new StringBuilder();
        BufferedWriter bufferedWriter = null;
        BufferedReader bufferedReader = null;


        try {
            // Create Data to send  to server
            JSONObject dataToSend = new JSONObject();
            dataToSend.put("fbname", String.valueOf(position.getLongitude()));
            dataToSend.put("content", String.valueOf(position.getLatitude()));
            dataToSend.put("likes", 1223);
            dataToSend.put("comments", 8345329);
            dataToSend.put("cool", "XXXXXXXXXXXXX");

            // Init and config request, then connect to server
            URL url = new URL(urlPath);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000/* milliseconds*/);
            urlConnection.setConnectTimeout(10000 /*milliseconds*/);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json"); //set header
            urlConnection.connect();


            // Write to Sever
            OutputStream outputStream = urlConnection.getOutputStream();
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
            bufferedWriter.write(dataToSend.toString());
            bufferedWriter.flush();


            // Read respond
            InputStream inputStream = urlConnection.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                result.append(line).append("\n");
            }
        } finally {
            if (bufferedReader != null) bufferedReader.close();
            if (bufferedWriter != null) bufferedWriter.close();
        }

        return result.toString();
    }
}
