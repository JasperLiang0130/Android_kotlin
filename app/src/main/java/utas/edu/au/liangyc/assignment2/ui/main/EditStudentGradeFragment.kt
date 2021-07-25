package utas.edu.au.liangyc.assignment2.ui.main

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import utas.edu.au.liangyc.assignment2.R
import utas.edu.au.liangyc.assignment2.databinding.FragmentEditStudentGradeBinding
import utas.edu.au.liangyc.assignment2.databinding.FragmentTutorialBinding
import utas.edu.au.liangyc.assignment2.databinding.FragmentTutorialDetailBinding
import utas.edu.au.liangyc.assignment2.model.Student

class EditStudentGradeFragment : DialogFragment(){

    private lateinit var inflatedEditGradeView:FragmentEditStudentGradeBinding
    private var s_name:String? = null
    private var s_id:String? = null
    private var s_week:String? = null
    private var s_extra:String? = null
    private var s_type:String? = null
    private var s_grade:String? = null
    private var checkboxArr = mutableListOf<CheckBox>()
    private lateinit var att_spinner:Spinner
    private lateinit var lv_hd_spinner:Spinner
    private lateinit var lv_a_spinner:Spinner
    private lateinit var sc_text:EditText
    private val CHECKBOX_IS_CHECK: Int = 1
    private val CHECKBOX_NOT_CHECK: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getString("NAME").let {
            s_name = it
        }
        arguments?.getString("ID").let {
            s_id = it
        }
        arguments?.getString("WEEK").let {
            s_week = it
        }
        arguments?.getString("TYPE").let {
            s_type = it
        }
        arguments?.getString("EXTRA").let {
            s_extra = it
        }
        arguments?.getString("GRADE").let {
            s_grade = it
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        inflatedEditGradeView = FragmentEditStudentGradeBinding.inflate(layoutInflater, container, false)
        when(s_type) {
            "attendance" -> {
                att_spinner = Spinner(context)
                att_spinner.layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                //att_spinner.setSelection(1)
                ArrayAdapter.createFromResource(
                    requireContext(),
                    R.array.attendance,
                    android.R.layout.simple_spinner_item
                ).also {
                    // Specify the layout to use when the list of choices appears
                        adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    att_spinner.adapter = adapter
                }
                inflatedEditGradeView.embbedGrade.addView(att_spinner)
            }
            "level_HD" -> {
                lv_hd_spinner = Spinner(context)
                lv_hd_spinner.layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                //lv_hd_spinner.setSelection(3)
                ArrayAdapter.createFromResource(
                    requireContext(),
                    R.array.level_HD,
                    android.R.layout.simple_spinner_item
                ).also {
                    // Specify the layout to use when the list of choices appears
                        adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    lv_hd_spinner.adapter = adapter
                }
                inflatedEditGradeView.embbedGrade.addView(lv_hd_spinner)
            }
            "level_A" -> {
                lv_a_spinner = Spinner(context)
                lv_a_spinner.layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                //lv_a_spinner.setSelection(4)
                ArrayAdapter.createFromResource(
                    requireContext(),
                    R.array.level_A,
                    android.R.layout.simple_spinner_item
                ).also {
                    // Specify the layout to use when the list of choices appears
                        adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    lv_a_spinner.adapter = adapter
                }
                inflatedEditGradeView.embbedGrade.addView(lv_a_spinner)
            }
            "checkbox" -> {
                checkboxArr.clear()
                for (i in 1..s_extra?.toInt()!!) {
                    val checkbox = CheckBox(context)
                    checkbox.setText("Task " + i)
                    checkbox.layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    checkboxArr.add(checkbox)
                    inflatedEditGradeView.embbedGrade.addView(checkbox)
                }
            }
            "score" -> {
                sc_text = EditText(context)
                //sc_text.setText("60")
                sc_text.layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                sc_text.setEms(4)
                sc_text.gravity = Gravity.CENTER
                sc_text.setPadding(sc_text.paddingLeft, 0, sc_text.paddingRight, 0)

                inflatedEditGradeView.embbedGrade.addView(sc_text)

            }
        }

        return inflatedEditGradeView.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inflatedEditGradeView.nameDisplay.setText(s_name)
        inflatedEditGradeView.idDisplay.setText(s_id)
        when(s_type){
            "attendance" -> {
                att_spinner.setSelection(getIndexOfAttendance(s_grade.toString()))
            }
            "level_HD" ->{
                lv_hd_spinner.setSelection(getIndexOfLevelHD(s_grade.toString()))
            }
            "level_A" -> {
                lv_a_spinner.setSelection(getIndexOfLevelA(s_grade.toString()))
            }
            "checkbox" ->{
                var stu_check_arr: MutableList<Int>
                if(s_grade != ""){
                    stu_check_arr = (s_grade)!!.split(",").map { it.trim().toInt() } as MutableList<Int>

                }else{ //initial status
                    stu_check_arr = mutableListOf()
                    for(i in 0..checkboxArr.size-1){
                        stu_check_arr.add(0)
                    }
                }
                for(i in 0..(checkboxArr.size-1)){
                    if(stu_check_arr[i] == CHECKBOX_IS_CHECK){
                        checkboxArr[i].isChecked = true
                    }else if(stu_check_arr[i] == CHECKBOX_NOT_CHECK){
                        checkboxArr[i].isChecked = false
                    }
                    checkboxArr[i].setOnCheckedChangeListener { buttonView, isChecked ->
                        if(isChecked){
                            stu_check_arr.set(i, CHECKBOX_IS_CHECK)
                        }else{
                            stu_check_arr.set(i, CHECKBOX_NOT_CHECK)
                        }
                    }
                }
            }
            "score" -> {
                if(s_grade != ""){
                    sc_text.setText(s_grade)
                }else{
                    sc_text.setText("0")
                }
                sc_text.addTextChangedListener(object: TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {

                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {

                    }

                    override fun afterTextChanged(s: Editable?) {
                        if(sc_text.text.toString() == ""){
                            sc_text.setText("0")
                        }else if(sc_text.text.toString().toInt() > s_extra!!.toInt()){
                            sc_text.setText(s_extra)
                        }
                    }
                })
                inflatedEditGradeView.baseScore.setText("/"+s_extra)
            }
        }

        inflatedEditGradeView.btnCancel.setOnClickListener {
            dismiss()
        }

        inflatedEditGradeView.btnNewSchOk.setOnClickListener {
            val db = Firebase.firestore
            db.collection("students").whereEqualTo("name", s_name).whereEqualTo("id", s_id)
                .get()
                .addOnSuccessListener { result->
                    var student:Student? = null
                    for (document in result) {
                        //Log.d(FIREBASE_TAG, document.toString())
                        val dstu = document.toObject<Student>()
                        student = Student(dstu.pk, dstu.img, dstu.name, dstu.id, dstu.grades)
                    }

                    //update student grade
                    when(s_type){
                        "attendance" -> {
                            student!!.grades!![s_week!!.toInt() - 1] = att_spinner.selectedItem.toString()
                        }
                        "level_HD" -> {
                            student!!.grades!![s_week!!.toInt() - 1] = lv_hd_spinner.selectedItem.toString()
                        }
                        "level_A" -> {
                            student!!.grades!![s_week!!.toInt() - 1] = lv_a_spinner.selectedItem.toString()
                        }
                        "checkbox" ->{
                            student!!.grades!![s_week!!.toInt() - 1] = checkboxArr.joinToString(separator = ",")
                        }
                        "score" ->{
                            if(sc_text.text.toString() == ""){
                                sc_text.setText("0")
                            }
                            student!!.grades!![s_week!!.toInt() - 1] = sc_text.text.toString()
                        }
                    }

                    db.collection("students").document(student!!.pk!!)
                        .set(student)
                        .addOnSuccessListener {
                            Toast.makeText(requireActivity()!!.applicationContext, s_name+"("+s_id+")"+" has been updated.", Toast.LENGTH_LONG).show()

                            //notify parent to change data view
                            parentFragmentManager.findFragmentByTag("studentOfListTag")!!.onResume()
                            //Log.d(FIREBASE_TAG, callback.toString())
                            dismiss()
                        }
                }
        }

    }

    override fun onStart() {
        super.onStart()
        val dm = activity?.resources?.displayMetrics!!
        dialog!!.window?.setLayout((dm.widthPixels * 0.98).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun getIndexOfAttendance(grade:String) : Int{
        return when(grade){
            "Absent" -> 0
            "Attend" ->1
            else -> 0 //for ""
        }
    }
    private fun getIndexOfLevelHD(grade:String):Int{
        return when(grade){
            "HD+" -> 0
            "HD"  -> 1
            "DN"  -> 2
            "CR"  -> 3
            "PP"  -> 4
            else  -> 5
        }
    }
    private fun getIndexOfLevelA(grade:String) : Int{
        return when(grade){
            "A" -> 0
            "B" -> 1
            "C" -> 2
            "D" -> 3
            else -> 4
        }
    }
}