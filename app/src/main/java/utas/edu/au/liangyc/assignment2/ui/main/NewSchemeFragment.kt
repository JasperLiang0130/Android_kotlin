package utas.edu.au.liangyc.assignment2.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import utas.edu.au.liangyc.assignment2.R
import utas.edu.au.liangyc.assignment2.SchemeDialogCallback
import utas.edu.au.liangyc.assignment2.databinding.FragmentNewSchemeBinding
import utas.edu.au.liangyc.assignment2.model.Scheme
import utas.edu.au.liangyc.assignment2.model.Student


class NewSchemeFragment : DialogFragment(){

    private lateinit var inflatedNewSchemeView: FragmentNewSchemeBinding
    private var ns_schemes = mutableListOf<Scheme>()
    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        inflatedNewSchemeView = FragmentNewSchemeBinding.inflate(layoutInflater, container, false)

        db.collection("schemes")
            .orderBy("week")
            .get()
            .addOnSuccessListener { result ->
                ns_schemes.clear()
                for(document in result){
                    val dsch = document.toObject<Scheme>()
                    var scheme = Scheme(dsch.pk, dsch.week, dsch.type, dsch.extra)
                    ns_schemes.add(scheme)
                }
                inflatedNewSchemeView.newWeekNum.text = (ns_schemes.size+1).toString()
                inflatedNewSchemeView.editExtra.visibility = View.INVISIBLE
                inflatedNewSchemeView.extra.visibility = View.INVISIBLE
            }

        //setting scheme spinner
        ArrayAdapter.createFromResource(
            requireActivity()!!.applicationContext,
            R.array.scheme_type,
            android.R.layout.simple_spinner_item
        ).also {
            // Specify the layout to use when the list of choices appears
            adapter -> adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            inflatedNewSchemeView.schemeSpinner.adapter = adapter
        }

        return inflatedNewSchemeView.root


    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inflatedNewSchemeView.schemeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(parent?.getItemAtPosition(position).toString()){
                    "Score Of X" -> {
                        inflatedNewSchemeView.extra.setText("X :")
                        inflatedNewSchemeView.editExtra.visibility = View.VISIBLE
                        inflatedNewSchemeView.extra.visibility = View.VISIBLE
                    }
                    "Checkbox" ->{
                        inflatedNewSchemeView.extra.setText("Number of box:")
                        inflatedNewSchemeView.editExtra.visibility = View.VISIBLE
                        inflatedNewSchemeView.extra.visibility = View.VISIBLE
                    }
                    else -> {
                        inflatedNewSchemeView.editExtra.setText("")
                        inflatedNewSchemeView.editExtra.visibility = View.INVISIBLE
                        inflatedNewSchemeView.extra.visibility = View.INVISIBLE
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }

        inflatedNewSchemeView.btnCancel.setOnClickListener {
            dismiss()
        }

        inflatedNewSchemeView.btnNewSchOk.setOnClickListener {
            if(inflatedNewSchemeView.schemeSpinner.selectedItem.toString() == "Score Of X" ){
                when(inflatedNewSchemeView.editExtra.text.toString()){
                    "" -> {
                        Toast.makeText(requireActivity()!!.applicationContext, "X needs to be filled.", Toast.LENGTH_LONG).show()
                    }
                    "0" ->{
                        Toast.makeText(requireActivity()!!.applicationContext, "X value has to greater than 0", Toast.LENGTH_LONG).show()
                    }
                    else -> addNewScheme()
                }
            }else if(inflatedNewSchemeView.schemeSpinner.selectedItem.toString() == "Checkbox" ){
                when(inflatedNewSchemeView.editExtra.text.toString()){
                    "" -> {
                        Toast.makeText(requireActivity()!!.applicationContext, "Number of box needs to be filled.", Toast.LENGTH_LONG).show()
                    }
                    "0" ->{
                        Toast.makeText(requireActivity()!!.applicationContext, "Number of box has to greater than 0", Toast.LENGTH_LONG).show()
                    }
                    else -> addNewScheme()
                }
            }else{
                addNewScheme()
            }

        }

    }

    override fun onStart() {
        super.onStart()
        val dm = activity?.resources?.displayMetrics!!
        dialog!!.window?.setLayout((dm.widthPixels * 0.98).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun convertSchemeToDBType(type: String):String{
        return when(type){
            "Attendance" -> "attendance"
            "Grade(HD+/HD/DN/CR/PP/NN)" -> "level_HD"
            "Grade(A/B/C/D/F)" -> "level_A"
            "Score Of X" -> "score"
            "Checkbox" -> "checkbox"
            else -> ""
        }
    }

    private fun addNewScheme(){
        var newScheme = Scheme(null, inflatedNewSchemeView.newWeekNum.text.toString().toInt(), convertSchemeToDBType(inflatedNewSchemeView.schemeSpinner.selectedItem.toString()), inflatedNewSchemeView.editExtra.text.trim().toString())
        db.collection("schemes")
            .add(newScheme)
            .addOnSuccessListener {
                newScheme.pk = it.id

                //update pk of new scheme
                db.collection("schemes")
                    .document(newScheme.pk!!)
                    .set(newScheme)
                    .addOnSuccessListener {

                        var all_students = mutableListOf<Student>()
                        //get all students for update their grades
                        db.collection("students")
                            .get()
                            .addOnSuccessListener { result ->
                                for (document in result) {
                                    //Log.d(FIREBASE_TAG, document.toString())
                                    val dstu = document.toObject<Student>()
                                    var student = Student(dstu.pk, dstu.img, dstu.name, dstu.id, dstu.grades)
                                    all_students.add(student)
                                }
                                //update each student
                                var sCollection = db.collection("students")
                                for(s in all_students){
                                    s.grades?.add("")
                                    sCollection.document(s.pk!!).set(s)
                                }
                                //finally send back to scheme main page
                                Toast.makeText(requireActivity()!!.applicationContext, "Week "+(ns_schemes.size+1)+" tutorial scheme has been added.", Toast.LENGTH_LONG).show()
                                val callback = parentFragment as? SchemeDialogCallback
                                callback?.onNewSchemeDialogClickOk(newScheme)

                                dismiss()
                            }
                    }
            }
            .addOnFailureListener{
                Toast.makeText(requireActivity()!!.applicationContext, "Adding new scheme fail, please try it again.", Toast.LENGTH_LONG).show()
                dismiss()
            }
    }




}