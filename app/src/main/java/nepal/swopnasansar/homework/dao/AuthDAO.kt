package nepal.swopnasansar.homework.dao

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthDAO {
    private val auth = FirebaseAuth.getInstance()

    fun getUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun getUid(): String? {
        return getUser()?.uid
    }
}