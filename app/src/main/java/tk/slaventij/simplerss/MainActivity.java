package tk.slaventij.simplerss;

import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText editText;
    private Button fetchFeedButton;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView feedTitleTextView;
    private TextView feedLinkTextView;
    private TextView feedDescriptionTextView;

    private List<RSSFeedModel> feedModelList;
    private String feedTitle;
    private String feedLink;
    private String feedDescription;

    public static final String TAG = "TAG";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        editText = findViewById(R.id.rssFeedEditText);
        fetchFeedButton = findViewById(R.id.fetchFeedButton);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        feedTitleTextView = findViewById(R.id.feedTitle);
        feedDescriptionTextView = findViewById(R.id.feedDescription);
        feedLinkTextView = findViewById(R.id.feedLink);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchFeedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetch();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetch();
            }
        });

    }

    private void fetch() {
        new FetchFeedTask().execute((Void) null);
    }

    private class FetchFeedTask extends AsyncTask<Void, Void, Boolean> {

        private String urlLink;

    /*public void execute(Void aVoid) {


    }*/

        @Override
        protected void onPreExecute() {
            swipeRefreshLayout.setRefreshing(true);
            urlLink = editText.getText().toString();
        }

        @Override
        protected void onPostExecute(Boolean success) {
            swipeRefreshLayout.setRefreshing(false);

            if (success) {
                feedTitleTextView.setText("Feed Title: " + feedTitle);
                feedDescriptionTextView.setText("Feed Description: " + feedDescription);
                feedLinkTextView.setText("Feed Link: " + feedLink);
                // fill recyclerview
                recyclerView.setAdapter(new RSSFeedListAdapter(feedModelList));
            } else {
                Toast.makeText(MainActivity.this, "Enter a valid RSS URL!",
                        Toast.LENGTH_LONG).show();
            }
            super.onPostExecute(success);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (TextUtils.isEmpty(urlLink)) {
                return false;
            }
            try {
                if (!urlLink.startsWith("http://") && !urlLink.startsWith("https://")) {
                    urlLink = "http://" + urlLink;
                }

                URL url = new URL(urlLink);
                InputStream inputStream = url.openConnection().getInputStream();
                feedModelList = parseFeed(inputStream);
                return true;
            } catch (IOException | XmlPullParserException e) {
                Log.e(TAG, "Error", e);
            }
            return false;
        }

        public List<RSSFeedModel> parseFeed(InputStream inputStream) throws XmlPullParserException,
                IOException {

            String title = null;
            String link = null;
            String description = null;
            boolean isItem = false;
            List<RSSFeedModel> items = new ArrayList<>();

            try {
                XmlPullParser xmlPullParser = Xml.newPullParser();
                xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                xmlPullParser.setInput(inputStream, null);

                xmlPullParser.nextTag();
                while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {
                    int eventType = xmlPullParser.getEventType();

                    String name = xmlPullParser.getName();
                    if (name == null) {
                        continue;
                    }

                    if (eventType == XmlPullParser.END_TAG) {
                        if (name.equalsIgnoreCase("item")) {
                            isItem = false;
                        }
                        continue;
                    }

                    if (eventType == XmlPullParser.START_TAG) {
                        if (name.equalsIgnoreCase("item")) {
                            isItem = true;
                            continue;
                        }
                    }

                    Log.d("MyXmlParser", "Parsing name ==> " + name);
                    String result = "";
                    if (xmlPullParser.next() == XmlPullParser.TEXT) {
                        result = xmlPullParser.getText();
                        xmlPullParser.nextTag();
                    }

                    if (name.equalsIgnoreCase("title")) {
                        title = result;
                    } else if (name.equalsIgnoreCase("link")) {
                        link = result;
                    } else if (name.equalsIgnoreCase("description")) {
                        description = result;
                    }

                    if (title != null && link != null && description != null) {
                        if (isItem) {
                            RSSFeedModel item = new RSSFeedModel(title, link, description);
                            items.add(item);
                        } else {
                            feedTitle = title;
                            feedLink = link;
                            feedDescription = description;
                        }

                        title = null;
                        link = null;
                        description = null;
                        isItem = false;
                    }
                }

                return items;

            } finally {
                inputStream.close();
            }
        }
    }
}
