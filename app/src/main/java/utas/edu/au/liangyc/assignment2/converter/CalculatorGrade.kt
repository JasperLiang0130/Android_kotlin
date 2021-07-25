package utas.edu.au.liangyc.assignment2.converter

import android.util.Log
import utas.edu.au.liangyc.assignment2.model.Scheme
import utas.edu.au.liangyc.assignment2.model.Student

private val CALCULATOR_TAG = "calculatorlogging"

class CalculatorGrade(cal_schemes:MutableList<Scheme>){
    var c_schemes = cal_schemes

    fun getAvgGrade(grades:MutableList<String>) : String{
        //Log.d(CALCULATOR_TAG, c_schemes.toString())
        //Log.d(CALCULATOR_TAG, grades.toString())

        var total:Float = c_schemes.size.toFloat()
        var sum:Float = 0F
        for (i in 0..c_schemes.size-1){
            when(c_schemes[i].type){
                "attendance" -> {
                    sum = sum + getMarkOfAttendance(grades[i])
                }
                "level_HD" -> {
                    sum = sum + getMarkOfLevelHD(grades[i])
                }
                "level_A" -> {
                    sum = sum + getMarkOfLevelA(grades[i])
                }
                "score" -> {
                    sum = sum + convert100BaseOfScore(c_schemes[i].extra.toString(), grades[i])
                }
                "checkbox" -> {
                    sum = sum + cal_checkbox_avg(grades[i])
                }
            }
        }

        return String.format("%.1f", sum/total)+"%"
    }

    fun getTutorialAvgGradePerWeek(scheme:Scheme, students: MutableList<Student>):String{
        var sum:Float = 0F
        //each scheme means per week
        return when (scheme.type) {
            "attendance" -> {
                for (s in students) {
                    var grades = s.grades
                    sum += getMarkOfAttendance(grades!![scheme.week!! - 1])
                }
                var total: Float = (students.size).toFloat()
                return String.format("%.1f", sum / total)

            }
            "level_HD" -> {
                for (s in students) {
                    var grades = s.grades
                    sum += getMarkOfLevelHD(grades!![scheme.week!! - 1])
                }
                var total: Float = (students.size).toFloat()
                return String.format("%.1f", sum / total)
            }
            "level_A" -> {
                for (s in students) {
                    var grades = s.grades
                    sum += getMarkOfLevelA(grades!![scheme.week!! - 1])
                }
                var total: Float = (students.size).toFloat()
                return String.format("%.1f", sum / total)
            }
            "score" -> {
                for (s in students) {
                    var grades = s.grades
                    if (grades!![scheme.week!! - 1] == "") {
                        sum += 0F
                    } else {
                        sum += grades!![scheme.week!! - 1].toFloat()
                    }
                }
                var total: Float = (students.size * scheme.extra!!.toInt()).toFloat()
                return String.format("%.1f", sum / total * scheme.extra!!.toFloat())

            }
            "checkbox" -> {
                for (s in students) {
                    var grades = s.grades
                    sum += cal_checkbox_total(grades!![scheme.week!! - 1])
                }
                var total: Float = (students.size * scheme.extra!!.toInt()).toFloat()
                return String.format("%.1f", sum / total * scheme.extra!!.toFloat())
            }
            else -> return "N/A"
        }

    }

    private fun cal_checkbox_total(box_arr:String):Float{
        if(box_arr == ""){
            return 0F
        }

        var box_list:MutableList<Float> = box_arr.split(",").map { it.trim().toFloat() } as MutableList<Float>
        var sum:Float = 0F
        for (i in 0..box_list.size-1){
            if(box_list[i] == 1F){
                sum = sum + 1F
            }
        }
        return sum
    }

    private fun cal_checkbox_avg(box_arr:String):Float{
        if(box_arr == ""){
            return 0F
        }

        var box_list:MutableList<Float> = box_arr.split(",").map { it.trim().toFloat() } as MutableList<Float>
        var sum:Float = 0F
        for (i in 0..box_list.size-1){
            if(box_list[i] == 1F){
                sum = sum + 1F
            }
        }
        return sum/(box_list.size.toFloat())*100F
    }

    private fun convert100BaseOfScore(base:String, score:String):Float{
        if(score == ""){
            return 0F
        }
        return score.toFloat()/base.toFloat()*100F
    }

    private fun getMarkOfAttendance(grade:String) : Float{
        return when(grade){
            "Absent" -> 0F
            "Attend" ->100F
            else -> 0F //for ""
        }
    }

    private fun getMarkOfLevelHD(grade:String):Float{
        return when(grade){
            "HD+" -> 100F
            "HD"  -> 80F
            "DN"  -> 70F
            "CR"  -> 60F
            "PP"  -> 50F
            else  -> 0F
        }
    }

    private fun getMarkOfLevelA(grade:String) : Float{
        return when(grade){
            "A" -> 100F
            "B" -> 80F
            "C" -> 70F
            "D" -> 60F
            else -> 0F
        }
    }
}