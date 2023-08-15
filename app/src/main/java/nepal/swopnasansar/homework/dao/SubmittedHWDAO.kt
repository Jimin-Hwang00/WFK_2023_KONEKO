package nepal.swopnasansar.homework.dao

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import nepal.swopnasansar.homework.dto.Student
import nepal.swopnasansar.homework.dto.SubmittedHW

class SubmittedHWDAO {
    private val TAG = "SubmittedHWDAO"

    val db = Firebase.firestore
    val submittedHWRef = db.collection("submitted_hw")

    suspend fun getSubmittedHWBySubmittedHWKey(submittedHWKey: String): SubmittedHW? {
        var submittedHW: SubmittedHW? = null

        try {
            val documentSnapshot = submittedHWRef.document(submittedHWKey).get().await()

            if (documentSnapshot.exists()) {
                submittedHW = documentSnapshot.toObject(SubmittedHW::class.java)
            }
        } catch (e: Exception) {
            submittedHW = null
            Log.e(TAG, "Error getting submitted homework.", e)
        }

        return submittedHW
    }

    suspend fun getSubmittedHWByHWKey(homeworkKey: String): ArrayList<SubmittedHW>? {
        var result: ArrayList<SubmittedHW>? = ArrayList()

        try {
            submittedHWRef.whereEqualTo("homework_key", homeworkKey).get()
                .addOnSuccessListener { documents ->
                    for (doc in documents) {
                        val submittedHW = doc.toObject(SubmittedHW::class.java)
                        result?.add(submittedHW)
                    }
                }
                .addOnFailureListener {
                    Log.e(TAG, "Fail to get submitted homework.", it)
                }
                .await()
        } catch (e: Exception) {
            Log.e(TAG, "Fail to get submitted homework.", e)
        }

        return result
    }

    suspend fun getSubmittedHWByHWKeyAndStnKey(homeworkKey: String, studentKey: String): ArrayList<SubmittedHW>? {
        var result: ArrayList<SubmittedHW>? = ArrayList()

        try {
            submittedHWRef
                .whereEqualTo("homework_key", homeworkKey)
                .whereEqualTo("stn_key", studentKey)
                .get()
                .addOnSuccessListener { documents ->
                    for (doc in documents) {
                        val submittedHW = doc.toObject(SubmittedHW::class.java)
                        result?.add(submittedHW)
                    }
                }
                .addOnFailureListener {
                    Log.e(TAG, "Fail to get submitted homework.", it)
                }
                .await()
        } catch (e: Exception) {
            Log.e(TAG, "Fail to get submitted homework.", e)
        }

        return result
    }

}