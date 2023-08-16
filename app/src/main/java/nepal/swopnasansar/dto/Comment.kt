package nepal.swopnasansar.dto

data class Comment(var comment_key: String, var title: String, var content: String, var date: String, var author_key: String, var author_name: String, var receiver_key: String, var receiver_name: String, var read: Boolean) {
    constructor() : this ("", "", "", "", "", "", "", "", false) {}

    fun mapToObject(map: HashMap<String, Any?>): Comment {
        val comment = Comment()

        comment.comment_key = map["comment_key"] as? String ?: ""
        comment.title = map["title"] as? String ?: ""
        comment.content = map["content"] as? String ?: ""
        comment.date = map["date"] as? String ?: ""
        comment.author_key = map["author_key"] as? String ?: ""
        comment.author_name = map["author_name"] as? String?: ""
        comment.receiver_key = map["receiver_key"] as? String?: ""
        comment.receiver_name = map["receiver_name"] as? String?: ""
        comment.read = map["read"] as? Boolean ?: false

        return comment
    }
}