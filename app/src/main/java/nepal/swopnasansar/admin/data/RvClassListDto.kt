package nepal.swopnasansar.admin.data

import java.io.Serializable

class RvClassListDto(var class_name : String, var teacher_name : String, var subject : String, var class_key : String, var subject_key : String) :
    Serializable {
    constructor() : this("", "", "", "", "") {
        // 필요한 경우 필드들을 초기화할 수도 있습니다.
    }
}