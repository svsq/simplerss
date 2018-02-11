package tk.slaventij.simplerss;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by svsq on 10.02.2018.
 */

class RSSFeedListAdapter extends RecyclerView.Adapter<RSSFeedListAdapter.FeedModelViewHolder> {

    private List<RSSFeedModel> rssFeedModels;

    public static class FeedModelViewHolder extends RecyclerView.ViewHolder {

        private View rssFeedView;

        public FeedModelViewHolder(View v) {
            super(v);
            rssFeedView = v;
        }
    }

    public RSSFeedListAdapter(List<RSSFeedModel> rssFeedModels) {
        this.rssFeedModels = rssFeedModels;

    }

    @Override
    public FeedModelViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rss_feed, parent, false);
        FeedModelViewHolder holder = new FeedModelViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(FeedModelViewHolder holder, int position) {
        final RSSFeedModel rssFeedModel = rssFeedModels.get(position);
        ((TextView) holder.rssFeedView.findViewById(R.id.titleText)).setText(rssFeedModel.title);
        ((TextView) holder.rssFeedView.findViewById(R.id.descriptionText))
                .setText(rssFeedModel.description);
        ((TextView) holder.rssFeedView.findViewById(R.id.linkText)).setText(rssFeedModel.link);

    }

    @Override
    public int getItemCount() {
        return rssFeedModels.size();
    }
}
