package com.example.simpleinstagram;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;

import org.parceler.Parcels;

import model.Post;

public class DetailActivity extends AppCompatActivity {

    private TextView tvUser;
    private TextView tvCaption;
    private ImageView imageView;
    private TextView timestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tvUser = findViewById(R.id.tvUser);
        tvCaption = findViewById(R.id.tvCaption);
        imageView = findViewById(R.id.imageView);
        timestamp = findViewById(R.id.timestamp);

        Post post = (Post) Parcels.unwrap(getIntent().getParcelableExtra(Post.class.getSimpleName()));
        ParseFile image = post.getImage();
        if (image != null) {
            Glide.with(this)
                    .load(image.getUrl())
                    .into(imageView);
        }
        tvCaption.setText(post.getDescription());
        tvUser.setText(post.getUser().getUsername());
        timestamp.setText(post.getTimestamp());
    }
}
