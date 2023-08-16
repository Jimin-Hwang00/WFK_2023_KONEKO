package nepal.swopnasansar.data

import java.io.Serializable

class StnAttDto (var stn_key : String, var stn_name : String, var check : Boolean) :
    Serializable {
    constructor() : this("", "", false) {
        // 필요한 경우 필드들을 초기화할 수도 있습니다.
    }
}