package nepal.swopnasansar.dao

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import nepal.swopnasansar.dto.Subject
import nepal.swopnasansar.dto.Youtube

class SubjectDAO {
    private val TAG = "SubjectDAO"
    val db = Firebase.firestore
    val subjectRef = db.collection("subject")

    suspend fun createSubject(subject: Subject): String? {
        return try {
            val documentRef = subjectRef.add(subject).await()
            documentRef.id
        } catch (exception: Exception) {
            Log.e(TAG, exception.toString())
            null
        }
    }

    suspend fun updateSubject(subjectKey: String, updateFields: Map<String, Any>): Boolean {
        return try {
            subjectRef.document(subjectKey).update(updateFields).await()
            true
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            false
        }
    }

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

    suspend fun addYoutube(subject:Subject, youtube: Youtube): Boolean {
        var result = false

        try {
            subjectRef.document(subject.subject_key)
                .update("youTube", FieldValue.arrayUnion(youtube))
                .addOnSuccessListener {
                    result = true
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Fail to add link.", e)
                }
                .await()

        } catch (e: Exception) {
            Log.e(TAG, "Fail to add link.", e)
        }

        return result
    }

    suspend fun removeYoutube(key: String, youtube: Youtube): Boolean {
        return try {
            subjectRef.document(key)
                .update("youTube", FieldValue.arrayRemove(youtube))
                .await()

            true
        } catch (e: Exception) {
            Log.e(TAG, "Fail to remove link.", e)
            false
        }
    }

    suspend fun removeSubject(key: String): Boolean {
        return try {
            subjectRef.document(key)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Fail to remove subject.", e)
            false
        }
    }
}