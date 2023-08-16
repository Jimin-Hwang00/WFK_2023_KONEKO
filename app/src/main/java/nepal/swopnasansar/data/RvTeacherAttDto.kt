package nepal.swopnasansar.data

import java.io.Serializable

class RvTeacherAttDto (var class_name : String, var date : String, var stn_name : String, var stn_key : String, var check : String) :
    Serializable {
    constructor() : this("", "", "", "", "") {
        // 필요한 경우 필드들을 초기화할 수도 있습니다.
    }
}