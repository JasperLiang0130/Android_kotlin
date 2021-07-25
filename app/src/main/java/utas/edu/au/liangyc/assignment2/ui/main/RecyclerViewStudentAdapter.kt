package utas.edu.au.liangyc.assignment2.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import utas.edu.au.liangyc.assignment2.R
import utas.edu.au.liangyc.assignment2.converter.Base64
import utas.edu.au.liangyc.assignment2.converter.CalculatorGrade
import utas.edu.au.liangyc.assignment2.databinding.StudentItemBinding
import utas.edu.au.liangyc.assignment2.model.Scheme
import utas.edu.au.liangyc.assignment2.model.Student

const val RECYCLE_TAG = "DATASHAREDLogging"
class RecyclerViewStudentAdapter(var students: MutableList<Student>, var schemes: MutableList<Scheme>) : RecyclerView.Adapter<RecyclerViewStudentAdapter.StudentHolder>() {

    class StudentHolder(val binding: StudentItemBinding) : RecyclerView.ViewHolder(binding.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentHolder {
        val binding = StudentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StudentHolder(binding)
    }

    override fun onBindViewHolder(holder: StudentHolder, position: Int) {
        val student = students[position]
        var calculatorGrade = CalculatorGrade(schemes)
        holder.binding.txtStuID.text = student.id
        holder.binding.txtStuName.text = student.name
        val image = student.img
        if(image != null){
            holder.binding.imageView.setImageBitmap(Base64().decodeStringToBitmap(image))
        }else{
            holder.binding.imageView.setImageBitmap(null)
        }
        holder.binding.stuGrade.setText(calculatorGrade.getAvgGrade(student.grades!!))

        holder.itemView.setOnClickListener(object:View.OnClickListener{
            override fun onClick(v: View?) {
                Log.d(RECYCLE_TAG, "CLICK: " + student.name)
                val activity=v!!.context as AppCompatActivity
                var editFragment = StudentEditDetailFragment()
                editFragment.apply {
                    arguments = Bundle().apply {
                        putString("NAME", student.name)
                        putString("ID", student.id)
                        putString("IMG", student.img)
                    }
                }
                activity.supportFragmentManager.beginTransaction().setReorderingAllowed(true)
                    .replace(R.id.studentMain, editFragment).addToBackStack(null).commit()

            }
        })

    }

    override fun getItemCount(): Int {
        return students.size
    }
}

/*
class RecyclerViewStudentAdapter(var context: Context, val students: MutableList<Student>) : RecyclerView.Adapter<RecyclerViewStudentAdapter.StudentHolder>() {

    class StudentHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtStuName = itemView.findViewById<TextView>(R.id.txtStuName)
        val txtStuID = itemView.findViewById<TextView>(R.id.txtStuID)
        val img = itemView.findViewById<ImageView>(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentHolder {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        return StudentHolder(inflater.inflate(R.layout.student_item, parent, false))
    }

    override fun onBindViewHolder(holder: StudentHolder, position: Int) {
        val student = students[position]
        holder.txtStuID.text = student.id
        holder.txtStuName.text = student.name
        holder.img.setImageResource(student.img)
    }

    override fun getItemCount(): Int {
        return students.size
    }
}
*/
