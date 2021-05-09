package ncirl.x15015556.detect_diabetic_reinopathy;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class ImageActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    public static final int PICTURE_RESULT = 1;
    public static final int GALLERY_RESULT = 2;
    public static final int REQUEST_CAMERA = 1000;
    public static final int REQUEST_GALLERY = 1001;
    public static final int REQUEST_GALLERY_FROM_CAMERA = 1050;

    ContentValues values;
    Uri uriImage; // UR*I*
    Bitmap image;
//    String imageUrl; //UR*L*



    Button btnSignOut, btnRevokeAccess, btnCameraPhoto, btnGallerySelect, btnResults;
    String patientID, googleID, googleName;
    TextView tvPatientID, tvGoogleID, tvGoogleName, tvSelectedImage;
    Intent mainActivity, patientActivity, resultsActivity;
    ImageView ivImage;
    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title in Android R/10+
        setContentView(R.layout.activity_image);

        //App runs full screen, no header or status bar.
        // Version R is Android 10+ - if/else for compatibility
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            // Removes the header/title bar in app
            // Android R is version 10 - if/else for versions older versions
            final WindowInsetsController insetsController = getWindow().getInsetsController();
            if (insetsController != null) {
                insetsController.hide(WindowInsets.Type.statusBars());
            }
        } else {
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
            );
        }

        // Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestId()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mainActivity = new Intent(ImageActivity.this, SignInActivity.class);
        patientActivity = new Intent(ImageActivity.this, PatientActivity.class);
        resultsActivity = new Intent(ImageActivity.this, ResultsActivity.class);

        btnSignOut = findViewById(R.id.btnSignOut);
        btnRevokeAccess = findViewById(R.id.btnRevokeAccess);

        //Getting image
        ivImage = findViewById(R.id.ivImage);
        tvSelectedImage = findViewById(R.id.tvSelectedImage);
        btnResults = findViewById(R.id.btnResults);
        btnCameraPhoto = findViewById(R.id.btnCameraPhoto);
        btnGallerySelect = findViewById(R.id.btnGalerySelect);


        // Buttons
        btnSignOut = findViewById(R.id.btnSignOut);
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
                startActivity(mainActivity);
            }
        });

        btnRevokeAccess = findViewById(R.id.btnRevokeAccess);
        btnRevokeAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                revokeAccess();
                startActivity(mainActivity);
            }
        });

        //Declaring patientID textview, and getting relevant values from storage, if present.
        tvPatientID = findViewById(R.id.tvPatientID);
        patientID = ReadWriteToFile.readFromFilePatientID(this);
        // if no PatientID stored, go to PatientActivity, else populate patientID
        if ((patientID == null) || (patientID.equals(""))) {
            startActivity(patientActivity);

        } else {
            tvPatientID.setText(patientID);
        }

        //Declaring GoogleID and Name textview, and getting relevant values from storage, if present.
        tvGoogleID = findViewById(R.id.tvGoogleID);
        tvGoogleName = findViewById(R.id.tvGoogleName);
        googleName = ReadWriteToFile.readFromFileGoogleName(this);
        googleID = ReadWriteToFile.readFromFileGoogleID(this);
        //if no Google Name or ID, go back to SignInActivity to sign in, else populate with GoogleID and GoogleName
        if ((googleID == null) || (googleID.equals("")) || (googleName == null) || (googleName.equals(""))) {
            startActivity(mainActivity);

        } else {
            tvGoogleName.setText(googleName);
            tvGoogleID.setText(googleID);
        }
        
        buttons();
    }

    private void buttons() {
        btnResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(resultsActivity);
            }
        });

        btnCameraPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(ImageActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ImageActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);

                } else {
                    launchCamera();
                }
            }
        });

        btnGallerySelect.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    String permissions = Manifest.permission.READ_EXTERNAL_STORAGE;
                    requestPermissions(new String[]{permissions}, REQUEST_GALLERY);

                } else {
                   pickImageFromGallery();
                }
            }
        });
    }

    private void pickImageFromGallery(){
        Intent pickImage = new Intent(Intent.ACTION_PICK);
        pickImage.setType("image/*");
        startActivityForResult(pickImage, GALLERY_RESULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("GotImage", "resultCode: " + resultCode + ". Request Code: " + requestCode + ". Data: " + data.getData().toString());
        Uri selectedImageUri = data.getData();

        String uriImage = selectedImageUri.toString();
        setExtras(uriImage);

        if ( (resultCode == RESULT_OK) && (requestCode == PICTURE_RESULT) ){

                Log.d("GotImage", "URUImage: " + uriImage + ". selectedImageUrl: " + selectedImageUri);
                try {
                    if (Build.VERSION.SDK_INT < 28) {
                        image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                        Log.d("GotImage", "ImageURI: " + selectedImageUri);
                        ivImage.setImageBitmap(image);
                        btnResults.setVisibility(View.VISIBLE);
                        /// use picasso
                        tvSelectedImage.setVisibility(View.INVISIBLE);
                        Toast.makeText(this, "Got image", Toast.LENGTH_LONG).show();

                    } else {
                        ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(),selectedImageUri);
                        image = ImageDecoder.decodeBitmap(source);
                        Log.d("GotImage", "ImageURI: " + selectedImageUri);
                        ivImage.setImageBitmap(image);
                        btnResults.setVisibility(View.VISIBLE);
                        tvSelectedImage.setVisibility(View.INVISIBLE);
                        Toast.makeText(this, "Got image", Toast.LENGTH_LONG).show();
                    }
                } catch (FileNotFoundException e) {
                    Toast.makeText(this, "File not found", Toast.LENGTH_LONG).show();
                    Log.d("GotImage", "File not found: " + e.getMessage());
                    e.printStackTrace();
                } catch (IOException e) {
                    Toast.makeText(this, "An error occurred!", Toast.LENGTH_LONG).show();
                    Log.d("GotImage", "Error: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
            if (selectedImageUri != null) {
//                selectedImageUri = data.getData();
                Log.d("GotImage", "URI was not null: " + selectedImageUri);
                if (Build.VERSION.SDK_INT < 28) {
                    try {
                        image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d("GotImage", "ImageURI: " + selectedImageUri);
                        ivImage.setImageBitmap(image);
                        btnResults.setVisibility(View.VISIBLE);
                        tvSelectedImage.setVisibility(View.INVISIBLE);
                        Toast.makeText(this, "Got image", Toast.LENGTH_LONG).show();

                    } else {
                        ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(),selectedImageUri);
                    try {
                        image = ImageDecoder.decodeBitmap(source);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d("GotImage", "ImageURI: " + selectedImageUri);
                        ivImage.setImageBitmap(image);
                        btnResults.setVisibility(View.VISIBLE);
                        tvSelectedImage.setVisibility(View.INVISIBLE);
                        Toast.makeText(this, "Got image", Toast.LENGTH_LONG).show();
                    }
            }
            else{
                Log.d("GotImage", "Line 245: URI is null");
            }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);

        return Uri.parse(path);
    }

    private void launchCamera() {
        try {
            values = new ContentValues();
            uriImage = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Intent imageCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            imageCapture.putExtra(MediaStore.EXTRA_OUTPUT, uriImage);
            startActivityForResult(imageCapture, PICTURE_RESULT);

        } catch (java.lang.Exception e) {
            Log.d("Error launching camera", e.toString());

            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ImageActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_GALLERY_FROM_CAMERA);

            } else {
                pickImageFromGallery();
            }
        }
    }

//    private Bitmap getBitmapFromURI(Uri uri) throws IOException {
//        ParcelFileDescriptor parcelFileDescriptor = this.getContentResolver().openFileDescriptor(uri, "r");
//        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
//        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
//        parcelFileDescriptor.close();
//
//        return image;
//    }

    @RequiresApi(api = Build.VERSION_CODES.M)
//    @Override
    protected void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);

        if (requestCode == REQUEST_CAMERA) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                launchCamera();
            }

        } else if (requestCode == REQUEST_GALLERY) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                pickImageFromGallery();
            }

        } else if (requestCode == REQUEST_GALLERY_FROM_CAMERA) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                launchCamera();
            }
        }
    }

    public Bitmap centreCrop(Bitmap srcBitmap) {
        if (srcBitmap.getWidth() >= srcBitmap.getHeight()) {
            return Bitmap.createBitmap(
                    srcBitmap,
                    srcBitmap.getWidth() / 2 - srcBitmap.getHeight() / 2,
                    0,
                    srcBitmap.getHeight(),
                    srcBitmap.getWidth()
            );
        } else {
            return Bitmap.createBitmap(
                    srcBitmap,
                    0,
                    srcBitmap.getHeight() / 2 - srcBitmap.getWidth() / 2,
                    srcBitmap.getWidth(),
                    srcBitmap.getWidth()
            );
        }
    }

    private Bitmap rotateImage(Bitmap image, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(Float.parseFloat(String.valueOf(degree)));
        Bitmap rotatedImage = Bitmap.createBitmap(
                image,
                0,
                0,
                image.getWidth(),
                image.getHeight(),
                matrix,
                true
        );
        image.recycle();

        return rotatedImage;
    }

    private Bitmap rotateImageIfRequired(Context context, Bitmap image, Uri selectedImage) throws IOException{
        InputStream input = getContentResolver().openInputStream(selectedImage);

        ExifInterface ei;
        if (Build.VERSION.SDK_INT > 23){
            ei = new ExifInterface(input);

        } else {
            ei = new ExifInterface(selectedImage.getPath());
        }

        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        Bitmap ret;

        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
            ret = rotateImage(image,90);

        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
            ret = rotateImage(image, 180);

        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
            ret = rotateImage(image, 270);
        }
        else{
            ret = image;
        }
        return ret;
    }

    public Bitmap handleSamplingsAndRotationBitmap(Context context, Uri selectedImage) throws IOException {
        int MAX_HEIGHT = 1024;
        int MAX_WIDTH = 1024;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        InputStream imageStream = getContentResolver().openInputStream(selectedImage);
        BitmapFactory.decodeStream(imageStream, null, options);
        imageStream.close();

        options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT);
        options.inJustDecodeBounds = false;

        imageStream = getContentResolver().openInputStream(selectedImage);
        Bitmap image = BitmapFactory.decodeStream(imageStream, null, options);
        image = rotateImageIfRequired(context, image, selectedImage);

        return image;
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        float height = options.outHeight;
        float width = options.outWidth;

        int inSampleSize = 1;

        if ( (height > reqHeight) || (width > reqWidth) ) {
            int heightRatio = Math.round(height / reqHeight);
            int widthRatio = Math.round(width / reqWidth);

            if (heightRatio < widthRatio){
                inSampleSize = heightRatio;
            } else {
                inSampleSize = widthRatio;
            }

            float totalPixels = (float) (width * height);
            float totalReqPixelCap = (float) (reqWidth * reqHeight * 2);

            while (totalPixels / (inSampleSize & inSampleSize) > totalReqPixelCap){
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    public String getRealPathFromUri(Uri contentUri) {
        String[] proj = new String[]{MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(columnIndex);
    }

    // To sign out the user from Google
    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // These ensure the saved info is deleted upon exit.
                        ReadWriteToFile.writeToFileGoogleID("", ImageActivity.this);
                        ReadWriteToFile.writeToFileGoogleName("", ImageActivity.this);
                        startActivity(mainActivity);
                    }
                });
    }

    // To allow the user to revoke access to Google Sign in.
    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // These ensure the saved info is deleted upon exit.
                        ReadWriteToFile.writeToFileGoogleID("", ImageActivity.this);
                        ReadWriteToFile.writeToFileGoogleName("", ImageActivity.this);
                        startActivity(mainActivity);
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnSignOut:
                signOut();
                startActivity(mainActivity);

            case R.id.btnRevokeAccess:
                revokeAccess();
                startActivity(mainActivity);
        }
    }

    // To stop the app from going back to the login screen when already logged in
    @Override
    public void onBackPressed() {
        this.finishAffinity(); // This un-focuses the app and brings the user to their home screen.
//        finish(); // Finishes current indent and opens Log in screen.
//        System.exit(0); // Restarts same intent.
    }

    // Google Sign in failed
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("Google", "Google Sign In failed");
    }

    public void setExtras(String uriImage){
        resultsActivity.putExtra("uriImage", uriImage);
    }
}
