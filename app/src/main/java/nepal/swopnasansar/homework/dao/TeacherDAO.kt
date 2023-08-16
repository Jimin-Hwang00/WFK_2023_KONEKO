package nepal.swopnasansar.homework.dao

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import nepal.swopnasansar.homework.dto.Class
import nepal.swopnasansar.homework.dto.Student
import nepal.swopnasansar.homework.dto.Teacher

class TeacherDAO {
    private val TAG = "TeacherDAO"
    val db = Firebase.firestore
    val teacherRef = db.collection("teacher")

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
}