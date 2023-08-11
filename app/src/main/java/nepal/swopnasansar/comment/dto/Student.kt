package nepal.swopnasansar.comment.dto

data class Student(var stn_key: String, var stn_name: String, var email: String, var fee: String, var is_fee_paid: Boolean) {
    constructor(): this("", "", "", "", false) {}
}