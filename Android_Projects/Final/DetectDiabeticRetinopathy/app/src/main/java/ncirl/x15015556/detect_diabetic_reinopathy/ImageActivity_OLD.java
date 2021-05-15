//package ncirl.x15015556.detect_diabetic_reinopathy;
//
//import android.content.Context;
//import android.content.Intent;
//import android.os.Build;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.view.WindowInsets;
//import android.view.WindowInsetsController;
//import android.view.WindowManager;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.android.gms.auth.api.Auth;
//import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.common.api.ResultCallback;
//import com.google.android.gms.common.api.Status;
//
//import java.io.BufferedReader;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//
//public class ImageActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener  {
//
//    public static final int RESULT_LOAD_IMG = 1;
//
//    Button btnTakePhoto, btnSignOut, btnRevokeAccess;
//    ImageView ivCameraPhoto;
//    String currentPhotoPath;
//    String patientID, googleID, googleName;
//    TextView tvPatientID, tvGoogleID, tvGoogleName;
//    Intent mainActivity, patientActivity;
//    GoogleApiClient mGoogleApiClient;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
////        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title in Android R/10+
//        setContentView(R.layout.activity_image);
//
//        //App runs full screen, no header or status bar.
//        // Version R is Android 10+ - if/else for compatibility
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//
//            // Removes the header/title bar in app
//            // Android R is version 10 - if/else for versions older versions
//            final WindowInsetsController insetsController = getWindow().getInsetsController();
//            if (insetsController != null) {
//                insetsController.hide(WindowInsets.Type.statusBars());
//            }
//        } else {
//            getWindow().setFlags(
//
//                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                    WindowManager.LayoutParams.FLAG_FULLSCREEN
//            );
//        }
//
//        // Google Sign In
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail()
//                .requestId()
//                .build();
//
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .enableAutoManage(this, this)
//                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
//                .build();
//
//        mainActivity = new Intent(ImageActivity.this, SignInActivity.class);
//        patientActivity = new Intent(ImageActivity.this, PatientActivity.class);
//        btnSignOut = findViewById(R.id.btnSignOut);
//        btnRevokeAccess = findViewById(R.id.btnRevokeAccess);
//
//        // Buttons
//        btnSignOut = findViewById(R.id.btnSignOut);
//        btnSignOut.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                signOut();
//                startActivity(mainActivity);
//            }
//        });
//
//        btnRevokeAccess = findViewById(R.id.btnRevokeAccess);
//        btnRevokeAccess.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                revokeAccess();
//                startActivity(mainActivity);
//            }
//        });
//
//        //Declaring patientID textview, and getting relevant values from storage, if present.
//        tvPatientID = findViewById(R.id.tvPatientID);
//        patientID = ReadWriteToFile.readFromFilePatientID(this);
//        // if no PatientID stored, go to PatientActivity, else populate patientID
//        if( (patientID.equals(null)) || (patientID.equals("")) ){
//            startActivity(patientActivity);
//
//        } else {
//            tvPatientID.setText(patientID);
//        }
//
//        //Declaring GoogleID and Name textview, and getting relevant values from storage, if present.
//        tvGoogleID = findViewById(R.id.tvGoogleID);
//        tvGoogleName = findViewById(R.id.tvGoogleName);
//        googleName = ReadWriteToFile.readFromFileGoogleName(this);
//        googleID = ReadWriteToFile.readFromFileGoogleID(this);
//        //if no Google Name or ID, go back to SignInActivity to sign in, else populate with GoogleID and GoogleName
//        if ( (googleID.equals(null)) || (googleID.equals("")) || (googleName.equals(null)) || (googleName.equals("")) ) {
//            startActivity(mainActivity);
//
//        } else {
//            tvGoogleName.setText(googleName);
//            tvGoogleID.setText(googleID);
//        }
//
//
//
////        ivCameraPhoto = findViewById(R.id.ivCameraPhoto);
////        btnTakePhoto = findViewById(R.id.btnTakePhoto);
////        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
////                photoPickerIntent.setType("image/*");
////                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
////            }
////        });
//    }
//
//    // To sign out the user from Google
//    private void signOut() {
//        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
//                new ResultCallback<Status>() {
//                    @Override
//                    public void onResult(Status status) {
//                        // These ensure the saved info is deleted upon exit.
//                        ReadWriteToFile.writeToFileGoogleID("", ImageActivity.this);
//                        ReadWriteToFile.writeToFileGoogleName("", ImageActivity.this);
//                        startActivity(mainActivity);
//                    }
//                });
//    }
//
//    // To allow the user to revoke access to Google Sign in.
//    private void revokeAccess() {
//        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
//                new ResultCallback<Status>() {
//                    @Override
//                    public void onResult(Status status) {
//                        // These ensure the saved info is deleted upon exit.
//                        ReadWriteToFile.writeToFileGoogleID("", ImageActivity.this);
//                        ReadWriteToFile.writeToFileGoogleName("", ImageActivity.this);
//                        startActivity(mainActivity);
//                    }
//                });
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//
//            case R.id.btnSignOut:
//                signOut();
//                startActivity(mainActivity);
//
//            case R.id.btnRevokeAccess:
//                revokeAccess();
//                startActivity(mainActivity);
//        }
//    }
//
////    @Override
////    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
////        super.onActivityResult(requestCode, resultCode, data);
////
////        if (resultCode == RESULT_OK) {
////            try{
////                final Uri imageUri = data.getData();
////                final InputStream imageStram = getContentResolver().openInputStream(imageUri);
////                Bitmap selectedImage = BitmapFactory.decodeStream(imageStram);
////                ivCameraPhoto.setImageBitmap(selectedImage);
////
////            } catch (FileNotFoundException e) {
////                Toast.makeText(this,"An error occurred!", Toast.LENGTH_LONG).show();
////            }
////
////        } else {
////            Toast.makeText(this, "An image was not selected", Toast.LENGTH_LONG).show();
////        }
////    }
//
//    // To stop the app from going back to the login screen when already logged in
//    @Override
//    public void onBackPressed() {
//        this.finishAffinity(); // This un-focuses the app and brings the user to their home screen.
////        finish(); // Finishes current indent and opens Log in screen.
////        System.exit(0); // Restarts same intent.
//    }
//
//    public static String readFromFilePatientID(Context context) {
//        String patientID = "";
//
//        try {
//            InputStream inputStream = context.openFileInput("patientID.txt");
//
//            if (inputStream != null) {
//                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//                String receiveString = "";
//                StringBuilder stringBuilder = new StringBuilder();
//
//                while ((receiveString = bufferedReader.readLine()) != null) {
//                    stringBuilder.append(receiveString);
//                }
//
//                inputStream.close();
//                patientID = stringBuilder.toString();
//
//            }
//
//        } catch (FileNotFoundException e) {
//            Log.e("ReadFromFile", "patientID.txt not found: " + e.toString());
//
//        } catch (IOException e) {
//            Log.e("ReadFromFile", "Cannot read file 'patientID.txt': " + e.toString());
//
//        }
//        return patientID;
//    }
//
//    // Google Sign in failed
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//        Log.d("Google", "Google Sign In failed");
//    }
//
//
////    private void dispatchTakePictureIntent() {
////        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
////        // Ensure that there's a camera activity to handle the intent
////        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
////            // Create the File where the photo should go
////            File photoFile = null;
////            try {
////                photoFile = createImageFile();
////            } catch (IOException ex) {
////                // Error occurred while creating the File
////
////            }
////            // Continue only if the File was successfully created
////            if (photoFile != null) {
////                Uri photoURI = FileProvider.getUriForFile(this,
////                        "com.example.android.fileprovider",
////                        photoFile);
////                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
////                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
////            }
////        }
////    }
////
////    @Override
////    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
////        super.onActivityResult(requestCode, resultCode, data);
////        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
////            if (data != null) {
////                Bundle extras = data.getExtras();
////                Bitmap imageBitmap = (Bitmap) extras.get("data");
////                ivCameraPhoto.setImageBitmap(imageBitmap);
////            }
////        }
////    }
//
////    private File createImageFile() throws IOException {
////        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
////        String imageFileName = "JPEG_" + timestamp + "_";
////        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
////        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
////
////        currentPhotoPath = image.getAbsolutePath();
////        return image;
////    }
//
//}
