package nepal.swopnasansar.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nepal.swopnasansar.R
import nepal.swopnasansar.dao.AccountantDAO
import nepal.swopnasansar.dao.StudentDAO
import nepal.swopnasansar.dao.TeacherDAO
import nepal.swopnasansar.dao.TempDAO
import nepal.swopnasansar.databinding.ActivitySignUpBinding
import nepal.swopnasansar.dto.Temp

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding

    private var role: String? = null

    private val accountantDao = AccountantDAO()
    private val teacherDao = TeacherDAO()
    private val studentDao = StudentDAO()
    private val tempDao = TempDAO()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 라디오 그룹 클릭 리스너 - 선택한 role 가져오기
        binding.rgSignUpRole.setOnCheckedChangeListener { group, checkId ->
            when (checkId) {
                R.id.rb_sign_up_accountant -> role = getString(R.string.accountant)
                R.id.rb_sign_up_teacher -> role = getString(R.string.teacher)
                R.id.rb_sign_up_student -> role = getString(R.string.student)
                else -> role = null
            }
        }

        // role check에서 선택한 역할 라디오 버튼에 반영
        when (intent.getStringExtra("role")) {
            getString(R.string.accountant) -> binding.rbSignUpAccountant.isChecked = true
            getString(R.string.teacher) -> binding.rbSignUpTeacher.isChecked = true
            getString(R.string.student) -> binding.rbSignUpStudent.isChecked = true
            else -> null
        }

        binding.btnSignUp.setOnClickListener {
            binding.pbSignUp.visibility = View.VISIBLE
            getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

            val name = binding.evSignUpName.text.toString()
            val email = binding.evSignUpEmail.text.toString()

            lifecycleScope.launch {
                if (role.isNullOrBlank()) {
                    Toast.makeText(this@SignUpActivity, "You have to choose role(Accountant, Teacher, Student) first.", Toast.LENGTH_LONG).show()
                    binding.pbSignUp.visibility = View.GONE
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                } else if (name.isNullOrBlank()) {
                    Toast.makeText(this@SignUpActivity, "Pleas write down your name.", Toast.LENGTH_LONG).show()
                    binding.pbSignUp.visibility = View.GONE
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                } else if (email.isNullOrBlank()) {
                    Toast.makeText(this@SignUpActivity, "Pleas write down your email address.", Toast.LENGTH_LONG).show()
                    binding.pbSignUp.visibility = View.GONE
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                } else if (email.substringBefore("@").length < 6) {
                    Toast.makeText(this@SignUpActivity, "* An email's portion before the \"@\" symbol must consist of at least six characters. ", Toast.LENGTH_LONG).show()
                    binding.pbSignUp.visibility = View.GONE
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }else {
                    if (isEmailValid(email)) {      // 이메일 형식이 맞을 때
                        val sameEmailResult = withContext(Dispatchers.IO) {
                            sameEmailExists(email)
                        }

                        if (!sameEmailResult) {
                            val createTempResult = withContext(Dispatchers.IO) {
                                tempDao.createTemp(Temp(email, name, role!!))
                            }

                            if (createTempResult) {
                                Toast.makeText(applicationContext, "You have successfully signed up.", Toast.LENGTH_LONG).show()
                                binding.pbSignUp.visibility = View.GONE
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                                val intent = Intent(this@SignUpActivity, CheckRoleActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(this@SignUpActivity, "Error has occurred. Please try again.", Toast.LENGTH_SHORT).show()
                                binding.pbSignUp.visibility = View.GONE
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            }
                        } else {
                            Toast.makeText(this@SignUpActivity, "This email already exists. Please use a different email.", Toast.LENGTH_LONG).show()
                            binding.pbSignUp.visibility = View.GONE
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        }
                    } else {
                        Toast.makeText(this@SignUpActivity, "The email format is not valid.", Toast.LENGTH_LONG).show()
                        binding.pbSignUp.visibility = View.GONE
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    }
                }
            }
        }
    }

    fun isEmailValid(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})\$".toRegex()
        return email.matches(emailRegex)
    }

    suspend fun sameEmailExists(email: String): Boolean {
        return withContext(Dispatchers.IO) {
            val accountantResult = accountantDao.checkAccountByEmail(email)
            val teacherResult = teacherDao.checkAccountByEmail(email)
            val studentResult = studentDao.checkAccountByEmail(email)
            val tempResult = tempDao.checkTempByEmail(email)

            accountantResult || teacherResult || studentResult || tempResult
        }
    }
}