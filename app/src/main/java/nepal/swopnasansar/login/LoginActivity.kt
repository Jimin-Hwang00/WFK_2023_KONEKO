package nepal.swopnasansar.login

import android.content.Context
import android.content.Intent
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
import nepal.swopnasansar.*
import nepal.swopnasansar.dao.AccountantDAO
import nepal.swopnasansar.databinding.ActivityLoginBinding
import nepal.swopnasansar.dao.*

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
                        } else {
                            changePage()
                        }

                        withContext(Main) {
                            saveRoleForAutoLogin(role)
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
                            Toast.makeText(applicationContext, "An email has been sent to update your password. Please check the email.", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        withContext(Main) {
                            Toast.makeText(applicationContext, "Fail to send email. Try again.", Toast.LENGTH_LONG).show()
                        }
                    }

                    changePage()
                }
            }
            setNegativeButton("NO") { dialog, which ->
                dialog.dismiss()
                changePage()
            }
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    fun changePage() {
        if (role.equals(getString(R.string.administrator))) {
            val intent = Intent(this@LoginActivity, AdminMainActivity::class.java)
            startActivity(intent)
        } else if (role.equals(getString(R.string.teacher))) {
            val intent = Intent(this@LoginActivity, TeacherMainActivity::class.java)
            startActivity(intent)
        } else if (role.equals(getString(R.string.accountant))) {
            val intent = Intent(this@LoginActivity, AccountantMainActivity::class.java)
            startActivity(intent)
        } else if (role.equals(getString(R.string.student))) {
            val intent = Intent(this@LoginActivity, SPMainActivity::class.java)
            startActivity(intent)
        }
    }
}