package nepal.swopnasansar.dao

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import nepal.swopnasansar.dto.Accountant
import nepal.swopnasansar.dto.Teacher
import nepal.swopnasansar.dto.Temp

class TeacherDAO {
    private val TAG = "TeacherDAO"
    val db = Firebase.firestore
    val teacherRef = db.collection("teacher")

    suspend fun checkAccountByEmail(email: String): Boolean {
        try {
            val querySnapshot = teacherRef.whereEqualTo("email", email).get().await()

            if (!querySnapshot.isEmpty) {
                return true
            }
        } catch (e: Exception) {
            Log.e(TAG, "check account error", e)
        }

        return false
    }

    suspend fun getAllTeachers(): ArrayList<Teacher>? {
        var teachers: ArrayList<Teacher>? = ArrayList()

        try {
            val querySnapshot = teacherRef.get().await()

            for (doc in querySnapshot) {
                val teacher = doc.toObject(Teacher::class.java)
                Log.d(TAG, "teacher: ${teacher.teacher_key}")
                teachers?.add(teacher)
            }
        } catch (e: Exception) {
            teachers = null
            Log.e(TAG, "Error getting classes", e)
        }

        return teachers
    }

    suspend fun getTeacherByKey(teacherKey: String): Teacher? {
        var teacher: Teacher? = null

        try {
            val documentSnapshot = teacherRef.document(teacherKey).get().await()

            if (documentSnapshot.exists()) {
                teacher = documentSnapshot.toObject(Teacher::class.java)
            }
        } catch (e: Exception) {
            teacher = null
            Log.e(TAG, "Error getting student name", e)
        }

        return teacher
    }

    suspend fun createTeacherByUid(uid: String, temp: Temp): Boolean {
        return try {
            val teacher = Teacher(uid, temp.name, temp.email)
            teacherRef.document(uid).set(teacher).await()

            Log.e(TAG, "Success to create teacher by uid")

            true
        } catch (e: Exception) {
            Log.e(TAG, "Fail to create teacher by uid", e)
            false
        }
    }
}