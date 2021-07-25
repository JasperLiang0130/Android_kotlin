package utas.edu.au.liangyc.assignment2.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import utas.edu.au.liangyc.assignment2.R
import utas.edu.au.liangyc.assignment2.converter.Base64
import utas.edu.au.liangyc.assignment2.converter.CalculatorGrade
import utas.edu.au.liangyc.assignment2.databinding.FragmentStudentEditDetailBinding
import utas.edu.au.liangyc.assignment2.model.Scheme
import utas.edu.au.liangyc.assignment2.model.Student



class StudentEditDetailFragment : Fragment() {

    //declare list of scheme
    var schemes = mutableListOf<Scheme>()
    //declare list of student grades
    var grades = mutableListOf<String>()

    private lateinit var inflatedView:FragmentStudentEditDetailBinding
    private var s_name:String? = null
    private var s_id:String? = null
    private var s_img:String? = null
    private var student:Student? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getString("NAME").let {
            s_name = it
        }
        arguments?.getString("ID").let {
            s_id = it
        }
        arguments?.getString("IMG").let{
            s_img = it
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        saveInstanceState: Bundle?
    ) : View? {

        inflatedView = FragmentStudentEditDetailBinding.inflate(layoutInflater, container, false)

        //do normal oncreate things in here e.g.
        //inflatedView.textView.setText("hello this is student page")
        //btnCamera.setOnClickListener...
        inflatedView.editSName.setText(s_name)
        inflatedView.editSId.setText(s_id)
        inflatedView.editSImg.setImageBitmap(Base64().decodeStringToBitmap(s_img.toString()))

        return inflatedView.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inflatedView.editSWeekList.layoutManager = LinearLayoutManager(requireActivity()!!.applicationContext)
        inflatedView.editSWeekList.adapter = RecyclerViewStudentEditAdapter(grades, schemes, inflatedView.avgGrade)

        val db = Firebase.firestore
        var studentCollection = db.collection("students").whereEqualTo("name", s_name).whereEqualTo("id", s_id)
        studentCollection
            .get()
            .addOnSuccessListener {
                result ->
                grades.clear()
                for (document in result){
                    val sdcu = document.toObject<Student>()
                    student = Student(sdcu.pk, sdcu.img, sdcu.name, sdcu.id, sdcu.grades)
                    grades.addAll(sdcu.grades!!)
                }
                Log.d(FIREBASE_TAG, grades.toString())

                db.collection("schemes")
                    .orderBy("week")
                    .get()
                    .addOnSuccessListener { result ->
                        schemes.clear()
                        for(document in result){
                            val dsch = document.toObject<Scheme>()
                            var scheme = Scheme(dsch.pk, dsch.week, dsch.type, dsch.extra)
                            Log.d(FIREBASE_TAG, scheme.type!!.toString())
                            schemes.add(scheme)
                        }
                        (inflatedView.editSWeekList.adapter as RecyclerViewStudentEditAdapter).notifyDataSetChanged()

                        //set grade value
                        inflatedView.avgGrade.text = CalculatorGrade(schemes).getAvgGrade(grades)
                    }
            }


        inflatedView.editOk.setOnClickListener {
            Toast.makeText(requireActivity()!!.applicationContext, "Successfully update "+inflatedView.editSName.text.toString(), Toast.LENGTH_LONG).show()
            student!!.name = inflatedView.editSName.text.toString()
            student!!.id = inflatedView.editSId.text.toString()
            student!!.grades = RecyclerViewStudentEditAdapter(grades, schemes, inflatedView.avgGrade).getFinalGrades()
            db.collection("students").document(student!!.pk!!)
                .set(student!!)
                .addOnSuccessListener {
                    Log.d(FIREBASE_TAG, student!!.name+" has been modified")

                    activity?.supportFragmentManager?.beginTransaction()?.setReorderingAllowed(true)
                        ?.replace(R.id.studentMain, StudentFragment())?.remove(StudentEditDetailFragment())?.commit()
                }
        }

        inflatedView.sDelete.setOnClickListener {
            Toast.makeText(requireActivity()!!.applicationContext, inflatedView.editSName.text.toString()+" has been deleted.", Toast.LENGTH_LONG).show()
            db.collection("students").document(student!!.pk!!)
                .delete()
                .addOnSuccessListener {
                    Log.d(FIREBASE_TAG, student!!.name+" has been deleted")

                    activity?.supportFragmentManager?.beginTransaction()?.setReorderingAllowed(true)
                        ?.replace(R.id.studentMain, StudentFragment())?.remove(StudentEditDetailFragment())?.commit()
                }
        }

    }


    override fun onResume() {
        super.onResume()
        inflatedView.editSWeekList.adapter?.notifyDataSetChanged()
    }

}