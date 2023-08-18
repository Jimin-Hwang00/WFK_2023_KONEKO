package nepal.swopnasansar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.EmailAuthProvider
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
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_signout, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.item_sign_out -> {
                val dialog = SignoutDialog(this)
                dialog.setOnClickListener(object: SignoutDialog.ButtonClickListener {
                    override fun onClicked(email: String, pw: String) {
                        val user = authDao.getUser()

                        if (user != null) {
                            val credential = EmailAuthProvider.getCredential(email, pw)
                            user.reauthenticate(credential)
                                .addOnSuccessListener {
                                    user.delete()

                                    lifecycleScope.launch {
                                        val deleteDBResult = withContext(Dispatchers.IO) {
                                            teacherDao.removeTeacherByKey(uid!!)
                                        }

                                        if (deleteDBResult) {
                                            Toast.makeText(applicationContext, "Your account has been deleted.", Toast.LENGTH_LONG).show()
                                            val intent = Intent(this@TeacherMainActivity, CheckRoleActivity::class.java)
                                            startActivity(intent)
                                        }
                                    }
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this@TeacherMainActivity, "Fail to delete your account. Try again.", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this@TeacherMainActivity, "Authentication has failed.", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                })
                dialog.showDialog()

                true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        // 뒤로 가기 버튼 동작 없음
    }
}