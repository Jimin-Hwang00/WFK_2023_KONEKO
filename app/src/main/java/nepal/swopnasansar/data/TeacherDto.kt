package nepal.swopnasansar.data

import java.io.Serializable

class TeacherDto (var teacher_key : String, var teacher_name : String, var email : String) :
    Serializable {
    constructor() : this("", "", "") {
        // 필요한 경우 필드들을 초기화할 수도 있습니다.
    }
}