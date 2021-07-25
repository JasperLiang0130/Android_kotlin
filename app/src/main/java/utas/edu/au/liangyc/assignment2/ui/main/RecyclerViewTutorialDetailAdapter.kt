package utas.edu.au.liangyc.assignment2.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import utas.edu.au.liangyc.assignment2.R
import utas.edu.au.liangyc.assignment2.converter.Base64
import utas.edu.au.liangyc.assignment2.databinding.TutorialStudentItemBinding
import utas.edu.au.liangyc.assignment2.model.Scheme
import utas.edu.au.liangyc.assignment2.model.Student

class RecyclerViewTutorialDetailAdapter(val students: MutableList<Student>, val scheme: Scheme): RecyclerView.Adapter<RecyclerViewTutorialDetailAdapter.TutorialDetailHolder>(){

    class TutorialDetailHolder(val binding: TutorialStudentItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TutorialDetailHolder {
        val binding = TutorialStudentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TutorialDetailHolder(binding)
    }

    override fun onBindViewHolder(holder: TutorialDetailHolder, position: Int) {
        val student = students[position]
        holder.binding.tutStuId.text = student.id
        holder.binding.tutStuName.text = student.name
        val image = student.img
        if(image != null){
            holder.binding.tutImageView .setImageBitmap(Base64().decodeStringToBitmap(image))
        }else{
            holder.binding.tutImageView.setImageBitmap(null)
        }

        when(scheme.type){
            "attendance" -> {
                if(student.grades!![scheme.week!! -1] == ""){
                    holder.binding.tutGrade.setText("No mark")
                }else{
                    holder.binding.tutGrade.setText(student.grades!![scheme.week!! -1])
                }
            }
            "checkbox" -> {
                if(student.grades!![scheme.week!! -1] == ""){
                    holder.binding.tutGrade.setText("No mark")
                }else{
                    holder.binding.tutGrade.setText(cal_checkbox_total(student.grades!![scheme.week!! -1])+"/"+scheme.extra)
                }
            }
            "score" ->  {
                if(student.grades!![scheme.week!! -1] == ""){
                    holder.binding.tutGrade.setText("No mark")
                }else {
                    holder.binding.tutGrade.setText(student.grades!![scheme.week!! - 1] + "/" + scheme.extra)
                }
            }
            "level_HD" -> {
                if(student.grades!![scheme.week!! -1] == ""){
                    holder.binding.tutGrade.setText("No mark")
                }else{
                    holder.binding.tutGrade.setText(student.grades!![scheme.week!! -1])
                }
            }
            "level_A" -> {
                if(student.grades!![scheme.week!! -1] == ""){
                    holder.binding.tutGrade.setText("No mark")
                }else{
                    holder.binding.tutGrade.setText(student.grades!![scheme.week!! -1])
                }
            }
            else -> "unknown"
        }

        holder.itemView.setOnClickListener(object: View.OnClickListener{
                override fun onClick(v: View?) {
                    Log.d(RECYCLE_TAG, "CLICK: " + student.name)
                    val activity=v!!.context as AppCompatActivity
                    var dialog = EditStudentGradeFragment()
                    dialog.apply {
                        arguments = Bundle().apply {
                            putString("NAME", student.name)
                            putString("ID", student.id)
                            putString("TYPE", scheme.type)
                            putString("WEEK", scheme.week.toString())
                            putString("EXTRA", scheme.extra)
                            putString("GRADE", student.grades!![scheme.week!! -1])
                        }
                    }
                    dialog.show(activity.supportFragmentManager, "editStuGrade")
                }
        })

    }

    override fun getItemCount(): Int {
        return students.size
    }

    private fun cal_checkbox_total(box_arr:String):String{
        if(box_arr == ""){
            return "0"
        }

        var box_list:MutableList<Float> = box_arr.split(",").map { it.trim().toFloat() } as MutableList<Float>
        var sum:Float = 0F
        for (i in 0..box_list.size-1){
            if(box_list[i] == 1F){
                sum = sum + 1F
            }
        }
        return sum.toString()
    }
}