package nepal.swopnasansar.comment.dto

data class ReceiverTarget(var category: String, var name: String, var key: String, var selected: Boolean) {
    constructor(): this("", "", "", false)

    override fun toString(): String {
        return "ReceiverTarget -> category: ${category}, name : ${name}, key: ${key}"
    }
}
