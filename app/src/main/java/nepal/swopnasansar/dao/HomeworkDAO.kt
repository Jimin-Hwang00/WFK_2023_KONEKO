package nepal.swopnasansar.dao

import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import nepal.swopnasansar.dto.Homework
import nepal.swopnasansar.dto.SubmittedHW
import java.util.UUID

class HomeworkDAO {
    private val tag = "HomeworkDAO"

    private val db = Firebase.firestore
    private val homeworkRef = db.collection("homework")

    private val storage = Firebase.storage
    private val hwStorage = storage.reference.child("homework")

    suspend fun uploadHW(hw: Homework): String? {
        return try {
            val documentRef = homeworkRef.add(hw).await()
            documentRef.id
        } catch (exception: Exception) {
            Log.e(tag, exception.toString())
            null
        }
    }

    suspend fun submitHW(homeworkKey: String, submittedHW: SubmittedHW): Boolean {
        return try {
            homeworkRef.document(homeworkKey).update("submitted_hw", FieldValue.arrayUnion(submittedHW)).await()
            true
        } catch (exception: Exception) {
            Log.e(tag, exception.toString())
            false
        }
    }

    suspend fun updateHW(homeworkKey: String, updateFields: Map<String, Any>): Boolean {
        return try {
            homeworkRef.document(homeworkKey).update(updateFields).await()
            true
        } catch (e: Exception) {
            Log.e(tag, e.toString())
            false
        }
    }

    suspend fun uploadHWImageAndGetLink(uri: Uri): String? {
        return try {
            val uniqueFileName = "${UUID.randomUUID()}"

            val uploadTask = hwStorage.child(uniqueFileName).putFile(uri)
            val result = Tasks.await(uploadTask)
            val downloadUrl = result?.storage?.downloadUrl?.await()
            downloadUrl.toString()
        } catch (e: Exception) {
            Log.e(tag, e.toString())
            null
        }
    }

    suspend fun getHWbyHWKey(homeworkKey: String): Homework? {
        var homework: Homework? = null

        try {
            val documentSnapshot = homeworkRef.document(homeworkKey).get().await()

            if (documentSnapshot.exists()) {
                homework = documentSnapshot.toObject(Homework::class.java)
            } else {
                homework = Homework()
                homework.homework_key = "No Document"
            }
        } catch (e: Exception) {
            homework = null
            Log.e(tag, "Error getting homework", e)
        }

        return homework
    }

    suspend fun getHWbySubjectKey(subjectKey: String): ArrayList<Homework>? {
        var result: ArrayList<Homework>? = ArrayList()

        try {
            homeworkRef.whereEqualTo("subject_key", subjectKey).get()
                .addOnSuccessListener { documents ->
                    for (doc in documents) {
                        val item = doc.toObject(Homework::class.java)
                        result?.add(item)
                    }
                }
                .addOnFailureListener {
                    result = null
                    Log.e(tag, "Fail to get homework.", it)
                }
                .await()
        } catch (e: Exception) {
            result = null
            Log.e(tag, "Fail to get homework", e)
        }

        return result
    }

    suspend fun removeHWByHWKey(hw: Homework): Boolean {
        return try {
            homeworkRef.document(hw.homework_key).delete().await()
            Log.d(tag, "success to remove homework : ${hw.homework_key}")
            true
        } catch (e: Exception) {
            Log.d(tag, "fail to remove homework : ${hw.homework_key}")
            false
        }
    }

    suspend fun getHWbyTeacherKey(teacherKey: String): ArrayList<Homework>? {
        var result: ArrayList<Homework>? = ArrayList()

        try {
            homeworkRef.whereEqualTo("teacher_key", teacherKey).get()
                .addOnSuccessListener { documents ->
                    for (doc in documents) {
                        val item = doc.toObject(Homework::class.java)
                        result?.add(item)
                    }
                }
                .addOnFailureListener {
                    result = null
                    Log.e(tag, "Fail to get homework.", it)
                }
                .await()
        } catch (e: Exception) {
            result = null
            Log.e(tag, "Fail to get homework", e)
        }

        return result
    }

    suspend fun getHWbyClassKey(classKey: String): ArrayList<Homework>? {
        var result: ArrayList<Homework>? = ArrayList()

        try {
            homeworkRef.whereEqualTo("class_key", classKey).get()
                .addOnSuccessListener { documents ->
                    for (doc in documents) {
                        val item = doc.toObject(Homework::class.java)
                        result?.add(item)
                    }
                }
                .addOnFailureListener {
                    result = null
                    Log.e(tag, "Fail to get homework.", it)
                }
                .await()
        } catch (e: Exception) {
            result = null
            Log.e(tag, "Fail to get homework", e)
        }

        return result
    }
}