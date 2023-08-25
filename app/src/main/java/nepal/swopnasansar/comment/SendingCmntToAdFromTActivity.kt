package nepal.swopnasansar.comment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nepal.swopnasansar.dao.*
import nepal.swopnasansar.dto.Administrator
import nepal.swopnasansar.dto.Comment
import nepal.swopnasansar.databinding.ActivitySendingCmntToAdminBinding
import nepal.swopnasansar.login.CheckRoleActivity
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class SendingCmntToAdFromTActivity : AppCompatActivity() {
    private val TAG = "SendingCmntToAdFromTActivity"

    private lateinit var binding: ActivitySendingCmntToAdminBinding

    var admins: ArrayList<Administrator>? = ArrayList()

    private val authDao = AuthDAO()
    private val adminDao = AdminDAO()
    private val commentDao = CommentDAO()
    private val teacherDao = TeacherDAO()

    val uid = authDao.getUid()

    val date = Instant.ofEpochMilli(System.currentTimeMillis())
        .atOffset(ZoneOffset.ofHours(9))
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySendingCmntToAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (uid == null) {
            Toast.makeText(applicationContext, "You have to login.", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, CheckRoleActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

        binding.btnUploadCmntToAdmin.setOnClickListener {
            if (binding.evCmntToAdminTitle.text.isBlank()) {
                Toast.makeText(this@SendingCmntToAdFromTActivity, "Please write down the title", Toast.LENGTH_SHORT).show()
            } else {
                lifecycleScope.launch {
                    binding.pbSendingCmntToAdmin.visibility = View.VISIBLE
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                    lateinit var userName: String
                    val student = withContext(Dispatchers.IO) {
                        teacherDao.getTeacherByKey(uid!!)
                    }

                    if (student != null) {
                        userName = student.teacher_name

                        val uploadResult =  withContext(Dispatchers.IO) {
                            val comment = Comment("", binding.evCmntToAdminTitle.text.toString(), binding.evSendCmntToAdminContent.text.toString(), date, uid!!, userName, admins!![0].admin_key, admins!![0].admin_name, false)
                            commentDao.uploadComment(comment)
                        }

                        if (!uploadResult) {
                            Toast.makeText(this@SendingCmntToAdFromTActivity, "Failed to upload comment. Try Again", Toast.LENGTH_SHORT).show()
                            binding.pbSendingCmntToAdmin.visibility = View.INVISIBLE
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        } else {
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            val intent = Intent(this@SendingCmntToAdFromTActivity, SentCmntListActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        Toast.makeText(this@SendingCmntToAdFromTActivity, "Fail to upload comment. Try again.", Toast.LENGTH_SHORT).show()
                        binding.pbSendingCmntToAdmin.visibility = View.INVISIBLE
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    }
                }


            }
        }
    }

    override fun onResume() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        getAdmin()
        super.onResume()
    }

    fun getAdmin() {
        binding.pbSendingCmntToAdmin.visibility = View.VISIBLE

        lifecycleScope.launch(Dispatchers.IO) {

            withContext(Dispatchers.IO) {
                admins = adminDao.getAllAdmin()

                if (admins == null) {
                    Toast.makeText(this@SendingCmntToAdFromTActivity, "Failed to get Administrator.", Toast.LENGTH_SHORT).show()
                }
            }

            withContext(Main) {
                binding.pbSendingCmntToAdmin.visibility = View.INVISIBLE
            }
        }
    }
}