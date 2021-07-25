package utas.edu.au.liangyc.assignment2.ui.main

import android.content.res.Resources
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.setPadding
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import utas.edu.au.liangyc.assignment2.R
import utas.edu.au.liangyc.assignment2.converter.CalculatorGrade
import utas.edu.au.liangyc.assignment2.databinding.StudentWeekItemBinding
import utas.edu.au.liangyc.assignment2.model.Scheme
import utas.edu.au.liangyc.assignment2.model.Student


private const val SCHEME_TYPE_ATTENDANCE: Int = 0
private const val SCHEME_TYPE_CHECKBOX: Int = 1
private const val SCHEME_TYPE_SCORE:Int = 2
private const val SCHEME_TYPE_LEVEL_HD: Int = 3
private const val SCHEME_TYPE_LEVEL_A:Int = 4
const val EDIT_WEEK_RECYCLER_TAG = "weekRecyclerViewlogging"


class RecyclerViewStudentEditAdapter(val grades: MutableList<String>, val schemes: MutableList<Scheme>, val bindgrade:TextView) : RecyclerView.Adapter<RecyclerViewStudentEditAdapter.StudentEditHolder>() {
    private var checkboxArr = mutableListOf<CheckBox>()
    private var extra:String? = null
    private lateinit var att_spinner:Spinner
    private lateinit var lv_hd_spinner:Spinner
    private lateinit var lv_a_spinner:Spinner
    private lateinit var sc_text:EditText
    private val CHECKBOX_IS_CHECK: Int = 1
    private val CHECKBOX_NOT_CHECK: Int = 0
    private var calculatorGrade:CalculatorGrade = CalculatorGrade(schemes)

    class StudentEditHolder(val binding: StudentWeekItemBinding) : RecyclerView.ViewHolder(binding.root){}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentEditHolder {
        val binding = StudentWeekItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        when(viewType){
            SCHEME_TYPE_ATTENDANCE -> {
                    att_spinner = Spinner(parent.context)
                    att_spinner.layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    //att_spinner.setSelection(1)
                    ArrayAdapter.createFromResource(
                        parent.context,
                        R.array.attendance,
                        android.R.layout.simple_spinner_item
                    ).also {
                        // Specify the layout to use when the list of choices appears
                        adapter -> adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        att_spinner.adapter = adapter
                    }
                    binding.embbedSelect?.addView(att_spinner)
            }
            SCHEME_TYPE_LEVEL_HD -> {
                lv_hd_spinner = Spinner(parent.context)
                lv_hd_spinner.layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                //lv_hd_spinner.setSelection(3)
                ArrayAdapter.createFromResource(
                    parent.context,
                    R.array.level_HD,
                    android.R.layout.simple_spinner_item
                ).also {
                    // Specify the layout to use when the list of choices appears
                        adapter -> adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    lv_hd_spinner.adapter = adapter
                }
                binding.embbedSelect?.addView(lv_hd_spinner)
            }
            SCHEME_TYPE_LEVEL_A -> {
                lv_a_spinner = Spinner(parent.context)
                lv_a_spinner.layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                //lv_a_spinner.setSelection(4)
                ArrayAdapter.createFromResource(
                    parent.context,
                    R.array.level_A,
                    android.R.layout.simple_spinner_item
                ).also {
                    // Specify the layout to use when the list of choices appears
                    adapter -> adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    lv_a_spinner.adapter = adapter
                }
                binding.embbedSelect?.addView(lv_a_spinner)
            }
            SCHEME_TYPE_CHECKBOX -> {
                checkboxArr.clear()
                for(i in 1..extra?.toInt()!!){
                    val checkbox = CheckBox(parent.context)
                    checkbox.setText("Task "+i)
                    checkbox.layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    checkboxArr.add(checkbox)
                    binding.embbedSelect?.addView(checkbox)
                }
            }
            SCHEME_TYPE_SCORE -> {
                sc_text = EditText(parent.context)
                //sc_text.setText("60")
                sc_text.layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                sc_text.setEms(4)
                sc_text.gravity = Gravity.CENTER
                sc_text.setPadding(sc_text.paddingLeft,0, sc_text.paddingRight,0)

                binding.embbedSelect?.addView(sc_text)
            }
        }

        return StudentEditHolder(binding)
    }

