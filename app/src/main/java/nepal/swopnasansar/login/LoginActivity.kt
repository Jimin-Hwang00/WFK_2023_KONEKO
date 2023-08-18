package nepal.swopnasansar.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
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
    private val tempDao = TempDAO()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        role = intent.getStringExtra("role").toString()

        binding.loginBtn.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val pw = binding.pwEditText.text.toString()

            loginProcess(email, pw)
        }
    }

    fun loginProcess(email: String, pw: String) {
        lifecycleScope.launch {
            val roleCheckResult = withContext(Dispatchers.IO) {
                checkRole(role, email)
            }

            if (roleCheckResult) {
                val loginResult = withContext(Dispatchers.IO) {
                    authDao.login(email, pw)
                }

                if (loginResult) {       // 해당 role에 일치하는 이메일이 있는 경우
                    saveRoleForAutoLogin(role)          // 자동 로그인을 위한 role 저장

                    val isItFirstPW = checkIsItFirstPW(email, pw)

                    if (isItFirstPW) {          // 첫 로그인인 경우
                        askResetPWByDialog(email)
                    } else {                    // 첫 로그인이 아닌 경우
                        changePage()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Fail to login.", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {    // 해당 role에 일치하는 이메일이 없을 경우
                val temp = withContext(Dispatchers.IO) {
                    tempDao.getTempByEmail(email, role)
                }

                if (temp != null) {       // temp에 해당 계정이 있는 경우
                    if (temp.size == 1) {   // temp에 일치하는 개정이 한 개만 있을 경우
                        val loginResult = withContext(Dispatchers.IO) {
                            authDao.login(email, pw)
                        }

                        if (loginResult) {  // auth에 들록된 사용자
                            if (authDao.getUser()!!.isEmailVerified) {      // 이메일 인증이 완료된 경우
                                val removeTempResult = withContext(Dispatchers.IO) {
                                    tempDao.removeTempData(temp[0].email)
                                }

                                if (removeTempResult) {
                                    val uid = authDao.getUid()

                                    if (uid != null) {
                                        val createUserDBResult = withContext(Dispatchers.IO) {
                                            if (role.equals(getString(R.string.accountant))) {
                                                accountantDao.createAccountantByUid(uid!!, temp[0])
                                            } else if (role.equals(getString(R.string.teacher))) {
                                                teacherDao.createTeacherByUid(uid!!, temp[0])
                                            } else {
                                                studentDao.createStudentByUid(uid!!, temp[0])
                                            }
                                        }

                                        if (createUserDBResult) {
                                            saveRoleForAutoLogin(role)          // 자동 로그인을 위한 role 저장
                                            askResetPWByDialog(email)
                                        } else {
                                            val recreateTempResult = withContext(Dispatchers.IO) {
                                                tempDao.recreateTemp(temp[0])
                                            }

                                            if (!recreateTempResult) {
                                                Toast.makeText(this@LoginActivity, "An error has occurred. Please contact administrator.", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(this@LoginActivity, "An error has occurred. Please log in again.", Toast.LENGTH_SHORT).show()
                                            }

                                            authDao.logout()
                                        }
                                    } else {
                                        val recreateTempResult = withContext(Dispatchers.IO) {
                                            tempDao.recreateTemp(temp[0])
                                        }

                                        if (!recreateTempResult) {
                                            Toast.makeText(this@LoginActivity, "An error has occurred. Please contact administrator.", Toast.LENGTH_SHORT).show()
                                            Log.d("LoginActivity", "fail to recreate temp.")
                                        } else {
                                            Toast.makeText(this@LoginActivity, "An error has occurred. Please log in again.", Toast.LENGTH_SHORT).show()
                                        }

                                        authDao.logout()
                                    }
                                } else {
                                    Toast.makeText(this@LoginActivity, "An error has occurred. Please log in again.", Toast.LENGTH_SHORT).show()
                                    authDao.logout()
                                }
                            } else {                                // 이메일 인증이 안 된 경우
                                val sendEmailResult = withContext(Dispatchers.IO) {
                                    authDao.sendEmailForVerification()
                                }

                                if (sendEmailResult) {
                                    makeDialogforSendingVerificationEmail()
                                    authDao.logout()
                                } else {
                                    Toast.makeText(this@LoginActivity, "An error has occurred. Please log in again.", Toast.LENGTH_SHORT).show()
                                    authDao.logout()
                                }
                            }
                        } else {        // auth에 등록되지 않은 사용자인 경우
                            if (checkIsItFirstPW(email, pw)) {
                                val registerUserResult = withContext(Dispatchers.IO) {
                                    authDao.registerUser(email, pw)
                                }

                                if (registerUserResult) {
                                    val sendEmailResult = withContext(Dispatchers.IO) {
                                        authDao.sendEmailForVerification()
                                    }

                                    if (sendEmailResult) {
                                        makeDialogforSendingVerificationEmail()
                                    } else {
                                        Toast.makeText(this@LoginActivity, "An error has occurred. Please log in again.", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(this@LoginActivity, "An error has occurred. Please log in again.", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(this@LoginActivity, "Fail to login.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else if (temp.size == 0){
                        Toast.makeText(this@LoginActivity, "Fail to login.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@LoginActivity, "An error has occurred. Please contact administrator.", Toast.LENGTH_SHORT).show()
                    }
                } else {                // temp에 해당 계정이 없는 경우
                    Toast.makeText(this@LoginActivity, "Fail to login.", Toast.LENGTH_SHORT).show()
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

    fun makeDialogforSendingVerificationEmail() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.apply {
            setTitle("Verification Email")
            setMessage(getString(R.string.verification_email_dialog_message))
            setPositiveButton("OK") { dialog, which ->
                null
            }
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    fun changePage() {
        if (role.equals(getString(R.string.administrator))) {
            val intent = Intent(this@LoginActivity, AdminMainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            startActivity(intent)
        } else if (role.equals(getString(R.string.teacher))) {
            val intent = Intent(this@LoginActivity, TeacherMainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            startActivity(intent)
        } else if (role.equals(getString(R.string.accountant))) {
            val intent = Intent(this@LoginActivity, AccountantMainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            startActivity(intent)
        } else if (role.equals(getString(R.string.student))) {
            val intent = Intent(this@LoginActivity, SPMainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            startActivity(intent)
        }
    }
}