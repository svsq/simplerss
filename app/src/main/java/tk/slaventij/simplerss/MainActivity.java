package tk.slaventij.simplerss;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
}
