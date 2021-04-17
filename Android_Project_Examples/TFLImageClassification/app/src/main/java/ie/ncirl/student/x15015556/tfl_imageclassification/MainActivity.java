package ie.ncirl.student.x15015556.tfl_imageclassification;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1000;
    private static final int CAMERA_REQUEST_CODE = 1001;

    private static final int READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 2000;
    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 2001;

    /**
     * UI Elements
     */
    private ImageView imageView;
    private ListView listView;
    private ImageClassifier imageClassifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeUIElements();
    }

    private void initializeUIElements() {
        imageView = findViewById(R.id.iv_capture);
        listView = findViewById(R.id.lv_probabilities);
        Button takePicture = findViewById(R.id.bt_take_picture);
        Button storageGallery = findViewById(R.id.bt_open_gallery);

        /*
         * Creating an instance of our tensor image classifier
         */
        try {
            imageClassifier = new ImageClassifier(this);
        } catch (IOException e) {
            Log.e("Image Classifier Error", "ERROR: " + e);
        }

        // adding on click listener to button
        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking whether camera permissions are available.
                // if permission is available then we open camera intent to get picture
                // otherwise requests for permissions
                if (hasCameraPermission()) {
                    openCamera();
                } else {
                    requestCameraPermission();
                }
            }
        });

        storageGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hasStoragePermission()) {
                    openGallery();
                } else {
                    requestStoragePermission();
                }
            }
        });
    }

    /**
     * checks whether all the needed permissions have been granted or not
     *
     * @param grantResults the permission grant results
     * @return true if all the reqested permission has been granted,
     * otherwise returns false
     */
    private boolean hasAllPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result == PackageManager.PERMISSION_DENIED)
                return false;
        }
        return true;
    }

    /**
     * Method requests for permission if the android version is marshmallow or above
     */
    private void requestCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // whether permission can be requested or on not
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                Toast.makeText(this, "Camera Permission Required", Toast.LENGTH_LONG).show();
            }
            else if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {

            }
            // request the camera permission permission
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * Method requests for permission if the android version is marshmallow or above
     */
    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // whether permission can be requested or on not
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "Storage Permission Required", Toast.LENGTH_LONG).show();
            }
            else if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {

            }
            // request the camera permission permission
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CAMERA_PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * checks whether camera permission is available or not
     *
     * @return true if android version is less than marshmallo,
     * otherwise returns whether camera permission has been granted or not
     */
    private boolean hasCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    private boolean hasStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    @Override
    //onPermission*S*Result
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // if this is the result of our camera permission request
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (hasAllPermissions(grantResults)) {
                openCamera();
            }
            else {
                requestCameraPermission();
            }
        }
        if (requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (hasAllPermissions(grantResults)) {
                openGallery();
            }
            else {
                requestCameraPermission();
            }
        }
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
    }

    private void openGallery() {
        Intent storageIntent = new Intent(Intent.ACTION_GET_CONTENT);
        storageIntent.setType("image/*");

        Intent chooser = Intent.createChooser(storageIntent, "Choose an image");
        startActivityForResult(chooser, READ_EXTERNAL_STORAGE_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        Bitmap image = null;

        // if this is the result of our camera image request
        if (requestCode == CAMERA_REQUEST_CODE) {
            image = (Bitmap) Objects.requireNonNull(Objects.requireNonNull(data).getExtras()).get("data");
            // displaying this bitmap in imageview
            imageView.setImageBitmap(image);

            // pass this bitmap to classifier to make prediction
            List<ImageClassifier.Recognition> predictions = imageClassifier.recogniseImage(image, 0);

            // creating a list of string to display in list view
            final List<String> predictionsList = new ArrayList<>();

            for (ImageClassifier.Recognition recog : predictions) {
                //Format item name
                String recogName = recog.getName();
                String recogNameFormatted = recogName.substring(0, 1).toUpperCase() + recogName.substring(1); // Changes first letter to uppercase

                //format number
                float recogConfidence = recog.getConfidence() * 100; // to change it from 0.55123456 to 55.123456
                double recogFormatted = Math.round(recogConfidence * 100.0) / 100.0; // to change it from 55.123456 to 55.12

                String recogResult;
                if (recogConfidence <= 0.01){
                    recogResult = "<0.01";
                } else {
                    recogResult = String.valueOf(recogFormatted);
                }

                predictionsList.add(recogNameFormatted + ": " + recogResult + "%");
            }


            // creating an array adapter to display the classification result in list view
            ArrayAdapter<String> predictionsAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, predictionsList);
            listView.setAdapter((predictionsAdapter));
        }
        else if (requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE) {
            final Uri imageUri = data.getData();
            final InputStream imageStream;
            try {
                imageStream = getContentResolver().openInputStream(imageUri);
                image = BitmapFactory.decodeStream(imageStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            imageView.setImageBitmap(image);

            // pass this bitmap to classifier to make prediction
            List<ImageClassifier.Recognition> predictions = imageClassifier.recogniseImage(image, 0);

            // creating a list of string to display in list view
            final List<String> predictionsList = new ArrayList<>();

            for (ImageClassifier.Recognition recog : predictions) {
                //Format item name
                String recogName = recog.getName();
                String recogNameFormatted = recogName.substring(0, 1).toUpperCase() + recogName.substring(1); // Changes first letter to uppercase

                float recogConfidence = recog.getConfidence() * 100; // to change it from 0.55123456 to 55.123456
                double recogFormatted = Math.round(recogConfidence * 100.0) / 100.0; // to change it from 55.123456 to 55.12

                String recogResult;
                if (recogConfidence <= 0.01){
                    recogResult = "<0.01";
                } else {
                    recogResult = String.valueOf(recogFormatted);
                }

                predictionsList.add(recogNameFormatted + ": " + recogResult + "%");
            }


            // creating an array adapter to display the classification result in list view
            ArrayAdapter<String> predictionsAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, predictionsList);
            listView.setAdapter((predictionsAdapter));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void getResult(Bitmap image){
        imageView.setImageBitmap(image);

        // pass this bitmap to classifier to make prediction
        List<ImageClassifier.Recognition> predictions = imageClassifier.recogniseImage(image, 0);

        // creating a list of string to display in list view
        final List<String> predictionsList = new ArrayList<>();

        for (ImageClassifier.Recognition recog : predictions) {
            //Format item name
            String recogName = recog.getName();
            String recogNameFormatted = recogName.substring(0, 1).toUpperCase() + recogName.substring(1); // Changes first letter to uppercase

            //fomat number
            float recogConfidence = recog.getConfidence() * 100; // to change it from 0.55123456 to 55.123456
            double recogFormatted = Math.round(recogConfidence * 100.0) / 100.0; // to change it from 55.123456 to 55.12

            predictionsList.add(recogNameFormatted + ": " + recogFormatted + "%");
    }
        }
}