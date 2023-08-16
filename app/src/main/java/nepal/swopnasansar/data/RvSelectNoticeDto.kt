package nepal.swopnasansar.data

import java.io.Serializable

class RvSelectNoticeDto (var class_name : String, var subject_name : String, var student_name : String, var student_key : String) :
    Serializable {
    constructor() : this("", "", "", "") {
        // 필요한 경우 필드들을 초기화할 수도 있습니다.
    }
}