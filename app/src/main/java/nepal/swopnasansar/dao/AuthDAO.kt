package nepal.swopnasansar.dao

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class AuthDAO {
    private val TAG = "AuthDAO"

    private val auth = FirebaseAuth.getInstance()
    suspend fun login(email: String, password: String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Log.d(TAG, "login success")
            true // 로그인 성공 시 true 반환
        } catch (e: Exception) {
            Log.d(TAG, e.toString())
            false // 로그인 실패 시 false 반환
        }
    }

    fun logout() {
        auth.signOut()
    }

    suspend fun sendPasswordResetEmail(email: String): Boolean {
        val user = getUser()

        if (user != null) {
            val sendEmailTask = auth.sendPasswordResetEmail(email)

            try {
                sendEmailTask.await()
                Log.d(TAG, "send password reset email success")
                return true
            } catch (e: Exception) {
                Log.d(TAG, "send password reset email fail")
            }
        }

        return false
    }

    fun getUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun getUid(): String? {
        return getUser()?.uid
    }

    suspend fun registerUser(email: String, pw: String): Boolean {
        try {
            auth.createUserWithEmailAndPassword(email, pw).await()
            return true
        }  catch (e: Exception) {
            Log.e(TAG, "Fail to create user.", e)
            return false
        }
    }

    suspend fun resetPassword(newPw: String): Boolean {
        val user = getUser()

        return try {
            user?.updatePassword(newPw)?.await()
            true
        } catch (exception: Exception) {
            Log.e(TAG, "Fail to reset password.", exception)
            false
        }
    }
}