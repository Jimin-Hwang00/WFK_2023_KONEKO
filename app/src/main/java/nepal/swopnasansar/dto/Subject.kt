package nepal.swopnasansar.dto

data class Subject(var subject_key: String, var subject_name: String, var teacher_key: String, var class_key: String, var youTube: ArrayList<Youtube>) {
    constructor(): this("", "", "", "", ArrayList())
}