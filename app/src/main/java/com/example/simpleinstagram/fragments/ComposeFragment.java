package com.example.simpleinstagram.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import model.Post;

import static android.app.Activity.RESULT_OK;

public class ComposeFragment extends Fragment {
    private EditText descriptionInput;
    private Button createButton;
    private Button takePictureButton;
    private Button uploadButton;
    private ImageView imageView;
    BottomNavigationView bottomNavigationView;
    ProgressBar pb;

    private final String TAG = "ComposeFragment";
    public final static int PICK_PHOTO_CODE = 1046;

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
        uploadButton = view.findViewById(R.id.uploadButton);
        imageView = view.findViewById(R.id.imageView);
        bottomNavigationView = view.findViewById(R.id.bottom_navigation);
        pb = view.findViewById(R.id.pbLoading);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String description = descriptionInput.getText().toString();
                final ParseUser user = ParseUser.getCurrentUser();
                if (imageView.getDrawable() == null) {
                    Log.e(TAG, "No photo to post");
                    return;
                }
                Bitmap imageBitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                pb.setVisibility(ProgressBar.VISIBLE);
                createPost(description, getParseFile(imageBitmap), user);
            }
        });
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera();
            }
        });
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPickPhoto();
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
        } else if (requestCode == PICK_PHOTO_CODE && data != null) {
            Uri photoUri = data.getData();
            Bitmap selectedImage = null;
            try {
                selectedImage = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), photoUri);
                photoFile = new File(photoUri.getPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageView.setImageBitmap(selectedImage);
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

    // Trigger gallery selection for a photo
    public void onPickPhoto() {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, PICK_PHOTO_CODE);
        }
    }

    private void createPost(String description, ParseFile imageFile, ParseUser user) {
        final Post post = new Post();
        post.setDescription(description);
        post.setImage(imageFile);
        post.setUser(user);
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

    public static ParseFile getParseFile(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
        byte[] image = stream.toByteArray();

        return new ParseFile(image);
    }

}
