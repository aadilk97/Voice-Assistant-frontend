package com.example.aadil.testmac;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import android.speech.tts.TextToSpeech;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Objects;

import bsh.Interpreter;


public class MainActivity extends Activity {

    public TextView txt;
    public TextToSpeech t1;
    private Button btn;
    private EditText etxt;
    private CheckBox cbox;

    private final int REQ_CODE_SPEECH_INPUT = 100;


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    private void setAlarm(int hours, int mins, boolean pmflag){
        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(AlarmClock.EXTRA_HOUR, hours);
        intent.putExtra(AlarmClock.EXTRA_MINUTES, mins);
        intent.putExtra(AlarmClock.EXTRA_IS_PM, true);
        getApplicationContext().startActivity(intent);
    }

    private void speechInput(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak something");

        try{
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        }catch(ActivityNotFoundException e){
            Toast t = Toast.makeText(getApplicationContext(), "Not supported", Toast.LENGTH_SHORT);
            t.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQ_CODE_SPEECH_INPUT){
            if(resultCode == RESULT_OK && null != data){
                ArrayList<String> result = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                Toast t = Toast.makeText(getApplicationContext(), result.get(0), Toast.LENGTH_LONG);
                t.show();

                new MyAsyncTask().execute(result.get(0));

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent load = new Intent(this, Loading.class);
        this.startActivity(load);

        txt = (TextView) findViewById(R.id.textView1);
        btn = (Button) findViewById(R.id.button);
        etxt = (EditText) findViewById(R.id.editText);
        cbox = (CheckBox) findViewById(R.id.checkBox);

        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener(){
            @Override
            public void onInit(int i) {
                if(i != TextToSpeech.ERROR){
                    t1.setLanguage(Locale.UK);
                }
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speechInput();
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    public void OnCLick(View view) {
        //new MyAsyncTask().execute();
        //txt.setText("My first android app built from a mac!");

        Interpreter interpreter = new Interpreter();
        try {
            Integer ans = (Integer) interpreter.eval(etxt.getText().toString());
            etxt.setText(ans.toString());
        } catch(Exception e){
            e.printStackTrace();
        }


    }

    private class MyAsyncTask extends AsyncTask<String, Void, String>{
        String json;
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            String url;
            if(cbox.isChecked())
                url = "http://192.168.1.9:8000/";

            else
                url = "https://nameless-escarpment-30648.herokuapp.com/";
            String s2 = params[0];
            s2 = s2.replaceAll(" ", "%20");

            url += s2;

            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse response;
            // Creating HTTP Post
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("Accept", "application/json");

            try {
                response = httpClient.execute(httpGet);
                HttpEntity entity = response.getEntity();
               // json = EntityUtils.toString(entity);


                InputStream inputStream = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null){
                    sb.append(line + "\n");
                }
                String result = sb.toString();

                JSONObject jsonObject = new JSONObject(result);
                String data = jsonObject.getString("data");
                int code = jsonObject.getInt("code");

                if(data.equals("ALARM")){
                    int hours = jsonObject.getInt("hours");
                    int mins = jsonObject.getInt("mins");
                    boolean flag = jsonObject.getBoolean("flag");
                    System.out.print("Hours = " + hours);
                    data = "Ok setting the alarm";
                    setAlarm(hours, mins, flag);
                }
                switch(code){
                    case 101:
                        //data = "Here is what i found";
                        String burl = "https://www.google.com/search?q=" + s2;
                        Intent bintent = new Intent(Intent.ACTION_VIEW, Uri.parse(burl));
                        bintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().startActivity(bintent);
                        break;

                    case 102:
                        PackageManager pm = getPackageManager();
                        List packages = pm.getInstalledPackages(0);
                        ListIterator<PackageInfo> i = packages.listIterator();

                        String input = data;
                        input = input.replaceAll("\\s", "");
                        System.out.println(input);

                        while(i.hasNext()){
                            //System.out.println(i.next().packageName.toString());
                            String x = i.next().packageName;
                            ApplicationInfo ai = null;
                            try {
                                ai = pm.getApplicationInfo(x, 0);
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                            String appname = pm.getApplicationLabel(ai).toString();
                            appname = appname.replaceAll("\\s", "");
                            if(appname.toLowerCase().equals(input.toLowerCase())){
                                Intent launch = getPackageManager().getLaunchIntentForPackage(x);
                                startActivity(launch);
                            }
                        }
                        break;
                    case 103:
                        String location = "geo:0,0?q=" + data;
                        String uri = String.format(Locale.ENGLISH, location);
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        startActivity(intent);
                        break;
                    case 104:
                        int ct = 0;
                        String titles[] = new String[16];
                        String links[] = new String[16];
                        for(int it=0; it < jsonObject.names().length(); it++) {
                            String title = "", link = "";
                            try {
                                title = jsonObject.names().getString(it);
                                link = jsonObject.get(title).toString();
                            }catch(Exception e) {
                                e.printStackTrace();
                            }
                            if (!title.equals("data") && !title.equals("code")) {
                                titles[ct] = title;
                                links[ct++] = link;
                            }
                            System.out.println(titles[0] + " " + links[0]);
                        }
                        Intent news_intent = new Intent(getApplicationContext(), News.class);
                        news_intent.putExtra("TITLES", titles);
                        news_intent.putExtra("LINKS", links);
                        startActivity(news_intent);
                        break;
                    case 105:
                        Interpreter interpreter = new Interpreter();
                        try {
                            Integer ans = (Integer) interpreter.eval(data);
                            data = "Answer is "+ ans.toString();
                            System.out.println(data);
                        } catch(Exception e){
                            e.printStackTrace();
                        }
                        break;
                }

                return data;
                //return json;

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
            //System.out.println(s);
            txt.setText(s);
            t1.speak(s, TextToSpeech.QUEUE_FLUSH, null);
            super.onPostExecute(s);

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.aadil.testmac/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.aadil.testmac/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
