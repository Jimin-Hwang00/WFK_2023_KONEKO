package nepal.swopnasansar.homework.dto

data class Teacher(var teacher_key: String, var teacher_name: String, var email: String) {
    constructor(): this("", "", "")
}