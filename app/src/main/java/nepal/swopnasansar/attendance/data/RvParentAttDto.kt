package nepal.swopnasansar.attendance.data

import java.io.Serializable

class RvParentAttDto  (var date : String, var check : String, var stn_name : String, var stn_key : String) :
    Serializable {
    constructor() : this("", "", "", "") {
        // 필요한 경우 필드들을 초기화할 수도 있습니다.
    }
}