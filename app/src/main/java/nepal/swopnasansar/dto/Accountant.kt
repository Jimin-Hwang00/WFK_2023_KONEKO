package nepal.swopnasansar.dto

data class Accountant(var accountant_key: String, var accountant_name: String, var email: String) {
    constructor(): this("", "", "")
}