package nepal.swopnasansar.homework.dao

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import nepal.swopnasansar.homework.dto.Subject

class SubjectDAO {
    private val TAG = "SubjectDAO"
    val db = Firebase.firestore
    val subjectRef = db.collection("subject")

    suspend fun getAllSubejct(): ArrayList<Subject>? {
        var subjects: ArrayList<Subject>? = ArrayList()

        try {
            val querySnapshot = subjectRef.get().await()

            for (doc in querySnapshot) {
                val subject = doc.toObject(Subject::class.java)
                subjects?.add(subject)
            }
        } catch (e: Exception) {
            subjects = null
            Log.e(TAG, "Error getting admins", e)
        }

        return subjects
    }

    suspend fun getSubjectByTeacherKey(key: String): ArrayList<Subject>? {
        var subjects: ArrayList<Subject>? = ArrayList()

        try {
            subjectRef.whereEqualTo("teacher_key", key).get()
                .addOnSuccessListener { document ->
                    for (doc in document) {
                        val subject = doc.toObject(Subject::class.java)
                        Log.d(TAG, "subject key: ${subject.subject_key}")
                        subjects?.add(subject)
                    }
                }
                .addOnFailureListener {
                    subjects = null
                    Log.e(TAG, "Fail to get subject.", it)
                }
                .await()
        } catch (e: Exception) {
            subjects = null
            Log.e(TAG, "Fail to get subject.", e)
        }

        return subjects
    }

    suspend fun getSubjectByClassKey(classKey: String): ArrayList<Subject>? {
        var subjects: ArrayList<Subject>? = ArrayList()

        try {
            subjectRef.whereEqualTo("class_key", classKey).get()
                .addOnSuccessListener { document ->
                    for (doc in document) {
                        val subject = doc.toObject(Subject::class.java)
                        Log.d(TAG, "subject key: ${subject.subject_key}")
                        subjects?.add(subject)
                    }
                }
                .addOnFailureListener {
                    subjects = null
                    Log.e(TAG, "Fail to get subject.", it)
                }
                .await()
        } catch (e: Exception) {
            subjects = null
            Log.e(TAG, "Fail to get subject.", e)
        }

        return subjects
    }
}