package nepal.swopnasansar.dao

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import nepal.swopnasansar.R
import nepal.swopnasansar.dto.Accountant
import nepal.swopnasansar.dto.Class
import nepal.swopnasansar.dto.Temp

class AccountantDAO {
    private val TAG = "AccountantDAO"

    private val db = Firebase.firestore
    private val accountantRef = db.collection("accountant")

    suspend fun checkAccountByEmail(email: String): Boolean {
        try {
            val querySnapshot = accountantRef.whereEqualTo("email", email).get().await()

            if (!querySnapshot.isEmpty) {
                Log.d(TAG, "account exits")
                return true
            }
        } catch (e: Exception) {
            Log.e(TAG, "check account error", e)
        }

        return false
    }

    suspend fun getAccountantByKey(key: String): Accountant? {
        val querySnapshot = accountantRef.document(key).get().await()

        if (querySnapshot.exists()) {
            val result = querySnapshot.toObject(Accountant::class.java)
            return result
        } else {
            Log.d(TAG, "query doesn't exist")
            return null
        }
    }

    suspend fun createAccountantByUid(uid: String, temp: Temp): Boolean {
        return try {
            val accountant = Accountant(uid, temp.name, temp.email)
            accountantRef.document(uid).set(accountant).await()

            Log.e(TAG, "Success to create accountant by uid")

            true
        } catch (e: Exception) {
            Log.e(TAG, "Fail to create accountant by uid", e)
            false
        }
    }
}