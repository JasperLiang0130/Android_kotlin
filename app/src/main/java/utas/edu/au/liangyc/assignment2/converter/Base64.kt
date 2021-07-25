package utas.edu.au.liangyc.assignment2.converter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.util.Base64
import java.io.ByteArrayOutputStream


class Base64 {
    private val CRITERIA_IMG_WIDTH = 200
    private val CRITERIA_IMG_HEIGHT = 200

    private fun resizeBase64Image(base64image: String): String {
        val imgBytes: ByteArray = Base64.decode(base64image.toByteArray(), Base64.DEFAULT)
        val opts = BitmapFactory.Options()
        var img = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.size, opts)
        img = Bitmap.createScaledBitmap(img, CRITERIA_IMG_WIDTH, CRITERIA_IMG_HEIGHT, false)
        val os = ByteArrayOutputStream()
        img.compress(Bitmap.CompressFormat.PNG, 100, os)
        return Base64.encodeToString(os.toByteArray(), Base64.NO_WRAP)
    }

    fun decodeStringToBitmap(base64String: String): Bitmap {
        val decodedString = Base64.decode(resizeBase64Image(base64String), Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString,0, decodedString.size)
    }

    fun encodeBitmapToString(img : Bitmap): String{
        val os = ByteArrayOutputStream()
        img.compress(Bitmap.CompressFormat.PNG, 100, os)
        return Base64.encodeToString(os.toByteArray(), Base64.NO_WRAP)
    }

}