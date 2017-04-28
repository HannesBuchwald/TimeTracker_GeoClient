package cc.timetracker.getmygeop;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

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

public class MainActivity extends AppCompatActivity {

    private TextView mResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        init();

        // make Get Request
        new GetDataTask().execute("https://radiant-temple-85392.herokuapp.com/api/status");

        // make Post Request
        //new PostDataTask().execute("https://radiant-temple-85392.herokuapp.com/api/status");


        //make Put
        //new PutDataTask().execute("https://radiant-temple-85392.herokuapp.com/api/status/");


        // make Delete
        //new DeleteDataTask().execute("http://192.168.2.102:3000/api/status/");

    }


    private void init() {
        mResult = (TextView) findViewById(R.id.tv_result);

    }


    class GetDataTask extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Loading data ...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                return getData(params[0]);
            } catch (IOException ex) {
                return "Network error: " + ex;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // set date response to textView
            if (mResult != null) mResult.setText(result);

            // cancel ProgressDialog
            if (progressDialog != null) progressDialog.dismiss();
        }


        private String getData(String urlPath) throws IOException {

            // Init and config request, than connect to server
            StringBuilder result = new StringBuilder();
            BufferedReader bufferedReader = null;

            try {
                URL url = new URL(urlPath);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000/* milliseconds*/);
                urlConnection.setConnectTimeout(10000 /*milliseconds*/);
                urlConnection.setRequestMethod("GET");
               // urlConnection.setRequestProperty("Content-Type", "application/json"); //set header
                urlConnection.connect();

                //Read data response from server
                InputStream inputStream = urlConnection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line).append("\n");

                }

            } finally {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            }

            return result.toString();
        }
    }


    class PostDataTask extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Inserting data ...");
            progressDialog.show();

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
            if (mResult != null) mResult.setText(result);

            // cancel ProgressDialog
            if (progressDialog != null) progressDialog.dismiss();
        }


        private String postData(String urlPath) throws IOException, JSONException {

            StringBuilder result = new StringBuilder();
            BufferedWriter bufferedWriter = null;
            BufferedReader bufferedReader = null;


            try {
                // Create Data to send  to server
                JSONObject dataToSend = new JSONObject();
                dataToSend.put("fbname", "Think twice code once");
                dataToSend.put("content", "feel good");
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


    class PutDataTask extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Updating data ...");
            progressDialog.show();

        }

        @Override
        protected String doInBackground(String... params) {

            try {
                return putData(params[0]);
            } catch (IOException ex) {
                return " Network error ! ";
            } catch (JSONException ex) {
                return "Data Invalid !";
            }

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // set date response to textView
            if (mResult != null) mResult.setText(result);

            // cancel ProgressDialog
            if (progressDialog != null) progressDialog.dismiss();
        }


        private String putData(String urlPath) throws IOException, JSONException {

            BufferedWriter bufferedWriter = null;


            try {
                // Create Data to send  to server
                JSONObject dataToSend = new JSONObject();
                dataToSend.put("fbname", "Think twice code once");
                dataToSend.put("content", "feel good Updating!!!!!!!!!!!!!!!!!!!!");
                dataToSend.put("likes", 1223);
                dataToSend.put("comments", 8345329);
                dataToSend.put("cool", "HHHHHHHHHHHH");


                // Init and config request, then connect to server
                URL url = new URL(urlPath);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000/* milliseconds*/);
                urlConnection.setConnectTimeout(10000 /*milliseconds*/);
                urlConnection.setRequestMethod("PUT");
                urlConnection.setDoOutput(true); //enable output (body data)
                urlConnection.setRequestProperty("Content-Type", "application/json"); //set header
                urlConnection.connect();


                // Write to Sever
                OutputStream outputStream = urlConnection.getOutputStream();
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
                bufferedWriter.write(dataToSend.toString());
                bufferedWriter.flush();


                if (urlConnection.getResponseCode() == 200) {
                    return "Update succefully !";
                } else {
                    return "Update failed !";
                }

            } finally {
                if (bufferedWriter != null) bufferedWriter.close();
            }
        }
    }


    class DeleteDataTask extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Deleting data ...");
            progressDialog.show();

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

            // set date response to textView
            if (mResult != null) mResult.setText(result);

            // cancel ProgressDialog
            if (progressDialog != null) progressDialog.dismiss();
        }


        private String deleteData(String urlPath) throws IOException {

            String result = null;

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
}


