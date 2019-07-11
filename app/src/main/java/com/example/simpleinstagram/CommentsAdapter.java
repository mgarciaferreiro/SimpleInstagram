package com.example.simpleinstagram;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;

import model.InstaComment;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    private Context context;
    private List<InstaComment> comments;

    private TextView tvText;
    private TextView timestamp;
    private ImageView ivProfile;

    public CommentsAdapter(Context context, List<InstaComment> comments) {
        this.context = context;
        this.comments = comments;
    }

    @NonNull
    @Override
    public CommentsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new CommentsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        InstaComment comment = comments.get(position);
        final ParseUser user = comment.getUser();
        try {
            ParseFile profilePicFile = user.fetchIfNeeded().getParseFile("profilePicture");
            if (profilePicFile != null) {
                RequestOptions requestOptionsProfile = new RequestOptions();
                requestOptionsProfile = requestOptionsProfile.transforms(new CenterCrop(), new RoundedCorners(100));
                Glide.with(context)
                        .load(profilePicFile.getUrl())
                        .apply(requestOptionsProfile)
                        .into(ivProfile);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String sourceString = "<b>" + comment.getUser().getUsername() + "</b> " + comment.getText();
        tvText.setText(Html.fromHtml(sourceString));
        timestamp.setText(comment.getTimestamp());
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
            tvText = itemView.findViewById(R.id.tvComment);
            timestamp = itemView.findViewById(R.id.tvCreatedAt);
            ivProfile = itemView.findViewById(R.id.ivProfile);
        }
    }

    // Clean all elements of the recycler
    public void clear() {
        comments.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<InstaComment> list) {
        comments.addAll(list);
        notifyDataSetChanged();
    }
}
