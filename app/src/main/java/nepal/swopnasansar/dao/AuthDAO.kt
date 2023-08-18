package nepal.swopnasansar.dao

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import kotlin.concurrent.timerTask

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

    suspend fun sendEmailForVerification(): Boolean {
        return try {
            val user = getUser()

            if (user != null) {
                user.sendEmailVerification().await()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
    suspend fun registerUser(email: String, pw: String): Boolean {
        return try {
            auth.createUserWithEmailAndPassword(email, pw).await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Fail to create user.", e)
            false
        }
    }

    suspend fun removeUser(): Boolean {
        return try {
            if (getUser() != null) {
                getUser()!!.delete()
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Fail to remove user : ${getUid()}", e)
            false
        }
    }
}