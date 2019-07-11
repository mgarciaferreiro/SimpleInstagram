package com.example.simpleinstagram.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.simpleinstagram.CameraUtil;
import com.example.simpleinstagram.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;

import model.Post;

import static android.app.Activity.RESULT_OK;

public class ComposeFragment extends Fragment {
    private EditText descriptionInput;
    private Button createButton;
    private Button takePictureButton;
    private ImageView imageView;
    BottomNavigationView bottomNavigationView;
    ProgressBar pb;

    private final String TAG = "ComposeFragment";

    public String photoFileName = "photo.jpg";
    private File photoFile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_compose, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        descriptionInput = view.findViewById(R.id.descriptionInput);
        createButton = view.findViewById(R.id.createButton);
        takePictureButton = view.findViewById(R.id.takePictureButton);
        imageView = view.findViewById(R.id.imageView);
        bottomNavigationView = view.findViewById(R.id.bottom_navigation);
        pb = view.findViewById(R.id.pbLoading);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String description = descriptionInput.getText().toString();
                final ParseUser user = ParseUser.getCurrentUser();
                if (photoFile == null || imageView.getDrawable() == null) {
                    Log.e(TAG, "No photo to post");
                    return;
                }
                pb.setVisibility(ProgressBar.VISIBLE);
                createPost(description, photoFile, user);
            }
        });
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CameraUtil.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                imageView.setImageBitmap(takenImage);
            } else {
                Log.e(TAG, "Picture wasn't taken");
            }
        }
    }

    public void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference to access to future access
        photoFile = CameraUtil.getPhotoFileUri(getContext(), photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CameraUtil.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    private void createPost(String description, File imageFile, ParseUser user) {
        Post post = new Post();
        post.setDescription(description);
        post.setImage(new ParseFile(imageFile));
        post.setUser(user);
        post.setLikeCount(0);
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("MainActivity", "Create post success");
                    descriptionInput.setText("");
                    imageView.setImageResource(0);
                    pb.setVisibility(ProgressBar.INVISIBLE);
                } else {
                    Log.d("MainActivity", "Create post failure");
                    e.printStackTrace();
                }
            }
        });
    }

}
