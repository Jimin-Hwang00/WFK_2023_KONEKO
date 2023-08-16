package nepal.swopnasansar.dto

data class Teacher(var teacher_key: String, var teacher_name: String, var email: String) {
    constructor(): this("", "", "")
}