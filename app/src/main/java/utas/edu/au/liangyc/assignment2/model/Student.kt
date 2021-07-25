package utas.edu.au.liangyc.assignment2.model

data class Student(
    var pk: String?=null,
    var img: String?=null,
    var name: String?=null,
    var id: String?=null,
    var grades:MutableList<String>? = mutableListOf<String>()
)

