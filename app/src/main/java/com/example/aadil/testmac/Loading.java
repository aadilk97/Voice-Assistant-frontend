package com.example.aadil.testmac;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ProgressBar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;


public class Loading extends Activity {
    private ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading);

        pb = (ProgressBar) findViewById(R.id.progressBar);
        pb.setVisibility(View.VISIBLE);
        new MyAsyncTask1().execute();

    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }


    private class MyAsyncTask1 extends AsyncTask<String, Void, String> {
        String json;
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            String url = "https://nameless-escarpment-30648.herokuapp.com/";


            url += "what%20is%20your%20name";

            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse response;
            // Creating HTTP Post
            HttpGet httpGet = new HttpGet(url);



            try {
                response = httpClient.execute(httpGet);
                HttpEntity entity = response.getEntity();
                json = EntityUtils.toString(entity);
                return json;

                //txt.setText(json);
            }
            catch(ClientProtocolException e){
                e.printStackTrace();
            }
            catch(IOException e){
                e.printStackTrace();
            }
            catch(Exception e) {
                //Toast t = Toast.makeText(getApplicationContext(), "1", Toast.LENGTH_SHORT);
                //t.show();
                e.printStackTrace();
            }
            return null;
        }

        @Override
        /* Process the result of query from here */
        protected void onPostExecute(String s) {
            Loading.this.finish();
        }
    }
}
