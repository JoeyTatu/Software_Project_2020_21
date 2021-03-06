/* @references:

Vishnu, J., 2020. Integrating Google Sign-In into Android App. [Online]
Available at: https://medium.com/dev-genius/integrating-google-sign-in-into-android-app-70d582f4eed8
[Accessed 17 December 2020].
*/

package ie.ncirl.student.x15015556

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import ie.ncirl.student.x15015556.R
import ie.ncirl.student.x15015556.SignInActivity
import kotlinx.android.synthetic.main.activity_user_profile.*

class UserProfile : AppCompatActivity(), View.OnClickListener {

    private var mGoogleSignInClient : GoogleSignInClient? = null //@ref (Vishnu, 2020)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        signout_btn.setOnClickListener {
            signOut()
        } //@ref (Vishnu, 2020)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build() //@ref (Vishnu, 2020)

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso) //@ref (Vishnu, 2020)

        val acct = GoogleSignIn.getLastSignedInAccount(this) //@ref (Vishnu, 2020)

        if (acct != null) { //@ref (Vishnu, 2020)
            var personName= acct.displayName
            person_name.setText(personName)

            val personEmail = acct.email
            person_email.setText(personEmail)

            val personId = acct.id
            person_id.setText(personId)
        }
    }

    fun signOut(){
        mGoogleSignInClient?.signOut()?.addOnCompleteListener(this, object:OnCompleteListener<Void>{
            override fun onComplete(p0: Task<Void>) {
                Toast.makeText(this@UserProfile, "Signed Out", Toast.LENGTH_LONG).show()
            }
        })

        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }
}

