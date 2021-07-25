package utas.edu.au.liangyc.assignment2.ui.main

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import utas.edu.au.liangyc.assignment2.Communicator
import utas.edu.au.liangyc.assignment2.R
import utas.edu.au.liangyc.assignment2.StudentDialogCallback
import utas.edu.au.liangyc.assignment2.databinding.FragmentStudentBinding
import utas.edu.au.liangyc.assignment2.model.Scheme
import utas.edu.au.liangyc.assignment2.model.Student


const val FIREBASE_TAG = "FirebaseLogging"
const val DISTORY_TAG = "StudnetDestoryLogging"

class StudentFragment : Fragment(), Communicator{

    private lateinit var inflatedView: FragmentStudentBinding
    private var students = mutableListOf<Student>()
    private var schemes = mutableListOf<Scheme>()
    private val db = Firebase.firestore
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        saveInstanceState: Bundle?
    ) : View? {

        inflatedView = FragmentStudentBinding.inflate(layoutInflater, container, false)

        //do normal oncreate things in here e.g.
        //inflatedView.textView.setText("hello this is student page")
        //btnCamera.setOnClickListener...
        return inflatedView.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //val item1 = Student(R.drawable.man, "Jasper", "490858")
        //val item2 = Student(R.drawable.woman, "Jenny", "456789")
        //val items = mutableListOf<Student>(item1, item2)

        inflatedView.studentList.layoutManager = LinearLayoutManager(requireActivity()!!.applicationContext)
        inflatedView.studentList.adapter = RecyclerViewStudentAdapter(students, schemes)
        var studentsCollection = db.collection("students")
        studentsCollection
            .get()
            .addOnSuccessListener { result ->
                students.clear() //this line clears the list, and prevents a bug where items would be duplicated upon rotation of screen
                Log.d(FIREBASE_TAG, "--- all students on start---")
                for (document in result) {
                    //Log.d(FIREBASE_TAG, document.toString())
                    val dstu = document.toObject<Student>()
                    var student = Student(dstu.pk, dstu.img, dstu.name, dstu.id, dstu.grades)
                    Log.d(FIREBASE_TAG, student.name.toString())

                    students.add(student)
                }

                var schemesCollection = db.collection("schemes")
                schemesCollection.orderBy("week")
                    .get()
                    .addOnSuccessListener { result ->
                        schemes.clear()
                        for (document in result) {
                            val dsch = document.toObject<Scheme>()
                            var scheme = Scheme(dsch.pk, dsch.week, dsch.type, dsch.extra)
                            schemes.add(scheme)
                        }
                        (inflatedView.studentList.adapter as RecyclerViewStudentAdapter).notifyDataSetChanged()
                    }

            }


        inflatedView.stuFab.setOnClickListener{
            //val i = Intent(this.activity, NewStudentFragment::class.java)
            //activity?.startActivity(i)
            loadFragment(NewStudentFragment(), StudentFragment(), "backStack")
        }

        inflatedView.filterStu.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d(EDIT_WEEK_RECYCLER_TAG, s.toString())
                //Log.d(EDIT_WEEK_RECYCLER_TAG, s!!.length.toString())
                var filter_studens = mutableListOf<Student>()
                if(students.size != 0 && s!!.length != 0){
                    students.forEach { stu ->
                        if(stu.name!!.length >= s.length && stu.name!!.substring(0, s!!.length).toLowerCase() == s.toString().toLowerCase()){
                            filter_studens.add(stu)
                        }
                    }
                }
                if(filter_studens.size == 0){
                    inflatedView.studentList.adapter = RecyclerViewStudentAdapter(students, schemes)
                }else{
                    inflatedView.studentList.adapter = RecyclerViewStudentAdapter(filter_studens, schemes)
                }

                /*
                for(f in filter_studens){
                    Log.d(EDIT_WEEK_RECYCLER_TAG, f.name.toString())
                }

                 */

            }

            override fun afterTextChanged(s: Editable?) {

            }

        })


    }

    override fun loadFragment(fragment: Fragment, rmFragment: Fragment, backStack: String?) {


        val transaction = childFragmentManager.beginTransaction()

        transaction.setReorderingAllowed(true)
        transaction.replace(R.id.studentMain,fragment).remove(rmFragment)
        transaction.addToBackStack(backStack)
        //transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        transaction.commit()
    }


    override fun onResume() {
        super.onResume()

         (inflatedView.studentList.adapter as RecyclerViewStudentAdapter).notifyDataSetChanged()

        //Log.d(FIREBASE_TAG, "child fms: "+childFragmentManager.fragments.toString())
        //Log.d(FIREBASE_TAG, "parent fms: "+parentFragment?.fragmentManager?.fragments.toString())

    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if(isVisibleToUser){
            Log.d(FIREBASE_TAG, "student page is called...")
            var studentsCollection = db.collection("students")
            studentsCollection
                .get()
                .addOnSuccessListener { result ->
                    students.clear() //this line clears the list, and prevents a bug where items would be duplicated upon rotation of screen
                    Log.d(FIREBASE_TAG, "--- all students on start---")
                    for (document in result) {
                        //Log.d(FIREBASE_TAG, document.toString())
                        val dstu = document.toObject<Student>()
                        var student = Student(dstu.pk, dstu.img, dstu.name, dstu.id, dstu.grades)
                        Log.d(FIREBASE_TAG, student.name.toString())

                        students.add(student)
                    }

                    var schemesCollection = db.collection("schemes")
                    schemesCollection.orderBy("week")
                        .get()
                        .addOnSuccessListener { result ->
                            schemes.clear()
                            for (document in result) {
                                val dsch = document.toObject<Scheme>()
                                var scheme = Scheme(dsch.pk, dsch.week, dsch.type, dsch.extra)
                                schemes.add(scheme)
                            }
                            (inflatedView.studentList.adapter as RecyclerViewStudentAdapter).notifyDataSetChanged()
                        }

                }

        }
    }


}