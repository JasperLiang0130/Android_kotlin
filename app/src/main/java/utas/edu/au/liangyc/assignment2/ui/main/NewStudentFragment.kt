package utas.edu.au.liangyc.assignment2.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import utas.edu.au.liangyc.assignment2.converter.Base64
import android.view.LayoutInflater
import android.view.SurfaceControl
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import utas.edu.au.liangyc.assignment2.Communicator
import utas.edu.au.liangyc.assignment2.R
import utas.edu.au.liangyc.assignment2.databinding.FragmentNewStudentBinding
import utas.edu.au.liangyc.assignment2.model.Scheme
import utas.edu.au.liangyc.assignment2.model.Student
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

const val NEW_STUDENT_TAG = "NewStudentLogging"
const val REQUEST_IMAGE_CAPTURE = 1

class NewStudentFragment : Fragment(), Communicator {

    private lateinit var inflatedNewView : FragmentNewStudentBinding
    private val db = Firebase.firestore
    var img64Str:String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        saveInstanceState: Bundle?
    ) : View? {
        inflatedNewView = FragmentNewStudentBinding.inflate(layoutInflater, container, false)
        Log.d(NEW_STUDENT_TAG, "back trigger")
        //do normal oncreate things in here e.g.
        //inflatedView.textView.setText("hello this is student page")
        //btnCamera.setOnClickListener...

        return inflatedNewView.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inflatedNewView.btnBack.setOnClickListener{
            parentFragment?.childFragmentManager?.popBackStack()
        }

        inflatedNewView.btnOk.setOnClickListener{

            val bs = Base64()
            var img_default = bs.encodeBitmapToString(BitmapFactory.decodeResource(context?.resources, R.drawable.man))
            if(img64Str == null){
                img64Str = img_default
            }
            var _schemes = mutableListOf<Scheme>()
            var _grades = mutableListOf<String>()

            var schemesCollection = db.collection("schemes")
            schemesCollection.orderBy("week")
                .get()
                .addOnSuccessListener { result ->
                    _schemes.clear()
                    for(document in result){
                        val dsch = document.toObject<Scheme>()
                        var scheme = Scheme(dsch.pk, dsch.week, dsch.type, dsch.extra)
                        //Log.d(FIREBASE_TAG, scheme.type!!.toString())
                        _schemes.add(scheme)
                    }
                    for(i in 0.._schemes.size-1){
                        _grades.add("") //initiaion is ""
                    }
                    //Log.d(FIREBASE_TAG, "_grade size: "+_grades.size)
                    //Log.d(FIREBASE_TAG, "_grade: "+_grades.toString())

                    if(inflatedNewView.editTextTextPersonName.text.toString()!="" && inflatedNewView.editTextNumber.text.toString()!=""){
                        var newStudent = Student(null,img64Str,inflatedNewView.editTextTextPersonName.text.toString(), inflatedNewView.editTextNumber.text.toString(), _grades)
                        var studentsCollection = db.collection("students")
                        studentsCollection
                            .add(newStudent)
                            .addOnSuccessListener {
                                Log.d(FIREBASE_TAG, "Student document created with id ${it.id}")
                                newStudent.pk = it.id

                                //update new student pk
                                studentsCollection.document(newStudent.pk!!)
                                    .set(newStudent)
                                    .addOnSuccessListener {
                                        Log.d(FIREBASE_TAG, "Student document pk has been updated")
                                    }

                                loadFragment(StudentFragment(),this, null)
                            }
                            .addOnFailureListener {
                                Log.e(FIREBASE_TAG, "Error writing student document", it)
                                parentFragment?.childFragmentManager?.popBackStack()
                            }
                    }

                }

        }

        inflatedNewView.addImageView.setOnClickListener {
            requestToTakeAPicture()
        }
    }

    override fun loadFragment(fragment: Fragment, rmFragment: Fragment, backStack: String?) {
        val transaction = fragmentManager?.beginTransaction()

        transaction?.setReorderingAllowed(true)
        transaction?.replace(R.id.studentMain,fragment)?.remove(rmFragment)
        transaction?.addToBackStack(backStack)
        //transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        transaction?.commit()
    }
    //step 4
    private fun requestToTakeAPicture()
    {
        requestPermissions(
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_IMAGE_CAPTURE
        )
    }

    //step 5
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode)
        {
            REQUEST_IMAGE_CAPTURE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission is granted.
                    takeAPicture()
                } else {
                    Toast.makeText(requireActivity().applicationContext, "Cannot access camera, permission denied", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    //step 6
    private fun takeAPicture() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        //try {
        val photoFile: File = createImageFile()!!
        val photoURI: Uri = FileProvider.getUriForFile(
                requireActivity().applicationContext,
                "utas.edu.au.liangyc.assignment2",
                photoFile
        )
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        //} catch (e: Exception) {}

    }

    //step 6 part 2
    lateinit var currentPhotoPath: String
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = requireActivity().applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
                "PNG_${timeStamp}_", /* prefix */
                ".png", /* suffix */
                storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    //step 7
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == AppCompatActivity.RESULT_OK)
        {
            setPic(inflatedNewView.addImageView)
        }
    }

    //step 7 pt2
    private fun setPic(imageView: ImageView) {
        // Get the dimensions of the View
        val targetW: Int = imageView.measuredWidth
        val targetH: Int = imageView.measuredHeight

        val bmOptions = BitmapFactory.Options().apply {
            // Get the dimensions of the bitmap
            inJustDecodeBounds = true

            BitmapFactory.decodeFile(currentPhotoPath, this)

            val photoW: Int = outWidth
            val photoH: Int = outHeight

            // Determine how much to scale down the image
            val scaleFactor: Int = Math.max(1, Math.min(photoW / targetW, photoH / targetH))

            // Decode the image file into a Bitmap sized to fill the View
            inJustDecodeBounds = false
            inSampleSize = scaleFactor
        }
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions)?.also { bitmap ->
            imageView.setImageBitmap(bitmap)
            img64Str = Base64().encodeBitmapToString(bitmap)

        }
    }


}