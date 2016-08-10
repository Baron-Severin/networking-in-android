package com.severin.baron.networking_in_android;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    JSONObject jsonObject;
    List<String> names;
    RecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        names = new ArrayList<>();

        try {
            new RunAsyncTask().execute("Cereal");
        } catch (Exception e) {
            e.printStackTrace();
        }

        RecyclerView rvContacts = (RecyclerView) findViewById(R.id.recyclerView);

        // Initialize contacts
        // Create adapter passing in the sample user data
        adapter = new RecyclerAdapter(this, names);
        // Attach the adapter to the recyclerview to populate items
        rvContacts.setAdapter(adapter);
        // Set layout manager to position the items
        rvContacts.setLayoutManager(new LinearLayoutManager(this));

    }

    private JSONObject downloadUrl(String myQuery) throws IOException, JSONException {
        InputStream is = null;

        try {
            URL url = new URL(queryBuilder(myQuery));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            // Starts the query
            conn.connect();
            is = conn.getInputStream();

            // Converts the InputStream into a string

            String contentAsString = readIt(is);
            Log.d("SEVTEST ", contentAsString);
            JSONObject jsonObject = new JSONObject(contentAsString);

            System.out.println("");
            JSONArray jsonArray = jsonObject.getJSONArray("items");

            List<String> nameList = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object =  ((JSONObject)jsonArray.get(i));
                nameList.add((String) object.get("name"));
            }

            names.addAll(nameList);

            return jsonObject;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }

    }



    public String readIt(InputStream stream) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        String read;

        while((read = br.readLine()) != null) {
            sb.append(read);
        }
        return sb.toString();
    }

    private String queryBuilder(String query) {
        StringBuilder sb = new StringBuilder();
        sb.append(getResources().getString(R.string.baseUrl));
        sb.append(getResources().getString(R.string.API_KEY));
        sb.append("=");
        sb.append(getResources().getString(R.string.myKey));
        sb.append("&");
        sb.append(getResources().getString(R.string.QUERY));
        sb.append("=");
        sb.append(query);
        query = sb.toString();
        Log.d("SEVTEST ", query);
        return query;
    }

    private class RunAsyncTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPostExecute(JSONObject jsO) {
            super.onPostExecute(jsO);
            jsonObject = jsO;
            adapter.notifyDataSetChanged();
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject = downloadUrl(strings[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonObject;
        }
    }


}
