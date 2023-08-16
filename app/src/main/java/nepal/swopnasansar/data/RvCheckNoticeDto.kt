package nepal.swopnasansar.data

import java.io.Serializable

class RvCheckNoticeDto (var title : String, var content : String, var studentNameList : ArrayList<String>, var notice_key : String) :
    Serializable {
    constructor() : this("", "", ArrayList(), "") {
        // 필요한 경우 필드들을 초기화할 수도 있습니다.
    }
}