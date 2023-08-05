package nepal.swopnasansar.admin.data

import java.io.Serializable

class ClassDto (var class_key : String, var class_name : String, var student_key : ArrayList<String>, var teacher_key : String) :
    Serializable {
    constructor() : this("", "", ArrayList(), "") {
        // 필요한 경우 필드들을 초기화할 수도 있습니다.
    }
}