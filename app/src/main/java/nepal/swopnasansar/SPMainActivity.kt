package nepal.swopnasansar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nepal.swopnasansar.accountant.SPCheckTuitionActivity
import nepal.swopnasansar.admin.CheckEventActivity
import nepal.swopnasansar.attendance.ParentAttendanceActivity
import nepal.swopnasansar.comment.SPCmntMainActivity
import nepal.swopnasansar.dao.AuthDAO
import nepal.swopnasansar.dao.StudentDAO
import nepal.swopnasansar.databinding.ActivitySpMainBinding
import nepal.swopnasansar.homework.SPCheckHWActivity
import nepal.swopnasansar.login.CheckRoleActivity
import nepal.swopnasansar.notice.ParentCheckNoticeActivity

class SPMainActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySpMainBinding

    private val authDao = AuthDAO()
    private val studentDao = StudentDAO()

    val uid = authDao.getUid()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sp_main)

        if (uid == null) {
            Toast.makeText(applicationContext, "You have to login.", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, CheckRoleActivity::class.java)
            startActivity(intent)
        }

        lifecycleScope.launch {
            binding.pbSpMain.visibility = View.VISIBLE

            val student = withContext(Dispatchers.IO) {
                studentDao.getStudentByKey(uid!!)
            }

            if(student != null) {
                binding.tvSpName.text = student.stn_name
            } else {
                binding.tvSpName.text = ""
            }

            binding.pbSpMain.visibility = View.INVISIBLE
        }

        binding.tvSPHW.setOnClickListener {
            val intent = Intent(this, SPCheckHWActivity::class.java)
            startActivity(intent)
        }

        binding.tvSPAttendance.setOnClickListener {
            val intent = Intent(this, ParentAttendanceActivity::class.java)
            startActivity(intent)
        }

        binding.tvSPNotice.setOnClickListener {
            val intent = Intent(this, ParentCheckNoticeActivity::class.java)
            startActivity(intent)
        }

        binding.tvSPCalendar.setOnClickListener {
            val intent = Intent(this, CheckEventActivity::class.java)
            startActivity(intent)
        }

        binding.tvSPTuition.setOnClickListener {
            val intent = Intent(this, SPCheckTuitionActivity::class.java)
            startActivity(intent)
        }

        binding.tvSPCmnt.setOnClickListener {
            val intent = Intent(this, SPCmntMainActivity::class.java)
            startActivity(intent)
        }

        binding.tvSPLogout.setOnClickListener {
            authDao.logout()

            val intent = Intent(this, CheckRoleActivity::class.java)
            startActivity(intent)
        }
    }
}