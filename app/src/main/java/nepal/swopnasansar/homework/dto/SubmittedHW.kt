package nepal.swopnasansar.homework.dto

data class SubmittedHW(var homework_key: String, var stn_key: String, var title: String, var content: String) {
    constructor(): this("", "", "", "")
}