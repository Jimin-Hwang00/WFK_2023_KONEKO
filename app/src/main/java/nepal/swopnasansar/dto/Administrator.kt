package nepal.swopnasansar.dto

data class Administrator(var admin_key: String, var admin_name: String, var email: String) {
    constructor(): this("", "", "") {}
}