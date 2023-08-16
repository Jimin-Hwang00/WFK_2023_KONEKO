package nepal.swopnasansar.dto

data class CmntTargetItem(var category: String, var name: String, var key: String, var selected: Boolean) {
    constructor(): this("", "", "", false)
}
