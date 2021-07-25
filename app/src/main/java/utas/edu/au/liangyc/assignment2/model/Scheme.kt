package utas.edu.au.liangyc.assignment2.model

data class Scheme(
    var pk: String?=null,
    var week:Int?=null,
    var type:String? = null,
    var extra:String? = null //for check box give additional value e.g.-> 5 checkboxes
)
