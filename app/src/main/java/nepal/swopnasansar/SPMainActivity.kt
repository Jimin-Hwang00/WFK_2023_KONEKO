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
        binding = ActivitySpMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (uid == null) {
            val intent = Intent(this, CheckRoleActivity::class.java)
            startActivity(intent)
        } else {
            lifecycleScope.launch {
                binding.pbSpMain.visibility = View.VISIBLE

                val student = withContext(Dispatchers.IO) {
                    studentDao.getStudentByKey(uid!!)
                }

                if (student != null) {
                    binding.tvSpName.text = student.stn_name
                } else {
                    binding.tvSpName.text = ""
                }

                binding.pbSpMain.visibility = View.INVISIBLE
            }
        }


        binding.tvSPHW.setOnClickListener {
            val intent = Intent(this, SPCheckHWActivity::class.java)
            startActivity(intent)
        }

        binding.arrowSpHw.setOnClickListener {
            val intent = Intent(this, SPCheckHWActivity::class.java)
            startActivity(intent)
        }

        binding.tvSPAttendance.setOnClickListener {
            val intent = Intent(this, ParentAttendanceActivity::class.java)
            startActivity(intent)
        }

        binding.arrowSpAttendance.setOnClickListener {
            val intent = Intent(this, ParentAttendanceActivity::class.java)
            startActivity(intent)
        }

        binding.tvSPNotice.setOnClickListener {
            val intent = Intent(this, ParentCheckNoticeActivity::class.java)
            startActivity(intent)
        }

        binding.arrowSpNotice.setOnClickListener {
            val intent = Intent(this, ParentCheckNoticeActivity::class.java)
            startActivity(intent)
        }

        binding.tvSPCalendar.setOnClickListener {
            val intent = Intent(this, CheckEventActivity::class.java)
            startActivity(intent)
        }

        binding.arrowSpCalendar.setOnClickListener {
            val intent = Intent(this, CheckEventActivity::class.java)
            startActivity(intent)
        }

        binding.tvSPTuition.setOnClickListener {
            val intent = Intent(this, SPCheckTuitionActivity::class.java)
            startActivity(intent)
        }

        binding.arrowSpTuiton.setOnClickListener {
            val intent = Intent(this, SPCheckTuitionActivity::class.java)
            startActivity(intent)
        }

        binding.tvSPCmnt.setOnClickListener {
            val intent = Intent(this, SPCmntMainActivity::class.java)
            startActivity(intent)
        }

        binding.arrowSpCmnt.setOnClickListener {
            val intent = Intent(this, SPCmntMainActivity::class.java)
            startActivity(intent)
        }

        binding.tvSPLogout.setOnClickListener {
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
                                            studentDao.removeStudentByKey(uid!!)
                                        }

                                        if (deleteDBResult) {
                                            Toast.makeText(applicationContext, "Your account has been deleted.", Toast.LENGTH_LONG).show()
                                            val intent = Intent(this@SPMainActivity, CheckRoleActivity::class.java)
                                            startActivity(intent)
                                        }
                                    }
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this@SPMainActivity, "Fail to delete your account. Try again.", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this@SPMainActivity, "Authentication has failed.", Toast.LENGTH_SHORT).show()
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