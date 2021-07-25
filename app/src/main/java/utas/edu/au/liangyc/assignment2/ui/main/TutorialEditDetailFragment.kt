package utas.edu.au.liangyc.assignment2.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import utas.edu.au.liangyc.assignment2.databinding.FragmentTutorialDetailBinding
import utas.edu.au.liangyc.assignment2.model.Scheme
import utas.edu.au.liangyc.assignment2.model.Student

class TutorialEditDetailFragment :Fragment() {

    private lateinit var inflatedTutDetailView: FragmentTutorialDetailBinding

    var scheme = Scheme()
    var detail_students = mutableListOf<Student>()
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.getString("TYPE").let {
            scheme.type = it
        }
        arguments?.getString("WEEK").let {
            scheme.week = it!!.toInt()
        }
        arguments?.getString("EXTRA").let{
            scheme.extra = it
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        inflatedTutDetailView = FragmentTutorialDetailBinding.inflate(layoutInflater, container, false)

        //do normal oncreate things in here e.g.
        //btnCamera.setOnclickListener...
        inflatedTutDetailView.tutDesc.setText("Week "+scheme.week+":   "+convertScheme(scheme.type.toString()))

        return inflatedTutDetailView.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inflatedTutDetailView.studentOfTutList.layoutManager = LinearLayoutManager(requireActivity()!!.applicationContext)
        inflatedTutDetailView.studentOfTutList.adapter = RecyclerViewTutorialDetailAdapter(detail_students, scheme)

        val db = Firebase.firestore
        var studentsCollection = db.collection("students")
        studentsCollection
            .get()
            .addOnSuccessListener { result ->
                detail_students.clear() //this line clears the list, and prevents a bug where items would be duplicated upon rotation of screen
                //Log.d(FIREBASE_TAG, "--- all students on start---")
                for (document in result) {
                    val dstu = document.toObject<Student>()
                    var student = Student(dstu.pk, dstu.img, dstu.name, dstu.id, dstu.grades)
                    detail_students.add(student)
                }
                (inflatedTutDetailView.studentOfTutList.adapter as RecyclerViewTutorialDetailAdapter).notifyDataSetChanged()
            }
    }

    override fun onResume() {
        super.onResume()
        Log.d(FIREBASE_TAG, "student call back is called")

        (parentFragmentManager.fragments[1] as TutorialFragment).onResume()

        var studentsCollection = db.collection("students")
        studentsCollection
            .get()
            .addOnSuccessListener { result ->
                detail_students.clear() //this line clears the list, and prevents a bug where items would be duplicated upon rotation of screen
                //Log.d(FIREBASE_TAG, "--- all students on start---")
                for (document in result) {
                    val dstu = document.toObject<Student>()
                    var student = Student(dstu.pk, dstu.img, dstu.name, dstu.id, dstu.grades)
                    detail_students.add(student)
                }
                (inflatedTutDetailView.studentOfTutList.adapter as RecyclerViewTutorialDetailAdapter).notifyDataSetChanged()
            }
    }

    private fun convertScheme(type:String):String{
        return when(type){
            "attendance" -> "Attendance"
            "level_HD" -> "Grade level(HD/DN/CR/PP/NN)"
            "level_A" -> "Grade level(A/B/C/D/F)"
            "checkbox" -> "Checkbox "+scheme.extra
            else -> "Score of "+scheme.extra
        }
    }
}