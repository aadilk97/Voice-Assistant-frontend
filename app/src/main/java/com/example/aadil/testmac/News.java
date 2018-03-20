package com.example.aadil.testmac;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class News extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        Bundle extras = getIntent().getExtras();

        String[] titles = extras.getStringArray("TITLES");
        final String[] links = extras.getStringArray("LINKS");

        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.news_listview_text, titles);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String burl = links[i];
                Intent bintent = new Intent(Intent.ACTION_VIEW, Uri.parse(burl));
                bintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(bintent);
            }
        });
    }

}
