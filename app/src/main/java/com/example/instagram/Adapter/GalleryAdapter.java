package com.example.instagram.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.Model.Posts;
import com.example.instagram.R;
import com.example.instagram.fragments.PostDetailFragment;
import com.squareup.picasso.Picasso;

import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private final Context mContext;
    private final List<Posts> mPosts;

    public GalleryAdapter(Context mContext, List<Posts> mPosts) {
        this.mContext = mContext;
        this.mPosts = mPosts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.gallery_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Posts posts = mPosts.get(position);
        Picasso.get().load(posts.getImageUrl()).into(holder.galleryUnit);

        holder.galleryUnit.setOnClickListener(v -> {
            mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
                    .putString("postId", posts.getPostId()).apply();
            ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new PostDetailFragment()).commit();
        });
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView galleryUnit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            galleryUnit = itemView.findViewById(R.id.photo_gallery);

        }
    }
}
