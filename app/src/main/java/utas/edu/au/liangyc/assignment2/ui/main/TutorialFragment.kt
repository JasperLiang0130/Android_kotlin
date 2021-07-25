package utas.edu.au.liangyc.assignment2.ui.main

import android.content.Context
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
import utas.edu.au.liangyc.assignment2.SchemeDialogCallback
import utas.edu.au.liangyc.assignment2.databinding.FragmentTutorialBinding
import utas.edu.au.liangyc.assignment2.model.Scheme
import utas.edu.au.liangyc.assignment2.model.Student

class TutorialFragment : Fragment(), SchemeDialogCallback {

    private lateinit var inflatedGradeView:FragmentTutorialBinding
    private var tut_students = mutableListOf<Student>()
    private var tut_schemes = mutableListOf<Scheme>()
    val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        inflatedGradeView = FragmentTutorialBinding.inflate(layoutInflater, container, false)

        //do normal oncreate things in here e.g.
        //btnCamera.setOnclickListener...

        return inflatedGradeView.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inflatedGradeView.gradeList.layoutManager = LinearLayoutManager(requireActivity()!!.applicationContext)
        inflatedGradeView.gradeList.adapter = RecyclerViewTutorialAdapter(tut_students, tut_schemes)

        var schemesCollection = db.collection("schemes")
        schemesCollection.orderBy("week")
            .get()
            .addOnSuccessListener { result ->
                tut_schemes.clear()
                for(document in result){
                    val dsch = document.toObject<Scheme>()
                    var scheme = Scheme(dsch.pk, dsch.week, dsch.type, dsch.extra)
                    tut_schemes.add(scheme)
                }

                Log.d(FIREBASE_TAG, "tut_scheme: "+ tut_schemes.toString())
                db.collection("students")
                    .get()
                    .addOnSuccessListener { result ->
                        tut_students.clear() //this line clears the list, and prevents a bug where items would be duplicated upon rotation of screen
                        for (document in result) {
                            //Log.d(FIREBASE_TAG, document.toString())
                            val dstu = document.toObject<Student>()
                            var student = Student(dstu.pk, dstu.img, dstu.name, dstu.id, dstu.grades)
                            tut_students.add(student)
                        }
                        (inflatedGradeView.gradeList.adapter as RecyclerViewTutorialAdapter).notifyDataSetChanged()
                    }
            }

        inflatedGradeView.schFab.setOnClickListener {
            var dialog = NewSchemeFragment()
            dialog.show(childFragmentManager, "newSchemeDialog")
        }

    }

    override fun onResume() {
        super.onResume()
        Log.d(FIREBASE_TAG, "tutorial view is resume......")
        var schemesCollection = db.collection("schemes")
        schemesCollection.orderBy("week")
            .get()
            .addOnSuccessListener { result ->
                tut_schemes.clear()
                for(document in result){
                    val dsch = document.toObject<Scheme>()
                    var scheme = Scheme(dsch.pk, dsch.week, dsch.type, dsch.extra)
                    tut_schemes.add(scheme)
                }

                Log.d(FIREBASE_TAG, "tut_scheme: "+ tut_schemes.toString())
                db.collection("students")
                    .get()
                    .addOnSuccessListener { result ->
                        tut_students.clear() //this line clears the list, and prevents a bug where items would be duplicated upon rotation of screen
                        for (document in result) {
                            //Log.d(FIREBASE_TAG, document.toString())
                            val dstu = document.toObject<Student>()
                            var student = Student(dstu.pk, dstu.img, dstu.name, dstu.id, dstu.grades)
                            tut_students.add(student)
                        }
                        (inflatedGradeView.gradeList.adapter as RecyclerViewTutorialAdapter).notifyDataSetChanged()
                    }
            }
        //inflatedGradeView.gradeList.adapter?.notifyDataSetChanged()
    }

    override fun onNewSchemeDialogClickOk(newsheme: Scheme) {
        tut_schemes.add(newsheme)
        inflatedGradeView.gradeList.adapter?.notifyDataSetChanged()
    }


}

