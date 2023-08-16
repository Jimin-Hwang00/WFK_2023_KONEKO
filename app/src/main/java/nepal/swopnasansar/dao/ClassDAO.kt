package nepal.swopnasansar.dao

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import nepal.swopnasansar.dto.Class

class ClassDAO {
    private val TAG = "ClassDAO"

    val db = Firebase.firestore
    val classRef = db.collection("class")

    suspend fun getAllClasses(): ArrayList<Class>? {
        var classes: ArrayList<Class>? = ArrayList()

        try {
            val querySnapshot = classRef.get().await()

            for (doc in querySnapshot) {
                val classDoc = doc.toObject(Class::class.java)
                Log.d(TAG, "getAllClasses: ${classDoc.class_key}")
                classDoc.student_key.forEach {
                    Log.d(TAG, "studentKey: ${it}")
                }
                classes?.add(classDoc)
            }

            Log.d(TAG, "classes size : ${classes?.size}")
        } catch (e: Exception) {
            classes = null
            Log.e(TAG, "Error getting classes", e)
        }

        return classes
    }

    suspend fun getClassByStudentKey(key: String): ArrayList<Class>? {
        var result: ArrayList<Class>? = ArrayList()

        try {
            classRef.whereArrayContains("student_key", key).get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val item = document.toObject(Class::class.java)
                        result?.add(item)
                    }
                }
                .addOnFailureListener {
                    result = null
                    Log.e(TAG, "Fail to get class.", it)
                }
                .await()

        } catch (e: Exception) {
            result = null
            Log.e(TAG, "Fail to get class.", e)
        }

        return result
    }

    suspend fun getClassByClassKey(key: String): nepal.swopnasansar.homework.dto.Class? {
        val querySnapshot = classRef.document(key).get().await()

        if (querySnapshot.exists()) {
            val result = querySnapshot.toObject(nepal.swopnasansar.homework.dto.Class::class.java)
            Log.d(TAG, "get class by class key result : ${result!!.class_key}")
            return result
        } else {
            Log.d(TAG, "querysnapshot doesn't exist")
            return null
        }
    }
}