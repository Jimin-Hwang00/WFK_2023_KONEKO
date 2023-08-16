package nepal.swopnasansar.notice.data

import java.io.Serializable

class RvEntireNoticeDto (var class_name : String, var subject_name : String, var subject_key : String
, var studentKeyList : ArrayList<String>, var studentNameList : ArrayList<String>) :
    Serializable {
    constructor() : this("", "", "", ArrayList(), ArrayList()) {
        // 필요한 경우 필드들을 초기화할 수도 있습니다.
    }
}