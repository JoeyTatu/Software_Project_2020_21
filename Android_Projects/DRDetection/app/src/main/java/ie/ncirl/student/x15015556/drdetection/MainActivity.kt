package ie.ncirl.student.x15015556.drdetection

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream
import java.io.IOException


var values : ContentValues? = null
var imageUri : Uri? = null

const val PICTURE_RESULT = 1
const val GALLERY_RESULT = 2
const val REQUEST_CAMERA = 1034
const val REQUEST_GALLERY = 1000
const val REQUEST_GALLERY_FROM_CAMERA = 234

var img : Bitmap? = null
var imageurl : String? = null

class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Function to bind XML views to Java Objects
//        bindViews()

        buttons()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun buttons() {
        next.setOnClickListener {
            val intent = Intent(applicationContext, ResultsActivity::class.java)
            Log.d("*Passing URI", "Is null: " + (imageUri == null))
            intent.putExtra("image", imageUri)
            startActivity(intent)
        }
        camera.setOnClickListener { // Check if we have permission
            if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA)
            } else {
                launchCamera()
            }
        }
        gallery.setOnClickListener {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED) {
                //permission not granted request it.
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                //show popupƒ
                requestPermissions(permissions, REQUEST_GALLERY)
            } else {
                /* permission already granted */
                pickImageFromGallery()
            }
        }
    }

    private fun pickImageFromGallery() {
        //intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_RESULT)
    }

    // Get the result from the Camera/Gallery
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // They didn't cancel
        if (resultCode == RESULT_OK) {
            if (requestCode == PICTURE_RESULT) {
                try {
                    img = MediaStore.Images.Media.getBitmap(
                            contentResolver, imageUri)
                    img = handleSamplingAndRotationBitmap(applicationContext, imageUri)
                    img = centerCrop(img)
                    imageView.setImageBitmap(img)
                    imageurl = getRealPathFromURI(imageUri)
                    makeToast("Got the image")
                    next.visibility = View.VISIBLE
                    text.visibility = View.INVISIBLE
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                imageUri = data!!.data
                try {
                    img = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
                    img = handleSamplingAndRotationBitmap(applicationContext, imageUri)
                    img = centerCrop(img)
                    next.visibility = View.VISIBLE
                    text.visibility = View.INVISIBLE
                    imageView.setImageBitmap(img)
                    makeToast("Got the image")
                } catch (e: IOException) {
                    e.printStackTrace()
                    makeToast("Error getting image")
                    Log.d("*Gallery error: ", e.toString())
                }
            }
        }
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    fun launchCamera() {
        try {
            values = ContentValues()
            imageUri = contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            startActivityForResult(intent, PICTURE_RESULT)
        } catch (e: java.lang.Exception) {
            Log.d("*Error launching Camera", e.toString())
            if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_GALLERY_FROM_CAMERA)
            } else {
                pickImageFromGallery()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA) {
            if (checkSelfPermission(Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                launchCamera()
            }
        } else if (requestCode == REQUEST_GALLERY) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                pickImageFromGallery()
            }
        } else if (requestCode == REQUEST_GALLERY_FROM_CAMERA) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                launchCamera()
            }
        }
    }

//    fun bindViews() {
//        val camera = findViewById(R.id.camera)
//        val gallery = findViewById(R.id.gallery)
//        val imageView = findViewById(R.id.imageView)
//        val text = findViewById(R.id.text)
//        val next = findViewById(R.id.next)
//    }

    fun centerCrop(srcBmp: Bitmap?): Bitmap? {
        if (srcBmp!!.width >= srcBmp!!.height) {
            Log.d("*Cropping", "Image width > height")
            return Bitmap.createBitmap(srcBmp!!, srcBmp.width / 2 - srcBmp.height / 2, 0, srcBmp.height, srcBmp.height)
        }
        return Bitmap.createBitmap(srcBmp!!, 0, srcBmp.height / 2 - srcBmp.width / 2, srcBmp.width, srcBmp.width)
    }

    private fun rotateImage(img: Bitmap, degree: Int): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        val rotatedImg = Bitmap.createBitmap(img, 0, 0, img.width, img.height, matrix, true)
        img.recycle()
        return rotatedImg
    }

    @Throws(IOException::class)
    private fun rotateImageIfRequired(context: Context, img: Bitmap, selectedImage: Uri): Bitmap? {
        val input = context.contentResolver.openInputStream(selectedImage)
        val ei: ExifInterface
        ei = if (Build.VERSION.SDK_INT > 23) ExifInterface(input!!) else ExifInterface(selectedImage.path!!)
        val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(img, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(img, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(img, 270)
            else -> img
        }
    }

    @Throws(IOException::class)
    fun handleSamplingAndRotationBitmap(context: Context, selectedImage: Uri?): Bitmap? {
        val MAX_HEIGHT = 1024
        val MAX_WIDTH = 1024
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        var imageStream = context.contentResolver.openInputStream(selectedImage!!)
        BitmapFactory.decodeStream(imageStream, null, options)
        imageStream!!.close()
        options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT)
        options.inJustDecodeBounds = false
        imageStream = context.contentResolver.openInputStream(selectedImage)
        var img = BitmapFactory.decodeStream(imageStream, null, options)
        img = rotateImageIfRequired(context, img!!, selectedImage)
        return img
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
            val totalPixels = (width * height).toFloat()
            val totalReqPixelsCap = (reqWidth * reqHeight * 2).toFloat()
            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++
            }
        }
        return inSampleSize
    }

    fun getRealPathFromURI(contentUri: Uri?): String? {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = managedQuery(contentUri, proj, null, null, null)
        val column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }

    var t: Toast? = null

    fun makeToast(s: String?) {
        if (t != null) t!!.cancel()
        t = Toast.makeText(applicationContext, s, Toast.LENGTH_LONG)
        t!!.show()
    }
}