package nepal.swopnasansar.accountant.dto

data class Student(var stn_key: String, var stn_name: String, var email: String, var fee: String, var is_fee_paid: Boolean) {
    constructor(): this("", "", "", "", false)

    fun isEqual(other: Student): Boolean {
        return this.fee == other.fee && this.is_fee_paid == other.is_fee_paid
    }
}