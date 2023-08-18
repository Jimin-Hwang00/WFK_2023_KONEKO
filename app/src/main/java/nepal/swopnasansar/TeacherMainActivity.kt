package nepal.swopnasansar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nepal.swopnasansar.admin.CheckEventActivity
import nepal.swopnasansar.attendance.TeacherAttendanceActivity
import nepal.swopnasansar.comment.TeacherCmntMainActivity
import nepal.swopnasansar.dao.AuthDAO
import nepal.swopnasansar.dao.TeacherDAO
import nepal.swopnasansar.databinding.ActivityTeacherMainBinding
import nepal.swopnasansar.homework.THWMainActivity
import nepal.swopnasansar.login.CheckRoleActivity
import nepal.swopnasansar.notice.TeacherNoticeMainActivity
import nepal.swopnasansar.youtube.TeacherCheckYoutbeListActivity

class TeacherMainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTeacherMainBinding

    private val authDao = AuthDAO()
    private val teacherDao = TeacherDAO()

    val uid = authDao.getUid()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeacherMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (uid == null) {
            val intent = Intent(this, CheckRoleActivity::class.java)
            startActivity(intent)
        } else {
            lifecycleScope.launch {
                binding.pbTeacherMain.visibility = View.VISIBLE

                val teacher = withContext(Dispatchers.IO) {
                    teacherDao.getTeacherByKey(uid!!)
                }

                if (teacher != null) {
                    binding.tvTeacherName.text = teacher.teacher_name
                } else {
                    binding.tvTeacherName.text = ""
                }

                binding.pbTeacherMain.visibility = View.INVISIBLE
            }
        }

        binding.tvTHW.setOnClickListener {
            val intent = Intent(this, THWMainActivity::class.java)
            startActivity(intent)
        }

        binding.arrowTHw.setOnClickListener {
            val intent = Intent(this, THWMainActivity::class.java)
            startActivity(intent)
        }

        binding.tvTAttendance.setOnClickListener {
            val intent = Intent(this, TeacherAttendanceActivity::class.java)
            startActivity(intent)
        }

        binding.arrowTAttendance.setOnClickListener {
            val intent = Intent(this, TeacherAttendanceActivity::class.java)
            startActivity(intent)
        }

        binding.tvTNotice.setOnClickListener {
           val intent = Intent(this, TeacherNoticeMainActivity::class.java)
            startActivity(intent)
        }

        binding.arrowTNotice.setOnClickListener {
            val intent = Intent(this, TeacherNoticeMainActivity::class.java)
            startActivity(intent)
        }

        binding.tvTCalendar.setOnClickListener {
            val intent = Intent(this, CheckEventActivity::class.java)
            startActivity(intent)
        }

        binding.arrowTCalendar.setOnClickListener {
            val intent = Intent(this, CheckEventActivity::class.java)
            startActivity(intent)
        }

        binding.tvTYoutube.setOnClickListener {
            val intent = Intent(this, TeacherCheckYoutbeListActivity::class.java)
            startActivity(intent)
        }

        binding.arrowTYoutube.setOnClickListener {
            val intent = Intent(this, TeacherCheckYoutbeListActivity::class.java)
            startActivity(intent)
        }

        binding.tvTCmnt.setOnClickListener {
            val intent = Intent(this, TeacherCmntMainActivity::class.java)
            startActivity(intent)
        }

        binding.arrowTCmnt.setOnClickListener {
            val intent = Intent(this, TeacherCmntMainActivity::class.java)
            startActivity(intent)
        }

        binding.tvTLogout.setOnClickListener {
            authDao.logout()

            val intent = Intent(this, CheckRoleActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        // 뒤로 가기 버튼 동작 없음
    }
}