/*
 * @References:
 * Global variables: Vishal Yidav - https://stackoverflow.com/questions/1944656/android-global-variable
 *
 * Facebook Login: Facebook, Inc. - https://developers.facebook.com/apps/927107168075416/fb-login/quickstart
 * Facebook Login: ProgrammingKnowledge - https://www.youtube.com/watch?v=qAN9KYhOSec
 *
 * Remove Header Bar: Boris Strandjev, Andriy D. - https://stackoverflow.com/questions/62835053/how-to-set-fullscreen-in-android-r
 *
 * Google Sign In: Google, Inc - https://developers.google.com/identity/sign-in/android/start-integrating
 * Google Sign In: ProgrammingKnowledge - https://www.youtube.com/watch?v=uPg1ydmnzpk
 *
 * OnSavedInstanceState/OnRestoreInstanceState: Zhar & Reto Meier - https://stackoverflow.com/questions/151777/how-to-save-an-activity-state-using-save-instance-state
 */

package ncirl.x15015556.detect_diabetic_reinopathy;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;


// FACEBOOK CODE IN MainActivity_OldVersion3.java.
// Facebook only allows API access for 72 hours for apps in development stage.
// Code is present and works, but may not work with examiner.

public class SignInActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener{

    //    private TextView textStatus;
//    private TextView status; // This will show any errors with Login to the user
//    private String username, facebookID, googleID;
    private String googleID, googleName;
    private Intent patientActivity;
    boolean signedIn;

    // Google
    SignInButton btnGoogleSignIn;
    private GoogleApiClient mGoogleApiClient;
    private static final int GOOGLE_SIGN_IN = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        FacebookSdk.sdkInitialize(getApplicationContext());
//        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        setContentView(R.layout.activity_sign_in);

        //App runs full screen, no header or status bar.
        // Version R is Android 10+ - if/else for compatibility
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            setContentView(R.layout.activity_sign_in);
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

        patientActivity = new Intent(SignInActivity.this, PatientActivity.class);

//        signedIn = checkIfSignedIn();
//        if (signedIn){
//            startActivity(patientActivity);
//        }


        //Google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestId()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            startActivity(patientActivity);
        }


        btnGoogleSignIn = findViewById(R.id.btn_google_sign_in);
        btnGoogleSignIn.setOnClickListener(v -> googleSignIn());


    }

    @Override
    public void onBackPressed() {
        this.finishAffinity(); // This un-focuses the app and brings the user to their home screen.
//        finish(); // Finishes current indent and opens Log in screen.
//        System.exit(0); // Restarts same intent.
    }

//    private boolean checkIfSignedIn() {
//        signedIn = false;
//        googleID = ReadWriteToFile.readFromFileGoogleID(this);
//        googleName = ReadWriteToFile.readFromFileGoogleName(this);
//
//        Log.d("acct", "CheckIfSignedIn - ID: " + googleID + ". Name: " + googleName);
//        if ( ( googleID != null) && (googleName != null) ){
//            signedIn = true;
//        }
//        else if ( (!googleID.equals("")) && (!googleName.equals("")) ){
//            signedIn = true;
//        }
//        else {
//            signedIn = false;
//        }
//        return signedIn;
//    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

//            case R.id.btn_google_sign_in: //Already integrated into Facebook Login button, can't access it from here
//                facebookLogin();

            case R.id.btn_google_sign_in:
                googleSignIn();
        }

    }

    private void googleSignIn() {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(intent, GOOGLE_SIGN_IN);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if (requestCode == FB_SIGN_IN) {
//            callbackManager.onActivityResult(requestCode, resultCode, data);
//            startActivity(secondActivity);
//        }

        if (requestCode == GOOGLE_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()){
                GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
                googleID = acct.getId();
                googleName = acct.getDisplayName();

                Log.d("acct", "ID: " + googleID + ". Name: " + googleName);

                ReadWriteToFile.writeToFileGoogleID(googleID, this);
                ReadWriteToFile.writeToFileGoogleName(googleName, this);

                startActivity(patientActivity);

            }
        }
    }

    // Google Sign in failed
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("LoginResult", "Google Sign In failed");
    }
}
