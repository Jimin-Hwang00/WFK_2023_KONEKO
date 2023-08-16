package nepal.swopnasansar.data

import java.io.Serializable

class RvParentNoticeDto (var title : String, var content : String, var notice_key : String, var receiver_key : String, var subject_name : String) :
    Serializable {
    constructor() : this("", "", "", "", "") {
        // 필요한 경우 필드들을 초기화할 수도 있습니다.
    }
}