package nepal.swopnasansar.dao

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import nepal.swopnasansar.dto.Administrator

class AdminDAO {
    private val TAG = "AdminDAO"
    val db = Firebase.firestore
    val adminRef = db.collection("administrator")

    suspend fun getAllAdmin(): ArrayList<Administrator>? {
        var admins: ArrayList<Administrator>? = ArrayList()

        try {
            val querySnapshot = adminRef.get().await()

            for (doc in querySnapshot) {
                val admin = doc.toObject(Administrator::class.java)
                Log.d(TAG, "admin: ${admin.admin_key}")
                admins?.add(admin)
            }
        } catch (e: Exception) {
            admins = null
            Log.e(TAG, "Error getting admins", e)
        }

        return admins
    }

    suspend fun checkAccountByEmail(email: String): Boolean {
        try {
            val querySnapshot = adminRef.whereEqualTo("email", email).get().await()

            if (!querySnapshot.isEmpty) {
                return true
            }
        } catch (e: Exception) {
            Log.e(TAG, "check account error", e)
        }

        return false
    }
}