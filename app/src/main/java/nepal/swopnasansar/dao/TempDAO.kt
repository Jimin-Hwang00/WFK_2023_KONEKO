package nepal.swopnasansar.dao

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import nepal.swopnasansar.dto.*

class TempDAO {
    private val TAG = "TempDAO"

    private val db = Firebase.firestore
    private val tempRef = db.collection("temp")

    suspend fun checkTempByEmail(email: String): Boolean {
        return try {
            val querySnapshot = tempRef.whereEqualTo("email", email).get().await()
            !querySnapshot.isEmpty
        } catch (e: Exception) {
            Log.e(TAG, "check account error", e)
            return false
        }
    }

    suspend fun getTempByEmail(email: String, role: String): ArrayList<Temp>? {
        var result: ArrayList<Temp>? = ArrayList()

        try {
            tempRef.whereEqualTo("email", email)
                .whereEqualTo("role", role)
                .get()
                .addOnSuccessListener { documents ->
                    for (doc in documents) {
                        val item = doc.toObject(Temp::class.java)
                        result?.add(item)
                    }
                }
                .addOnFailureListener {
                    result = null
                    Log.e(TAG, "Fail to get temp data", it)
                }
                .await()
        } catch (e: Exception) {
            result = null
            Log.e(TAG, "Fail to get temp data", e)
        }

        return result
    }

    suspend fun removeTempData(email: String): Boolean {
        return try {
            tempRef.document(email).delete().await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Fail to remove temp data : ${email}", e)
            false
        }
    }

    suspend fun createTemp(temp: Temp): Boolean {
        return try {
            tempRef.document(temp.email).set(temp).await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Fail to create temp data : ${temp.email}", e)
            false
        }
    }
 }