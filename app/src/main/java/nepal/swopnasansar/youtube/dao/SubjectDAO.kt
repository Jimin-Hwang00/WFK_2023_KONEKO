package nepal.swopnasansar.youtube.dao

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import nepal.swopnasansar.youtube.dto.Subject
import nepal.swopnasansar.youtube.dto.Youtube
import nepal.swopnasansar.youtube.dto.YoutubeListItem

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

    // add youtube link and title
    suspend fun addYoutube(subject: Subject,youtube: Youtube): Boolean {
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

    // remove youtube link and title
    suspend fun removeYoutube(key: String, youtube: Youtube): Boolean {
        var result = true

        try {
            subjectRef.document(key)
                .update("youTube", FieldValue.arrayRemove(youtube))
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
}