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
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.List;

import model.Like;
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

        commentButton.setOnClickListener(new View.OnClickListener() {
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
        Log.i("PostsAdapter", post.getDescription() + " " + post.likes + " likes");
        Log.i("PostsAdapter", post.getDescription() + " liked by user " + post.likedByUser);
        tvLikeCount.setText(post.likes + " likes");

        if (post.likedByUser) {
            heartButton.setBackgroundResource(R.drawable.ufi_heart_active);
        } else {
            heartButton.setBackgroundResource(R.drawable.ufi_heart_icon);
        }

        queryLikes(post);

        heartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (post.likedByUser) {
                    setUnliked(post);
                } else {
                    setLiked(post);
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

    private void setLiked(final Post post) {
        post.setLikedByUser(true);
        post.setLikeCount(post.likes++);
        final Like like = new Like();
        like.setPost(post);
        like.setUser(ParseUser.getCurrentUser());
        like.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.i("PostsAdapter", post.getDescription() + " liked");
                    queryLikes(post);
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setUnliked(final Post post) {
        post.setLikedByUser(false);
        post.setLikeCount(post.likes--);
        notifyDataSetChanged();
        final Like.Query likeQuery = new Like.Query();
        likeQuery.forPost(post).forUser(ParseUser.getCurrentUser());
        likeQuery.findInBackground(new FindCallback<Like>() {
            @Override
            public void done(List<Like> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        objects.get(0).deleteInBackground(new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Log.i("PostsAdapter", post.getDescription() + " unliked");
                                    queryLikes(post);
                                } else {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    public void queryLikes(final Post post) {
        final Like.Query likeQuery = new Like.Query();
        likeQuery.forPost(post);
        likeQuery.findInBackground(new FindCallback<Like>() {
            @Override
            public void done(List<Like> objects, ParseException e) {
                if (e == null) {
                    post.setLikeCount(objects.size());
                    for (int i = 0; i < objects.size(); i++) {
                        if (objects.get(i).getUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                            post.setLikedByUser(true);
                        }
                    }
                    //notifyDataSetChanged();
                } else {
                    e.printStackTrace();
                }
            }
        });
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
