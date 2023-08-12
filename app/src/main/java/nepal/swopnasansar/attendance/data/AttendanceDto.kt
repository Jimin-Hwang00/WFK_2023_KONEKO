package nepal.swopnasansar.attendance.data

import java.io.Serializable

class AttendanceDto (var attendance_key : String, var date : String, var stn_list : ArrayList<StnAttDto>) :
    Serializable {
    constructor() : this("", "", ArrayList()) {
        // 필요한 경우 필드들을 초기화할 수도 있습니다.
    }
}