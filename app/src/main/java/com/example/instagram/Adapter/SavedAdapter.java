package com.example.instagram.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.Model.Saved;
import com.example.instagram.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SavedAdapter extends RecyclerView.Adapter<SavedAdapter.ViewHolder> {

    private final Context mContext;
    private final List<Saved> mSaved;

    public SavedAdapter(Context mContext, List<Saved> mSaved) {
        this.mContext = mContext;
        this.mSaved = mSaved;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.gallery_view, parent, false);
        return new SavedAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Saved saved = mSaved.get(position);
        Picasso.get().load(saved.getImageUrl()).into(holder.galleryUnit);
    }

    @Override
    public int getItemCount() {
        return mSaved.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView galleryUnit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            galleryUnit = itemView.findViewById(R.id.photo_gallery);

        }
    }
}
