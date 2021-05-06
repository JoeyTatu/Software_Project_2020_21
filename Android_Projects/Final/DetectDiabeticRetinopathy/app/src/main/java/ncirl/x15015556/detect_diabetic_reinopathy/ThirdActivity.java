package ncirl.x15015556.detect_diabetic_reinopathy;

import android.os.Build;
import android.os.Bundle;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

public class ThirdActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title in Android R/10+
        setContentView(R.layout.activity_third);

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
    }

    // To stop the app from going back to the login screen when already logged in
    @Override
    public void onBackPressed() {
        this.finishAffinity(); // This un-focuses the app and brings the user to their home screen.
//        finish(); // Finishes current indent and opens Log in screen.
//        System.exit(0); // Restarts same intent.
    }
}
