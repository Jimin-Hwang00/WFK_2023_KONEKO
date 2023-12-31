package nepal.swopnasansar.dao

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import nepal.swopnasansar.R
import nepal.swopnasansar.dto.Accountant
import nepal.swopnasansar.dto.Class
import nepal.swopnasansar.dto.Teacher
import nepal.swopnasansar.dto.Temp

class AccountantDAO {
    private val TAG = "AccountantDAO"

    private val db = Firebase.firestore
    private val accountantRef = db.collection("accountant")

    suspend fun checkAccountByEmail(email: String): Boolean {
        return try {
            val querySnapshot = accountantRef.whereEqualTo("email", email).get().await()
            !querySnapshot.isEmpty
        } catch (e: Exception) {
            Log.e(TAG, "check account error", e)
            return false
        }
    }

    suspend fun getAccountantByKey(key: String): Accountant? {return try {
            val querySnapshot = accountantRef.document(key).get().await()

            if (querySnapshot.exists()) {
                val result = querySnapshot.toObject(Accountant::class.java)
                return result
            } else {
                return Accountant()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting accountant", e)
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

    suspend fun removeAccountantByKey(key: String): Boolean {
        return try {
            accountantRef.document(key).delete().await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Fail to remove accountant : $key")
            false // 삭제에 실패한 경우 false 반환
        }
    }
}