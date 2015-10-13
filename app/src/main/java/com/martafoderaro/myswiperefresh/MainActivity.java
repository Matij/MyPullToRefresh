package com.martafoderaro.myswiperefresh;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.widget.ListView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity{

    private String URL = "http://api.makeitapp.com/v2/getevents.php?userid=560408208b170";

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private SwipeListAdapter adapter;
    private List<RowElement> listElements;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listView);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_bright));
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(getResources().getColor(android.R.color.holo_orange_light));
        swipeRefreshLayout.setColorSchemeColors(Color.WHITE);

        listElements = new ArrayList<>();

        adapter = new SwipeListAdapter(MainActivity.this, listElements);
        listView.setAdapter(adapter);

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
                downloadElements();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                listElements.clear();
                downloadElements();
            }
        });
    }

    private void downloadElements(){

        new RetrieveLocationsTask().execute();


    }

    private class RetrieveLocationsTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected String doInBackground(String... params) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            String responseString = null;
            try {
                response = httpclient.execute(new HttpGet(URL));
                StatusLine statusLine = response.getStatusLine();
                if(statusLine.getStatusCode() == HttpStatus.SC_OK){

                    HttpEntity entity = response.getEntity();
                    responseString = EntityUtils.toString(entity, HTTP.UTF_8);
                } else{
                    //Closes the connection.
                    swipeRefreshLayout.setRefreshing(false);
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (ClientProtocolException e) {
                //TODO Handle problems..
            } catch (IOException e) {
                //TODO Handle problems..
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String resultString) {
            super.onPostExecute(resultString);
            try {
                JSONObject json = new JSONObject(resultString);
                Log.i("onPostExecute", resultString);
                JSONArray arrayData = json.getJSONArray("data");
                for (int i=0; i<arrayData.length(); i++) {
                    JSONObject dataElement = arrayData.getJSONObject(i);

                    RowElement rowElement = new RowElement();
                    rowElement.setTitle(dataElement.getString("title"));
                    rowElement.setSubtitle(dataElement.getString("subtitle"));

                    listElements.add(rowElement);

                }
                adapter.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
            }
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}
