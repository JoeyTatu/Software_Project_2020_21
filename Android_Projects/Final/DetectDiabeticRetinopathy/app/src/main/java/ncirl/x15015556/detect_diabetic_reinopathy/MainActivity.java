package ncirl.x15015556.detect_diabetic_reinopathy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

//This intent just checks if the user has previously signed in and directs
// them to the PatientActivity if they have or SignInActivity if not.

public class MainActivity extends AppCompatActivity {

    Intent signInActivity, patientActivity;
    String googleID, googleName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signInActivity = new Intent(MainActivity.this, SignInActivity.class);
        patientActivity = new Intent(MainActivity.this, PatientActivity.class);

        googleID = ReadWriteToFile.readFromFileGoogleID(this);
        googleName = ReadWriteToFile.readFromFileGoogleName(this);
        Log.d("googleMA", "ID: " + googleID + ". Name: " + googleName);
        if ( (googleID  == null) || (googleID.equals("")) || (googleName  == null) || (googleName.equals("")) ) {
            startActivity(signInActivity);
        }
        else {
            startActivity(patientActivity);
        }
    }

    // This is added just in case the app behaves weird and shows this intent for an extended period
    @Override
    public void onBackPressed() {
        this.finishAffinity(); // This un-focuses the app and brings the user to their home screen.
//        finish(); // Finishes current indent and opens Log in screen.
//        System.exit(0); // Restarts same intent.
    }
}
