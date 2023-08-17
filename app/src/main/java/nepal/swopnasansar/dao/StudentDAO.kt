package nepal.swopnasansar.dao

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import nepal.swopnasansar.dto.Student

class StudentDAO {
    private val TAG = "StudentDAO"

    val db = Firebase.firestore
    val studentRef = db.collection("student")

    suspend fun checkAccountByEmail(email: String): Boolean {
        try {
            val querySnapshot = studentRef.whereEqualTo("email", email).get().await()

            if (!querySnapshot.isEmpty) {
                return true
            }
        } catch (e: Exception) {
            Log.e(TAG, "check account error", e)
        }

        return false
    }

    suspend fun getAllStudents(): ArrayList<Student>? {
        var studentList: ArrayList<Student>? = null

        try {
            val querySnapshot = studentRef.get().await()
            studentList = ArrayList()

            for (doc in querySnapshot) {
                val student: Student = doc.toObject(Student::class.java)
                studentList!!.add(student)
            }
        } catch (e: Exception) {
            Log.d(TAG, e.toString())
        }

        return studentList
    }

    suspend fun getStudentByKey(studentKey: String): Student? {
        var student: Student? = null

        try {
            val documentSnapshot = studentRef.document(studentKey).get().await()

            if (documentSnapshot.exists()) {
                student = documentSnapshot.toObject(Student::class.java)
            }

            Log.d(TAG, "student : ${student}")
        } catch (e: Exception) {
            student = null
            Log.e(TAG, "Error getting student name", e)
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