package nepal.swopnasansar.login

import android.content.Intent
import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nepal.swopnasansar.*
import nepal.swopnasansar.dao.AuthDAO
import nepal.swopnasansar.databinding.ActivityResetPasswordBinding

class ResetPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResetPasswordBinding

    private lateinit var role: String

    private val authDao = AuthDAO()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        role = intent.getStringExtra("role").toString()

        binding.btnResetPw.setOnClickListener {
            val newPW = binding.evNewPw.text.toString()
            val reEnteredNewPW = binding.evCheckNewPw.text.toString()

            if (newPW.isNullOrBlank()) {
                Toast.makeText(this@ResetPasswordActivity, "Please write down the new password.", Toast.LENGTH_SHORT).show()
            } else if (reEnteredNewPW.isNullOrBlank()) {
                Toast.makeText(this@ResetPasswordActivity, "Please re-enter the new password for confirmation.", Toast.LENGTH_SHORT).show()
            } else if (newPW.length < 6){
                Toast.makeText(this@ResetPasswordActivity, "Password must be at least six characters long.", Toast.LENGTH_SHORT).show()
            } else {
                if (confirmPasswordMatch(newPW, reEnteredNewPW)) {
                    lifecycleScope.launch {
                        binding.pbResetPw.visibility = View.VISIBLE

                        val resetResult = withContext(Dispatchers.IO) {
                            authDao.resetPassword(newPW)
                        }

                        if (resetResult) {
                            Toast.makeText(applicationContext, "Success to reset password.", Toast.LENGTH_SHORT).show()

                            when (role) {
                                getString(R.string.administrator) -> {
                                    binding.pbResetPw.visibility = View.GONE

                                    val intent = Intent(this@ResetPasswordActivity, AdminMainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                                getString(R.string.accountant) -> {
                                    binding.pbResetPw.visibility = View.GONE

                                    val intent = Intent(this@ResetPasswordActivity, AccountantMainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                                getString(R.string.teacher) -> {
                                    binding.pbResetPw.visibility = View.GONE

                                    val intent = Intent(this@ResetPasswordActivity, TeacherMainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                                getString(R.string.student) -> {
                                    binding.pbResetPw.visibility = View.GONE

                                    val intent = Intent(this@ResetPasswordActivity, SPMainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                                else -> {
                                    binding.pbResetPw.visibility = View.GONE

                                    val intent = Intent(this@ResetPasswordActivity, CheckRoleActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                        } else {
                            Toast.makeText(this@ResetPasswordActivity, "Fail to reset password. Please try again.", Toast.LENGTH_SHORT).show()
                            binding.pbResetPw.visibility = View.GONE
                        }
                    }
                } else {
                    Toast.makeText(this@ResetPasswordActivity, "New password and re-entered password do not match.", Toast.LENGTH_SHORT).show()
                    binding.pbResetPw.visibility = View.GONE
                }
            }
        }
    }

    private fun confirmPasswordMatch(newPW: String, reEnteredNewPW: String): Boolean {
        return newPW == reEnteredNewPW
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}