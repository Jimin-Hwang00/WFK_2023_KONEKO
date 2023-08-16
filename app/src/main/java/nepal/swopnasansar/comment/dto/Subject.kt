package nepal.swopnasansar.comment.dto

import nepal.swopnasansar.youtube.dto.Youtube

data class Subject(var subject_key: String, var subject_name: String, var teacher_key: String, var class_key: String, var YouTube: ArrayList<Youtube>) {
    constructor(): this("", "", "", "", ArrayList())
}