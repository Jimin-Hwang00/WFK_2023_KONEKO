package nepal.swopnasansar.dto

import java.io.Serializable

data class THWSubmitItem(var homeworkKey: String, var className: String, var subjectName: String, var stnKey: String, var stnName: String, var idx: Int?):
    Serializable {
}