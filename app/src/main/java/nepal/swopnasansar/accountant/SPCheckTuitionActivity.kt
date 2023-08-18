package nepal.swopnasansar.accountant

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nepal.swopnasansar.dao.AuthDAO
import nepal.swopnasansar.dao.StudentDAO
import nepal.swopnasansar.dto.Student
import nepal.swopnasansar.databinding.ActivitySpCheckTuitionBinding
import nepal.swopnasansar.login.CheckRoleActivity

class SPCheckTuitionActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySpCheckTuitionBinding

    var student: Student? = Student()

    private val studentDao = StudentDAO()
    private val authDao = AuthDAO()

    val uid = authDao.getUid()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpCheckTuitionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (uid == null) {
            Toast.makeText(applicationContext, "You have to login.", Toast.LENGTH_SHORT).show()

            val intent = Intent(this@SPCheckTuitionActivity, CheckRoleActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
    }

    private fun updateUI(student: Student) {
        binding.tvSpCheckTuitionStnName.text = student.stn_name
        binding.tvSpCheckTuition.text = student.fee
        if (student.is_fee_paid) {
            binding.tvSpCheckTuitionStatus.text = "O"
        } else {
            binding.tvSpCheckTuitionStatus.text = "X"
        }
    }

    override fun onResume() {
        lifecycleScope.launch(Dispatchers.IO) {
            student = withContext(Dispatchers.IO) {
                studentDao.getStudentByKey(uid!!)
            }

            withContext(Main) {
                if (student != null) {
                    updateUI(student!!)
                } else {
                    Toast.makeText(this@SPCheckTuitionActivity, "Fail to get tuition information. Try again.", Toast.LENGTH_SHORT).show()
                }
            }
        }
        super.onResume()
    }
}