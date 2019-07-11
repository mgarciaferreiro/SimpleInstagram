package com.example.simpleinstagram;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import model.InstaComment;
import model.Post;

public class DetailActivity extends AppCompatActivity {

    private TextView tvUser;
    private TextView tvCaption;
    private ImageView imageView;
    private TextView timestamp;
    private ImageView ivProfile;
    private EditText etComment;
    private Button commentButton;

    RecyclerView rvComments;
    private CommentsAdapter adapter;
    private List<InstaComment> comments;
    private Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        rvComments = findViewById(R.id.rvComments);
        comments = new ArrayList<>();
        adapter = new CommentsAdapter(this, comments);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rvComments.addItemDecoration(itemDecoration);
        rvComments.setItemAnimator(new SlideInUpAnimator());
        rvComments.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvComments.setLayoutManager(linearLayoutManager);

        tvUser = findViewById(R.id.tvUser);
        tvCaption = findViewById(R.id.tvCaption);
        imageView = findViewById(R.id.imageView);
        timestamp = findViewById(R.id.timestamp);
        ivProfile = findViewById(R.id.ivProfile);
        etComment = findViewById(R.id.etComment);
        commentButton = findViewById(R.id.postButton);

        post = Parcels.unwrap(getIntent().getParcelableExtra(Post.class.getSimpleName()));
        final ParseUser user = post.getUser();
        ParseFile image = post.getImage();
        RequestOptions requestOptionsMedia = new RequestOptions();
        requestOptionsMedia = requestOptionsMedia.transforms(new CenterCrop());
        if (image != null) {
            Glide.with(this)
                    .load(image.getUrl())
                    .apply(requestOptionsMedia)
                    .into(imageView);
        }
        try {
            ParseFile profilePicFile = user.fetchIfNeeded().getParseFile("profilePicture");
            if (profilePicFile != null) {
                RequestOptions requestOptionsProfile = new RequestOptions();
                requestOptionsProfile = requestOptionsProfile.transforms(new CenterCrop(), new RoundedCorners(100));
                Glide.with(this)
                        .load(profilePicFile.getUrl())
                        .apply(requestOptionsProfile)
                        .into(ivProfile);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String sourceString = "<b>" + user.getUsername() + "</b> " + post.getDescription();
        tvCaption.setText(Html.fromHtml(sourceString));
        tvUser.setText(user.getUsername());
        timestamp.setText(post.getTimestamp());

        queryComments();

        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = etComment.getText().toString();
                InstaComment comment = new InstaComment();
                comment.setText(text);
                comment.setUser(user);
                comment.setPost(post);
                comment.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null) {
                            Log.i("DetailActivity", "Created comment");
                            queryComments();
                        } else {
                            Log.e("DetailActivity", "Failed to save comment", e);
                        }
                    }
                });
                etComment.setText(null);
            }
        });
    }

    public void queryComments() {
        final InstaComment.Query query = new InstaComment.Query();
        query.forPost(post);
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<InstaComment>() {
            @Override
            public void done(List<InstaComment> objects, ParseException e) {
                if (e == null) {
                    adapter.addAll(objects);
                    adapter.notifyDataSetChanged();
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}
