package nepal.swopnasansar.login.dao

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class TeacherDAO {
    private val TAG = "TeacherDAO"

    private val db = Firebase.firestore
    private val accountantRef = db.collection("teacher")

    suspend fun checkAccountByEmail(email: String): Boolean {
        try {
            val querySnapshot = accountantRef.whereEqualTo("email", email).get().await()

            if (!querySnapshot.isEmpty) {
                return true
            }
        } catch (e: Exception) {
            Log.e(TAG, "check account error", e)
        }

        return false
    }
}