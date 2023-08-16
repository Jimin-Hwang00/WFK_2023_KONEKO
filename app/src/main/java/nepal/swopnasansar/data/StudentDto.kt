package nepal.swopnasansar.data

import java.io.Serializable

class StudentDto(var stn_key : String, var stn_name : String, var email : String, var fee : String, var is_fee_paid : Boolean) : Serializable {
    constructor() : this("", "", "", "", false) {
        // 필요한 경우 필드들을 초기화할 수도 있습니다.
    }
}