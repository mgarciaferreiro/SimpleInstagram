package com.example.simpleinstagram;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.parse.ParseFile;

import org.parceler.Parcels;

import java.util.List;

import model.Post;

public class PostsGridAdapter extends RecyclerView.Adapter<PostsGridAdapter.ViewHolder> {

    private Context context;
    private List<Post> posts;
    private ImageView imageView;

    public PostsGridAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public PostsGridAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_item_post, parent, false);
        return new PostsGridAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostsGridAdapter.ViewHolder holder, int position) {
        Post post = posts.get(position);
        ParseFile image = post.getImage();
        RequestOptions requestOptionsMedia = new RequestOptions();
        requestOptionsMedia = requestOptionsMedia.transforms(new CenterCrop());
        if (image != null) {
            Glide.with(context)
                    .load(image.getUrl())
                    .apply(requestOptionsMedia)
                    .into(imageView);
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Post post = posts.get(position);
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra(Post.class.getSimpleName(), Parcels.wrap(post));
                context.startActivity(intent);
            }
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
