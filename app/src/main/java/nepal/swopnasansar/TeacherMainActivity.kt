package nepal.swopnasansar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nepal.swopnasansar.admin.UserCheckEventActivity
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
            val intent = Intent(this, UserCheckEventActivity::class.java)
            startActivity(intent)
        }

        binding.arrowTCalendar.setOnClickListener {
            val intent = Intent(this, UserCheckEventActivity::class.java)
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
            askForLogOut()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_signout, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.item_sign_out -> {
                val dialog = SignOutDialog(this)
                dialog.setOnClickListener(object: SignOutDialog.ButtonClickListener {
                    override fun onClicked(email: String, pw: String) {
                        if (email.isNotBlank() || pw.isNotBlank())  {
                            val user = authDao.getUser()

                            if (user != null) {
                                try {
                                    lifecycleScope.launch {
                                        binding.pbTeacherMain.visibility = View.VISIBLE

                                        val credential = EmailAuthProvider.getCredential(email, pw)
                                        user.reauthenticate(credential)
                                            .addOnSuccessListener {
                                                CoroutineScope(Dispatchers.IO).launch {
                                                    val deleteDBResult = withContext(Dispatchers.IO) {
                                                        teacherDao.removeTeacherByKey(uid!!)
                                                    }

                                                    withContext(Dispatchers.Main) {
                                                        if (deleteDBResult) {
                                                            user.delete()
                                                                .addOnSuccessListener {
                                                                    Toast.makeText(applicationContext, "Your account has been deleted.", Toast.LENGTH_LONG).show()
                                                                    binding.pbTeacherMain.visibility = View.GONE
                                                                    val intent = Intent(this@TeacherMainActivity, CheckRoleActivity::class.java)
                                                                    startActivity(intent)
                                                                }
                                                                .addOnFailureListener {
                                                                    Toast.makeText(this@TeacherMainActivity, "An error has occurred. Please contact administrator", Toast.LENGTH_LONG).show()
                                                                    binding.pbTeacherMain.visibility = View.GONE
                                                                }
                                                        } else {
                                                            Toast.makeText(this@TeacherMainActivity, "An error has occurred. Please contact administrator.", Toast.LENGTH_SHORT).show()
                                                            binding.pbTeacherMain.visibility = View.GONE
                                                        }
                                                    }
                                                }

                                            }
                                            .addOnFailureListener {
                                                Toast.makeText(this@TeacherMainActivity, "Email or password is incorrect. Please check again.", Toast.LENGTH_LONG).show()
                                                binding.pbTeacherMain.visibility = View.GONE
                                            }


                                    }
                                } catch (e: FirebaseAuthInvalidCredentialsException) {
                                    Toast.makeText(this@TeacherMainActivity, "Email or password is incorrect. Please check again.", Toast.LENGTH_LONG).show()
                                    binding.pbTeacherMain.visibility = View.GONE
                                } catch (e: FirebaseNetworkException) {
                                    Toast.makeText(this@TeacherMainActivity, "Network issue has occurred. Please try again later.", Toast.LENGTH_LONG).show()
                                    binding.pbTeacherMain.visibility = View.GONE
                                } catch (e: FirebaseException) {
                                    Toast.makeText(this@TeacherMainActivity, "A DB service error has occurred. Please try again later.", Toast.LENGTH_LONG).show()
                                    binding.pbTeacherMain.visibility = View.GONE
                                } catch (e: FirebaseAuthInvalidUserException) {
                                    Toast.makeText(this@TeacherMainActivity, "User does not exist.", Toast.LENGTH_LONG).show()
                                    binding.pbTeacherMain.visibility = View.GONE
                                } catch (e: Exception) {
                                    Toast.makeText(this@TeacherMainActivity, "An error has occurred. Please try again later.", Toast.LENGTH_LONG).show()
                                    binding.pbTeacherMain.visibility = View.GONE
                                }
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

    fun askForLogOut() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.apply {
            setTitle("Log out")
            setMessage("Would you like to log out?")
            setPositiveButton("YES") { dialog, which ->
                authDao.logout()

                val intent = Intent(this@TeacherMainActivity, CheckRoleActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
            setNegativeButton("NO") { dialog, which ->
                dialog.dismiss()
            }
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    override fun onBackPressed() {
        // 뒤로 가기 버튼 동작 없음
    }
}