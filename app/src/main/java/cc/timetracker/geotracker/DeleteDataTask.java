package cc.timetracker.geotracker;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by admin on 27.04.17.
 */

public class DeleteDataTask extends AsyncTask<String, Void, String> {

    ProgressDialog progressDialog;

    MainActivity mainActivity;

    public DeleteDataTask(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected String doInBackground(String... params) {

        try {
            return deleteData(params[0]);
        } catch (IOException ex) {
            return "Network error ! ";
        }

    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        AlertDialog alertDialog = new AlertDialog.Builder(mainActivity).create();
        alertDialog.setTitle("Delete Recorded Data");
        alertDialog.setMessage(result);
        alertDialog.show();

    }


    private String deleteData(String urlPath) throws IOException {

        String result = null;
        result = "Test ";

        // Init and config request, then connect to server
        URL url = new URL(urlPath);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setReadTimeout(10000/* milliseconds*/);
        urlConnection.setConnectTimeout(10000 /*milliseconds*/);
        urlConnection.setRequestMethod("DELETE");
        urlConnection.setRequestProperty("Content-Type", "application/json"); //set header
        urlConnection.connect();

        if (urlConnection.getResponseCode() == 204) {
            result = "Delete successfully !";
        } else {
            result = "Delete failed !";
        }

        return result;
    }

}