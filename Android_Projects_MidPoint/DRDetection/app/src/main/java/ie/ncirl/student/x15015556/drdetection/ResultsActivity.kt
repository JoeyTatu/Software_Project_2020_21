package ie.ncirl.student.x15015556.drdetection

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_results.*
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

//val title : TextView? = null
var share_btn: Button? = null
var image: Bitmap? = null
var imageView: ImageView? = null
//var imageUri: Uri? = null
var result: TextView? = null
var tflite: Interpreter? = null
var retino = false



class ResultsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)


        // Get the Uri from previous Activity
        val receiveIntent = intent
        imageView = findViewById(R.id.image_view)
        imageUri = receiveIntent.getParcelableExtra<Uri>("image")
        setImage()

        val startBtn = findViewById<View>(R.id.sendEmail) as Button
//        val title = findViewById<TextView>(R.id.title)
//        title.setpaintFlags(title.getpaintFlags() or Paint.UNDERLINE_TEXT_FLAG)

        val title = findViewById<TextView>(R.id.title)
        title.paintFlags = title.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        startBtn.setOnClickListener(View.OnClickListener { sendEmail() })
        share_btn.setOnClickListener(View.OnClickListener { shareWithEveryone() })
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        loadFile()
    }

    private fun classifyImage(bitmap: Bitmap) {
        var bitmap = bitmap
        val imageSizeX = 224
        val imageSizeY = 224

        // initialize output array
        val inputVal = Array(1) { FloatArray(1) }
        bitmap = getResizedBitmap(bitmap, imageSizeX, imageSizeY)
        val size = bitmap.rowBytes * bitmap.height
        val byteBuffer = ByteBuffer.allocateDirect(4 * 1 *
                224 * 224 * 3) //float_size = 4 bytes
        byteBuffer.order(ByteOrder.nativeOrder())
        val intValues = IntArray(224 * 224)
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0,
                bitmap.width, bitmap.height)
        var pixel = 0
        for (i in 0..223) {
            for (j in 0..223) {
                val `val` = intValues[pixel++]
                byteBuffer.putFloat((`val` shr 16 and 0xFF) * (1f / 255f))
                byteBuffer.putFloat((`val` shr 8 and 0xFF) * (1f / 255f))
                byteBuffer.putFloat((`val` and 0xFF) * (1f / 255f))
            }
        }

        // pass bitmap into the graph
        tflite!!.run(byteBuffer, inputVal)
        Log.wtf("*Prediction", "Result: " + inputVal[0][0])
        if (inputVal[0][0] >= 0.5) {
            if (inputVal[0][0] > 0.85) result!!.setText("Your eye result may be positive for retinopathy.\nConsider consulting a doctor.") else result!!.setText("You may have retinopathy.\nConsider consulting a doctor.")
            result!!.setTextColor(Color.parseColor("#fa0f0f"))
            retino = true
        } else {
            result!!.setText("Your eye is healthy!")
            result!!.setTextColor(Color.parseColor("#00c410"))
        }
    }

    fun getResizedBitmap(bm: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
        val width = bm.width
        val height = bm.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)
        return Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false)
    }

    fun loadFile() {
        try {
            tflite = Interpreter(loadModelFile()!!)
            classifyImage(image!!)
        } catch (e: Exception) {
            makeToast("Error getting model.")
            Log.d("*Model Loading Error", e.toString())
        }
    }

    @Throws(IOException::class)
    fun loadModelFile(): MappedByteBuffer? {
        val fileDescriptor = this.assets.openFd("model.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun setImage() {
        try {
            image = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
            //            image = MainActivity.handleSamplingAndRotationBitmap(getApplicationContext(), imageUri);
//            image = MainActivity.centerCrop(image);
            imageView!!.setImageBitmap(image)
        } catch (e: IOException) {
            e.printStackTrace()
            makeToast("Error getting image")
            Log.d("*Uri to Bitmap Error", e.toString())
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun shareWithEveryone() {
        Log.i("Share Results", "")
        val TO = arrayOf("")
        val CC = arrayOf("")
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.data = Uri.parse("mailto:")
        emailIntent.type = "application/image"
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO)
        emailIntent.putExtra(Intent.EXTRA_CC, CC)
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Eye Scanner")
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi,\nHere are the results from the Eye Scanner.")
        try {
            startActivity(Intent.createChooser(emailIntent, "Share..."))
            finish()
            Log.i("Shared!", "")
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(this@ResultsActivity, "There is no email client installed.", Toast.LENGTH_SHORT).show()
        }
    }

    fun sendEmail() {
        Log.i("Send email", "")
        val TO = arrayOf("")
        val CC = arrayOf("")
        val emailIntent = Intent(Intent.ACTION_VIEW)
        val data = Uri.parse("""
    mailto:?subject=Retino Eye Scan&body=Hello, my eye test results on RetinoScanner is attached below.
    The test result may have been positive for Retinopathy.&to=
    """.trimIndent())
        emailIntent.data = data
        startActivity(Intent.createChooser(emailIntent, "Send mail..."))
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO)
        emailIntent.putExtra(Intent.EXTRA_CC, CC)
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Your subject")
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi,\nHere are the results from the Eye Scanner.")
        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."))
            finish()
            Log.i("Finished sending email.", "")
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(this@ResultsActivity, "There is no email client installed.", Toast.LENGTH_SHORT).show()
        }
    }

    var t: Toast? = null

    fun makeToast(s: String?) {
        if (t != null) t!!.cancel()
        t = Toast.makeText(applicationContext, s, Toast.LENGTH_LONG)
        t!!.show()
    }
}
