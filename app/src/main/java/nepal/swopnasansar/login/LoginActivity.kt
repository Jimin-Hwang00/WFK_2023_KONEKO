package nepal.swopnasansar.login

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nepal.swopnasansar.R
import nepal.swopnasansar.databinding.ActivityLoginBinding
import nepal.swopnasansar.login.dao.*

class LoginActivity: AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private lateinit var role: String

    private val adminDao = AdminDAO()
    private val accountantDao = AccountantDAO()
    private val studentDao = StudentDAO()
    private val teacherDao = TeacherDAO()
    private val authDao = AuthDAO()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        role = intent.getStringExtra("role").toString()

        binding.loginBtn.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val pw =  binding.pwEditText.text.toString()

            lifecycleScope.launch {
                val checkRoleResult = withContext(Dispatchers.IO) {
                    checkRole(role, email)
                }

                if (checkRoleResult) {
                    val loginResult = withContext(Dispatchers.IO) {
                        authDao.login(email, pw)
                    }

                    if (loginResult) {
                        if (checkIsItFirstPW(email, pw)) {
                            withContext(Main) {
                                askResetPWByDialog(email)
                            }
                        }

                        withContext(Main) {
                            saveRoleForAutoLogin(role)

                            if (role.equals(getString(R.string.administrator))) {
                                // @TODO intent로 메인 페이지 이동
                            } else if (role.equals(getString(R.string.teacher))) {
                                // @TODO intent로 메인 페이지 이동
                            } else if (role.equals(getString(R.string.accountant))) {
                                // @TODO intent로 메인 페이지 이동
                            } else if (role.equals(getString(R.string.student))) {
                                // @TODO intent로 메인 페이지 이동
                            }
                        }
                    } else {
                        Toast.makeText(this@LoginActivity, "Fail to login. Try again.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    withContext(Main) {
                        Toast.makeText(this@LoginActivity, "Fail to login. Try again.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    fun saveRoleForAutoLogin(role: String) {
        val pref: SharedPreferences = getSharedPreferences("save_state", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = pref.edit()

        editor.putString("role", role)
        editor.apply()
    }

    suspend fun checkRole(role: String, email: String): Boolean {
        return withContext(Dispatchers.IO) {
            when (role) {
                getString(R.string.administrator) -> adminDao.checkAccountByEmail(email)
                getString(R.string.accountant) -> accountantDao.checkAccountByEmail(email)
                getString(R.string.teacher) -> teacherDao.checkAccountByEmail(email)
                getString(R.string.student) -> studentDao.checkAccountByEmail(email)
                else -> false
            }
        }
    }

    fun checkIsItFirstPW(email: String, pw: String): Boolean {
        val firstPW = email.substringBefore("@")
        return firstPW.equals(pw)
    }

    fun askResetPWByDialog(email: String) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.apply {
            setTitle(getString(R.string.set_new_password_title))
            setMessage(getString(R.string.set_new_password_message))
            setPositiveButton("YES") { dialog, which ->
                lifecycleScope.launch {
                    val sendEmailResult = withContext(Dispatchers.IO) {
                        authDao.sendPasswordResetEmail(email)
                    }

                    if (sendEmailResult) {
                        withContext(Main) {
                            Toast.makeText(this@LoginActivity, "Please check the email.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        withContext(Main) {
                            Toast.makeText(this@LoginActivity, "Fail to send email. Try again.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            setNegativeButton("NO") { dialog, which ->
                null
            }
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}