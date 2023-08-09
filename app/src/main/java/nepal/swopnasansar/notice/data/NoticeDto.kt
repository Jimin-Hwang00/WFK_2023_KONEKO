package nepal.swopnasansar.notice.data

import java.io.Serializable

class NoticeDto (var notice_key : String, var content : String, var title : String,
                 var receiver_key : ArrayList<String>, var receiver_name : ArrayList<String>, var subject_key : String,
                 var author_key : String, var author_name : String) :
    Serializable {
    constructor() : this("", "", "", ArrayList(), ArrayList(), "", "", "") {
        // 필요한 경우 필드들을 초기화할 수도 있습니다.
    }
}