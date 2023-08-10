package nepal.swopnasansar.youtube.dto

data class Subject (var subject_key: String, var subject_name: String, var class_key: String, var teacher: String, var youTube: ArrayList<Youtube>) {
    constructor(): this("", "", "", "", ArrayList())
}