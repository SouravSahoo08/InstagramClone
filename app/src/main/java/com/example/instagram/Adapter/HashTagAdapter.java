package com.example.instagram.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.R;

import java.util.List;

public class HashTagAdapter extends RecyclerView.Adapter<HashTagAdapter.ViewHolder> {

    private final Context mContext;
    private List<String> mHashtags;
    private List<String> mHashtagsCount;

    public HashTagAdapter(Context mContext, List<String> mHashtags, List<String> mHashtagsCount) {
        this.mContext = mContext;
        this.mHashtags = mHashtags;
        this.mHashtagsCount = mHashtagsCount;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.hashtags_list, parent, false);
        return new HashTagAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.hashTagtxt.setText(String.format("#%s", mHashtags.get(position)));
        holder.no_of_posts.setText(String.format("%s posts", mHashtagsCount.get(position)));
    }

    @Override
    public int getItemCount() {
        return mHashtags.size();
    }

    public void filter(List<String> filterTags, List<String> filterTagsCount) {
        this.mHashtags = filterTags;
        this.mHashtagsCount = filterTagsCount;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView hashTagtxt;
        public TextView no_of_posts;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            hashTagtxt = itemView.findViewById(R.id.hashTagtxt);
            no_of_posts = itemView.findViewById(R.id.no_of_posts);
        }

    }
}
