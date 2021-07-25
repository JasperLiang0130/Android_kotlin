package utas.edu.au.liangyc.assignment2.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import utas.edu.au.liangyc.assignment2.R
import utas.edu.au.liangyc.assignment2.converter.CalculatorGrade
import utas.edu.au.liangyc.assignment2.databinding.TutorialWeekItemBinding
import utas.edu.au.liangyc.assignment2.model.Scheme
import utas.edu.au.liangyc.assignment2.model.Student

class RecyclerViewTutorialAdapter(val students: MutableList<Student>, val schemes: MutableList<Scheme>): RecyclerView.Adapter<RecyclerViewTutorialAdapter.TutorialHolder>() {


    class TutorialHolder(val binding: TutorialWeekItemBinding) : RecyclerView.ViewHolder(binding.root){}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TutorialHolder {
        val binding = TutorialWeekItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TutorialHolder(binding)
    }

    override fun onBindViewHolder(holder: TutorialHolder, position: Int) {
        val calculatorGrade = CalculatorGrade(schemes)
        val scheme = schemes[position]
        var base:String? = null
        holder.binding.weekLabel.text = "Week "+scheme.week.toString()
        when(scheme.type.toString()){
            "attendance" -> {
                holder.binding.schemeLabel.setText("Attendance")
                holder.binding.schemeSub.setText("(Attend/Absent)")
                base = " /100.0"
            }
            "level_HD" -> {
                holder.binding.schemeLabel.setText("Grade of level")
                holder.binding.schemeSub.setText("(HD/DN/CR/PP/NN)")
                base = " /100.0"
            }
            "level_A" -> {
                holder.binding.schemeLabel.setText("Grade of level")
                holder.binding.schemeSub.setText("(A/B/C/D/F)")
                base = " /100.0"
            }
            "score" -> {
                holder.binding.schemeLabel.setText("Score of "+scheme.extra)
                holder.binding.schemeSub.setText("(Total score "+scheme.extra+")")
                base = " /"+scheme.extra+".0"
            }
            "checkbox" -> {
                holder.binding.schemeLabel.setText("CheckBoxes of "+scheme.extra)
                holder.binding.schemeSub.setText("(Total "+scheme.extra + " tasks)")
                base = " /"+scheme.extra+".0"
            }
            else -> {
                holder.binding.schemeLabel.setText("")
                holder.binding.schemeSub.setText("")
            }
        }

        holder.binding.weekAvg.text = calculatorGrade.getTutorialAvgGradePerWeek(scheme, students)+base

        holder.itemView.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                //Log.d(RECYCLE_TAG, "CLICK: " + student.name)
                val activity=v!!.context as AppCompatActivity
                var tutDetailFragment = TutorialEditDetailFragment()
                tutDetailFragment.apply {
                    arguments = Bundle().apply {
                        putString("TYPE", scheme.type)
                        putString("WEEK", scheme.week.toString())
                        putString("EXTRA", scheme.extra)
                    }
                }
                activity.supportFragmentManager.beginTransaction().setReorderingAllowed(true)
                    .replace(R.id.tutorialMain, tutDetailFragment, "studentOfListTag").addToBackStack(null).commit()


            }
        })

    }

    override fun getItemCount(): Int {
        return schemes.size
    }
}