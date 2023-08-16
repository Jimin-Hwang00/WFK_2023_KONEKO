package nepal.swopnasansar.dto

data class Class(var class_key: String, var class_name: String, var student_key: ArrayList<String>, var teacher_key:String) {
    constructor(): this("", "", ArrayList<String>(), "")
}