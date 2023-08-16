package nepal.swopnasansar.dto

data class Homework(var homework_key: String, var teacher_key: String,
                    var class_key: String, var class_name: String,
                    var subject_key: String, var subject_name: String,
                    var date: String, var title: String, var content: String, var image: String,
                    var submitted_hw: ArrayList<SubmittedHW>) {
    constructor(): this("", "", "", "", "", "", "", "", "", "", ArrayList())
}