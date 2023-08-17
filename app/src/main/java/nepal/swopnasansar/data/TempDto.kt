package nepal.swopnasansar.data

import java.io.Serializable

class TempDto (var email : String, var name : String, var role : String) :
    Serializable {
    constructor() : this("", "", "") {
        // 필요한 경우 필드들을 초기화할 수도 있습니다.
    }
}