package nepal.swopnasansar.accountant.dao

import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import nepal.swopnasansar.accountant.dto.Student

class StudentDAO {
    private val TAG = "StudentDAO"

    val db = Firebase.firestore
    val studentRef = db.collection("student")

    suspend fun getAllStudent(): ArrayList<Student>? {
        var studentList: ArrayList<Student>? = ArrayList<Student>()

        try {
            val querySnapshot = studentRef.get().await()

            for (doc in querySnapshot) {
                val studentItem = doc.toObject(Student::class.java)
                studentList?.add(studentItem)
            }
        } catch (e: Exception) {
            studentList = null
            Log.e(TAG, "Error getting students", e)
        }

        return studentList
    }

    suspend fun getStudentByKey(key: String): Student? {
        var student: Student? = Student()

        try {
            val querySnapshot = studentRef.document(key).get().await()

            if (querySnapshot.exists() && querySnapshot != null) {
                student = querySnapshot.toObject(Student::class.java)!!
            } else {
                student = null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting student information", e)
        }

        return student
    }

    suspend fun updateStudentByKey(student: Student): Boolean {
        var result = false

        try {
            studentRef.document(student.stn_key).set(student)
                .addOnSuccessListener {
                    Log.d(TAG, "update student success")
                    result = true
                }
                .addOnFailureListener {
                    Log.d(TAG, "update student fail")
                }
                .await()

            return result
        } catch (e: Exception) {
            Log.e(TAG, "Error updating students", e)
            return false
        }
    }

}