    override fun onBindViewHolder(holder: StudentEditHolder, position: Int) {

        var scheme = schemes[position]
        holder.binding.wkId.text = "Week "+scheme.week.toString()
        when(getItemViewType(position)){
            SCHEME_TYPE_ATTENDANCE -> {
                holder.binding.schemeTypeId.text = "ATTENDANCE"
                att_spinner.setSelection(getIndexOfAttendance(grades[scheme.week!!.toInt()-1]))
                //Log.d(EDIT_WEEK_RECYCLER_TAG, "grades: "+grades)
                att_spinner.onItemSelectedListener = object:AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        Log.d(EDIT_WEEK_RECYCLER_TAG, "before: "+grades.toString())
                        grades.set(scheme.week!!.toInt()-1, parent?.getItemAtPosition(position).toString())
                        Log.d(EDIT_WEEK_RECYCLER_TAG, "after: "+grades.toString())

                        //update avg display
                        bindgrade.setText(calculatorGrade.getAvgGrade(grades))
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }
                }
            }
            SCHEME_TYPE_LEVEL_HD -> {
                holder.binding.schemeTypeId.text = "GRADE(HD/DN/CR/PP/NN)"
                lv_hd_spinner.setSelection(getIndexOfLevelHD(grades[scheme.week!!.toInt()-1]))
                lv_hd_spinner.onItemSelectedListener = object:AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        Log.d(EDIT_WEEK_RECYCLER_TAG, "before: "+grades.toString())
                        grades.set(scheme.week!!.toInt()-1, parent?.getItemAtPosition(position).toString())
                        Log.d(EDIT_WEEK_RECYCLER_TAG, "after: "+grades.toString())

                        //update avg display
                        bindgrade.setText(calculatorGrade.getAvgGrade(grades))
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }
                }
            }
            SCHEME_TYPE_LEVEL_A -> {
                holder.binding.schemeTypeId.text = "GRADE(A/B/C/D/F)"
                lv_a_spinner.setSelection(getIndexOfLevelA(grades[scheme.week!!.toInt()-1]))
                lv_a_spinner.onItemSelectedListener = object:AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        Log.d(EDIT_WEEK_RECYCLER_TAG, "before: "+grades.toString())
                        grades.set(scheme.week!!.toInt()-1, parent?.getItemAtPosition(position).toString())
                        Log.d(EDIT_WEEK_RECYCLER_TAG, "after: "+grades.toString())

                        //update avg display
                        bindgrade.setText(calculatorGrade.getAvgGrade(grades))
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }
                }
            }
            SCHEME_TYPE_SCORE -> {
                holder.binding.schemeTypeId.text = ("SCORE OF "+scheme.extra)
                if(grades[scheme.week!!.toInt()-1] != ""){
                    sc_text.setText(grades[scheme.week!!.toInt()-1])
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
                        //Log.d(EDIT_WEEK_RECYCLER_TAG, "before: "+grades.toString())
                        if(sc_text.text.toString() == ""){
                            sc_text.setText("0")
                            grades.set(scheme.week!!.toInt()-1, "0")
                        }else if(sc_text.text.toString().toInt() > scheme.extra!!.toInt()){
                            sc_text.setText(scheme.extra)
                            grades.set(scheme.week!!.toInt()-1, scheme.extra.toString())
                        }else{
                            grades.set(scheme.week!!.toInt()-1, sc_text.text.toString())
                        }

                        //Log.d(EDIT_WEEK_RECYCLER_TAG, "after: "+grades.toString())

                        //update avg display
                        bindgrade.setText(calculatorGrade.getAvgGrade(grades))
                    }


                })
            }
            SCHEME_TYPE_CHECKBOX -> {
                holder.binding.schemeTypeId.text = "CHECKBOX"
                var stu_check_arr: MutableList<Int>
                if(grades[scheme.week!!.toInt()-1] != ""){
                    stu_check_arr = (grades[scheme.week!!.toInt()-1]).split(",").map { it.trim().toInt() } as MutableList<Int>

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
                        Log.d(EDIT_WEEK_RECYCLER_TAG, "before: "+grades.toString())
                        grades.set(scheme.week!!.toInt()-1, stu_check_arr.joinToString(separator = ","))
                        Log.d(EDIT_WEEK_RECYCLER_TAG, "after: "+grades.toString())

                        //update avg display
                        bindgrade.setText(calculatorGrade.getAvgGrade(grades))
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        extra = schemes[position].extra

        return when(schemes[position].type){
            "attendance" -> SCHEME_TYPE_ATTENDANCE
            "checkbox" -> SCHEME_TYPE_CHECKBOX
            "score" -> SCHEME_TYPE_SCORE
            "level_HD" -> SCHEME_TYPE_LEVEL_HD
            else -> SCHEME_TYPE_LEVEL_A
        }
    }

    override fun getItemCount(): Int {
        return schemes.size
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

    fun getFinalGrades():MutableList<String>{
        return grades
    }


}