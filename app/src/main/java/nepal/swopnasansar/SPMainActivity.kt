package nepal.swopnasansar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import nepal.swopnasansar.accountant.SPCheckTuitionActivity
import nepal.swopnasansar.admin.UserCheckEventActivity
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

        Log.d("SPMainActivity", "uid : ${uid}")

        if (uid == null) {
            val intent = Intent(this, CheckRoleActivity::class.java)
            startActivity(intent)
        } else {
            lifecycleScope.launch {
                binding.pbSpMain.visibility = View.VISIBLE

                val student = withContext(Dispatchers.IO) {
                    studentDao.getStudentByKey(uid)
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
            val intent = Intent(this, UserCheckEventActivity::class.java)
            startActivity(intent)
        }

        binding.arrowSpCalendar.setOnClickListener {
            val intent = Intent(this, UserCheckEventActivity::class.java)
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
                                        binding.pbSpMain.visibility = View.VISIBLE

                                        val credential = EmailAuthProvider.getCredential(email, pw)
                                        user.reauthenticate(credential)
                                            .addOnSuccessListener {
                                                CoroutineScope(Dispatchers.IO).launch {
                                                    val deleteDBResult = withContext(Dispatchers.IO) {
                                                        studentDao.removeStudentByKey(uid!!)
                                                    }

                                                    withContext(Main) {
                                                        if (deleteDBResult) {
                                                            user.delete()
                                                                .addOnSuccessListener {
                                                                    Toast.makeText(applicationContext, "Your account has been deleted.", Toast.LENGTH_LONG).show()
                                                                    binding.pbSpMain.visibility = View.GONE
                                                                    val intent = Intent(this@SPMainActivity, CheckRoleActivity::class.java)
                                                                    startActivity(intent)
                                                                }
                                                                .addOnFailureListener {
                                                                    Toast.makeText(this@SPMainActivity, "An error has occurred. Please contact administrator", Toast.LENGTH_LONG).show()
                                                                    binding.pbSpMain.visibility = View.GONE
                                                                }
                                                        } else {
                                                            Toast.makeText(this@SPMainActivity, "An error has occurred. Please contact administrator.", Toast.LENGTH_SHORT).show()
                                                            binding.pbSpMain.visibility = View.GONE
                                                        }
                                                    }
                                                }

                                            }
                                            .addOnFailureListener {
                                                Toast.makeText(this@SPMainActivity, "Email or password is incorrect. Please check again.", Toast.LENGTH_LONG).show()
                                                binding.pbSpMain.visibility = View.GONE
                                            }


                                    }
                                } catch (e: FirebaseAuthInvalidCredentialsException) {
                                    Toast.makeText(this@SPMainActivity, "Email or password is incorrect. Please check again.", Toast.LENGTH_LONG).show()
                                    binding.pbSpMain.visibility = View.GONE
                                } catch (e: FirebaseNetworkException) {
                                    Toast.makeText(this@SPMainActivity, "Network issue has occurred. Please try again later.", Toast.LENGTH_LONG).show()
                                    binding.pbSpMain.visibility = View.GONE
                                } catch (e: FirebaseException) {
                                    Toast.makeText(this@SPMainActivity, "A DB service error has occurred. Please try again later.", Toast.LENGTH_LONG).show()
                                    binding.pbSpMain.visibility = View.GONE
                                } catch (e: FirebaseAuthInvalidUserException) {
                                    Toast.makeText(this@SPMainActivity, "User does not exist.", Toast.LENGTH_LONG).show()
                                    binding.pbSpMain.visibility = View.GONE
                                } catch (e: Exception) {
                                    Toast.makeText(this@SPMainActivity, "An error has occurred. Please try again later.", Toast.LENGTH_LONG).show()
                                    binding.pbSpMain.visibility = View.GONE
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

    suspend private fun signOutProcess(email: String, pw: String) {
        val user = authDao.getUser()

        if (user != null) {
            try {
                lifecycleScope.launch {
                    binding.pbSpMain.visibility = View.VISIBLE

                    val deleteDBResult = withContext(Dispatchers.IO) {
                        studentDao.removeStudentByKey(uid!!)
                    }

                    if (deleteDBResult) {
                        val credential = EmailAuthProvider.getCredential(email, pw)
                        user.reauthenticate(credential)
                            .addOnSuccessListener {
                                user.delete()
                                    .addOnSuccessListener {
                                        Toast.makeText(applicationContext, "Your account has been deleted.", Toast.LENGTH_LONG).show()
                                        binding.pbSpMain.visibility = View.GONE
                                        val intent = Intent(this@SPMainActivity, CheckRoleActivity::class.java)
                                        startActivity(intent)
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(this@SPMainActivity, "An error has occurred. Please contact administrator", Toast.LENGTH_SHORT).show()
                                        binding.pbSpMain.visibility = View.GONE
                                    }
                            }
                            .addOnFailureListener {
                                Toast.makeText(this@SPMainActivity, "Fail to delete your account. Try again.", Toast.LENGTH_SHORT).show()
                                binding.pbSpMain.visibility = View.GONE
                            }
                    } else {
                        Toast.makeText(this@SPMainActivity, "Fail to delete your account. Try again.", Toast.LENGTH_SHORT).show()
                        binding.pbSpMain.visibility = View.GONE
                    }
                }
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                Toast.makeText(this@SPMainActivity, "Email or password is incorrect. Please check again", Toast.LENGTH_LONG).show()
                binding.pbSpMain.visibility = View.GONE
            } catch (e: FirebaseNetworkException) {
                Toast.makeText(this@SPMainActivity, "Network issue has occurred. Please try again later.", Toast.LENGTH_LONG).show()
                binding.pbSpMain.visibility = View.GONE
            } catch (e: FirebaseException) {
                Toast.makeText(this@SPMainActivity, "A DB service error has occurred. Please try again later.", Toast.LENGTH_LONG).show()
                binding.pbSpMain.visibility = View.GONE
            } catch (e: FirebaseAuthInvalidUserException) {
                Toast.makeText(this@SPMainActivity, "User does not exist.", Toast.LENGTH_LONG).show()
                binding.pbSpMain.visibility = View.GONE
            } catch (e: Exception) {
                Toast.makeText(this@SPMainActivity, "An error has occurred. Please try again later.", Toast.LENGTH_LONG).show()
                binding.pbSpMain.visibility = View.GONE
            }
        }
    }

    fun askForLogOut() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.apply {
            setTitle("Log out")
            setMessage("Would you like to log out?")
            setPositiveButton("YES") { dialog, which ->
                authDao.logout()

                val intent = Intent(this@SPMainActivity, CheckRoleActivity::class.java)
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