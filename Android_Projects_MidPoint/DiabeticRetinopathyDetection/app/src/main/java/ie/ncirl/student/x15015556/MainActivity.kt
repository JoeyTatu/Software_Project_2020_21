/* @references:

Vishnu, J., 2020. Integrating Google Sign-In into Android App. [Online]
Available at: https://medium.com/dev-genius/integrating-google-sign-in-into-android-app-70d582f4eed8
[Accessed 17 December 2020].
*/

package ie.ncirl.student.x15015556

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val RC_SIGN_IN = 1 //@ref (Vishnu, 2020)

    private var mGoogleSignInClient: GoogleSignInClient? = null //@ref (Vishnu, 2020)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        google_signIn_btn.setOnClickListener {
            signIn()
        } //@ref (Vishnu, 2020)
    }
    private fun signIn() { //@ref (Vishnu, 2020)
        val intent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(intent, RC_SIGN_IN)

    }
}