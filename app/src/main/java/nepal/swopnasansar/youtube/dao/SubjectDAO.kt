package nepal.swopnasansar.youtube.dao

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import nepal.swopnasansar.youtube.dto.Subject

class SubjectDAO {
    private val TAG = "SubjectDAO"
    val db = Firebase.firestore
    val subjectRef = db.collection("subject")

    // get all subjects using teacher_key
    suspend fun getSubjectsByTeacherKey(key: String): ArrayList<Subject>? {
        var subjects: ArrayList<Subject>? = ArrayList()

        try {
            subjectRef.whereEqualTo("teacher", key)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (doc in querySnapshot) {
                        val subject = doc.toObject<Subject>()
                        subjects!!.add(subject)
                    }
                }
                .addOnFailureListener { e ->
                    subjects = null
                    Log.e(TAG, "Fail to get subjects.", e)
                }
                .await()

        }catch (e: Exception) {
            Log.e(TAG, "Exception !!", e)
        }

        return subjects
    }

    // update subject
    suspend fun updateSubject(subject: Subject): Boolean {
        var result = false

        try {
            subjectRef.document(subject.subject_key).set(subject)
                .addOnSuccessListener {
                    result = true
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Fail to update subject.", e)
                }
                .await()
        } catch (e: Exception) {
            Log.e(TAG, "Fail to update subject.", e)
        }

        return result
    }
}