package nepal.swopnasansar.homework.dao

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import nepal.swopnasansar.homework.dto.Student

class StudentDAO {
    private val TAG = "StudentDAO"

    val db = Firebase.firestore
    val studentRef = db.collection("student")

    fun getAllStudent(): ArrayList<Student>? {
        var studentList: ArrayList<Student>? = ArrayList<Student>()
        studentRef.get()
            .addOnSuccessListener { querySnapshot ->
                for (doc in querySnapshot) {
                    val student: Student = doc.toObject(Student::class.java)
                    studentList?.add(student)
                }
            }
            .addOnFailureListener {
                studentList = null
                Log.d(TAG, it.toString())
            }
        return studentList
    }

    suspend fun getStudentByStnKey(studentKey: String): Student? {
        var student: Student? = null

        try {
            val documentSnapshot = studentRef.document(studentKey).get().await()

            if (documentSnapshot.exists()) {
                student = documentSnapshot.toObject(Student::class.java)
            }
        } catch (e: Exception) {
            student = null
            Log.e(TAG, "Error getting student.", e)
        }

        return student
    }

}