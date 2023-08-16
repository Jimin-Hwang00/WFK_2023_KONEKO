package nepal.swopnasansar.admin.data

import java.io.Serializable

class SubjectDto (var subject_key: String, var subject_name: String, var teacher_key: String, var class_key: String, var YouTube: ArrayList<Youtube>) :
    Serializable {
    constructor() : this("", "", "", "", ArrayList()) {
        // 필요한 경우 필드들을 초기화할 수도 있습니다.
    }
}