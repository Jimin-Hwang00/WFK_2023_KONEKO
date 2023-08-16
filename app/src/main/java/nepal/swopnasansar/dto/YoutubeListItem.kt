package nepal.swopnasansar.dto

data class YoutubeListItem(var subject_key: String, var subject: String, var title: String, var url: String) {
    constructor(): this("", "","", "")
}