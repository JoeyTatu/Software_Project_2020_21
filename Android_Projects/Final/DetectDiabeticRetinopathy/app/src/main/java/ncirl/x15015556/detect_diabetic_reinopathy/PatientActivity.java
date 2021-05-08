package ncirl.x15015556.detect_diabetic_reinopathy;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class PatientActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private Intent mainActivity, imageActivity;
    String patientID;
    TextView tvError, tvNote;
    EditText etPatientID;
    Button btnContinue, btnClear;
    ImageView ivError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title in Android R/10+
        setContentView(R.layout.activity_patient);

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

        //Declare items in XML
        TextView tvGoogleID = findViewById(R.id.tvGoogleID);
        TextView tvGoogleName = findViewById(R.id.tvGoogleName);
        tvError = findViewById(R.id.tvError);
        tvNote = findViewById(R.id.tvNote);
        etPatientID = findViewById(R.id.etPatientID);
        btnContinue = findViewById(R.id.btnContinue);
        btnClear = findViewById(R.id.btnClear);
        ivError = findViewById(R.id.ivError);




        mainActivity = new Intent(PatientActivity.this, SignInActivity.class);
        imageActivity = new Intent(PatientActivity.this, ImageActivity.class);

        // Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestId()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Get Google Info from file

        String googleID = ReadWriteToFile.readFromFileGoogleID(this);
        String googleName = ReadWriteToFile.readFromFileGoogleName(this);

        // Set Google info in XML
        tvGoogleID.setText(googleID);
        tvGoogleName.setText(googleName);


//        if ( (patientID > 999) && (patientID < 100000000)) { //4 to 8 numbers.
////            patientIDString = String.valueOf(patientID); //must to turned back into a string to send to EditText // It's already set. This would set the same value
//            etPatientID.setText(patientIDString);
//        }
        patientIDPresent();

        // Buttons
        Button btnSignOut = findViewById(R.id.btnSignOut);
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        Button btnRevokeAccess = findViewById(R.id.btnRevokeAccess);
        btnRevokeAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                revokeAccess();
            }
        });

        btnContinue.setOnClickListener(v -> {
            patientID = etPatientID.getText().toString();
            if ((patientID.length() >= 4) && (patientID.length() <= 8)) {
                ReadWriteToFile.writeToFilePatientID(patientID, PatientActivity.this);
                startActivity(imageActivity);

            } else {
                ivError.setVisibility(View.VISIBLE);
                tvError.setText(getText(R.string.patientError));
                tvNote.setText(null);
            }
        });

        btnClear.setOnClickListener(v -> {
            ReadWriteToFile.writeToFilePatientID("", PatientActivity.this);
            etPatientID.setText(null);
            tvNote.setText(null);
        });
    }

    // To stop the app from going back to the login screen when already logged in
    @Override
    public void onBackPressed() {
        this.finishAffinity(); // This un-focuses the app and brings the user to their home screen.
//        finish(); // Finishes current indent and opens Log in screen.
//        System.exit(0); // Restarts same intent.
    }


    // To sign out the user from Google
    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // These ensure the saved info is deleted upon exit.
                        ReadWriteToFile.writeToFileGoogleID("", PatientActivity.this);
                        ReadWriteToFile.writeToFileGoogleName("", PatientActivity.this);
                        finish();
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
                        ReadWriteToFile.writeToFileGoogleID("", PatientActivity.this);
                        ReadWriteToFile.writeToFileGoogleName("", PatientActivity.this);
                        finish();
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

            case R.id.btnContinue:
                patientID = etPatientID.getText().toString();
                if ( (patientID.length() >= 4) && (patientID.length() <=8) ) {
                    ReadWriteToFile.writeToFilePatientID(patientID, this);
                    startActivity(imageActivity);

                } else {
                    tvError.setText(getText(R.string.patientError));
                }

            case R.id.btnClear:
                ReadWriteToFile.writeToFilePatientID("", this);
                etPatientID.setText(null);


        }
    }

    // Google Sign in failed
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("Google", "Google Sign In failed");
    }

    // checks whether patient ID is present and if so, adds it to etPatientID
    // and populates tvNote
    private void patientIDPresent() {
        patientID = ReadWriteToFile.readFromFilePatientID(this);

        if ( (patientID == null) || (patientID.equals(null)) || (patientID.equals("")) ){
            tvNote.setText(null);

        }else{
            etPatientID.setText(patientID);
            tvNote.setText(R.string.note);
        }
    }
}
