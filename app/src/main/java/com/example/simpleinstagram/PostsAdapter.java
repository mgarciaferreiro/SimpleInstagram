package com.example.simpleinstagram;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.simpleinstagram.fragments.ProfileFragment;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.List;

import model.Post;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private Context context;
    private List<Post> posts;

    private TextView tvUser;
    private TextView tvCaption;
    private ImageView imageView;
    private TextView timestamp;
    private ImageView ivProfile;
    private TextView tvLikeCount;
    private Button heartButton;
    private Button commentButton;

    public PostsAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Post post = posts.get(position);
        final ParseUser user = post.getUser();
        ParseFile image = post.getImage();
        RequestOptions requestOptionsMedia = new RequestOptions();
        requestOptionsMedia = requestOptionsMedia.transforms(new CenterCrop());
        if (image != null) {
            Glide.with(context)
                    .load(image.getUrl())
                    .apply(requestOptionsMedia)
                    .into(imageView);
        }
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
        String sourceString = "<b>" + user.getUsername() + "</b> " + post.getDescription();
        tvCaption.setText(Html.fromHtml(sourceString));
        tvUser.setText(post.getUser().getUsername());
        timestamp.setText(post.getTimestamp());
        tvLikeCount.setText(post.getLikeCount().toString());

        if (post.getLikeCount() != null) {
            tvLikeCount.setText(post.getLikeCount().toString() + " likes");
        }

        if (isLikedByUser(post, ParseUser.getCurrentUser())) {
            heartButton.setBackgroundResource(R.drawable.ufi_heart_active);
        } else {
            heartButton.setBackgroundResource(R.drawable.ufi_heart);
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra(Post.class.getSimpleName(), Parcels.wrap(post));
                context.startActivity(intent);
            }
        });

        tvUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("post", post);
                Fragment profileFragment = new ProfileFragment();
                profileFragment.setArguments(bundle);
                ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, profileFragment).commit();
            }
        });

        heartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLikedByUser(post, user)) {
                    post.increment(Post.KEY_LIKE_COUNT, -1);
                    post.getRelation("usersWhoLiked").remove(user);
                    //user.getRelation("likedPosts").remove(post);
                    post.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.d("PostsAdapter", "Unlike post success");
                                notifyItemChanged(position);
                            } else {
                                Log.d("PostsAdapter", "Unlike post failure");
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    post.increment(Post.KEY_LIKE_COUNT, 1);
                    post.getRelation("usersWhoLiked").add(user);
                    //user.getRelation("likedPosts").add(post);
                    post.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.d("PostsAdapter", "Like post success");
                                //notifyItemChanged(holder.getLayoutPosition());
                                notifyDataSetChanged();
                            } else {
                                Log.d("PostsAdapter", "Like post failure");
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
            tvUser = itemView.findViewById(R.id.tvUser);
            tvCaption = itemView.findViewById(R.id.tvCaption);
            imageView = itemView.findViewById(R.id.imageView);
            timestamp = itemView.findViewById(R.id.timestamp);
            ivProfile = itemView.findViewById(R.id.ivProfile);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
            heartButton = itemView.findViewById(R.id.heartButton);
            commentButton = itemView.findViewById(R.id.commentButton);
        }
    }

    private boolean isLikedByUser(Post post, ParseUser user) {
        ParseRelation relation = post.getRelation("usersWhoLiked");
        try {
            Log.i("PostsAdapter", post.getDescription() + "is liked by user" + user.getUsername() + relation.getQuery().find().contains(user));
            return relation.getQuery().find().contains(user);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Clean all elements of the recycler
    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Post> list) {
        posts.addAll(list);
        notifyDataSetChanged();
    }
}
