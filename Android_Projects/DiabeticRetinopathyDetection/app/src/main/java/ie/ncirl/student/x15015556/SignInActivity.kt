/* @references:

Vishnu, J., 2020. Integrating Google Sign-In into Android App. [Online]
Available at: https://medium.com/dev-genius/integrating-google-sign-in-into-android-app-70d582f4eed8
[Accessed 17 December 2020].
*/

package ie.ncirl.student.x15015556

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

class SignInActivity : AppCompatActivity() {
    private val RC_SIGN_IN = 1 //@ref (Vishnu, 2020)

    private var mGoogleSignInClient: GoogleSignInClient? = null //@ref (Vishnu, 2020)

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_main)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build() //@ref (Vishnu, 2020)

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso) //@ref (Vishnu, 2020)

    }

    override fun onStart() { //@ref (Vishnu, 2020)
        super.onStart()
        val account = GoogleSignIn.getLastSignedInAccount(this)

        if (account != null){
            val intent = Intent(this@SignInActivity, UserProfile::class.java)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) { //@ref (Vishnu, 2020)
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)

                val intent = Intent(this@SignInActivity, UserProfile::class.java)
                startActivity(intent)
            } catch (e: ApiException) {
                // The ApiException status code indicates the detailed failure reason
                // Please refer to the GoogleSignInStatusCodes class reference for more information
                Log.e("TAG", "signInResult:failed code=" + e.statusCode)
            }
        }
    }
}