package nepal.swopnasansar.comment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nepal.swopnasansar.dao.AdminDAO
import nepal.swopnasansar.dao.AuthDAO
import nepal.swopnasansar.dao.CommentDAO
import nepal.swopnasansar.dto.Administrator
import nepal.swopnasansar.dto.Comment
import nepal.swopnasansar.databinding.ActivitySendingCmntToAdminBinding
import nepal.swopnasansar.login.CheckRoleActivity
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class SendingCmntToAdminActivity : AppCompatActivity() {
    private val TAG = "SendingCmntToAdminActivity"

    private lateinit var binding: ActivitySendingCmntToAdminBinding

    var admins: ArrayList<Administrator>? = ArrayList()

    private val authDao = AuthDAO()
    private val adminDAO = AdminDAO()
    private val commentDAO = CommentDAO()

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
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            startActivity(intent)
        }

        binding.btnUploadCmntToAdmin.setOnClickListener {
            if (binding.evCmntToAdminTitle.text.isBlank()) {
                Toast.makeText(this@SendingCmntToAdminActivity, "Please write down the title", Toast.LENGTH_SHORT).show()
            } else {
                lifecycleScope.launch(Dispatchers.IO) {
                    var uploadResult =  withContext(Dispatchers.IO) {
                        //@TODO author_name 어떻게 할지 고민
                        val comment = Comment("", binding.evCmntToAdminTitle.text.toString(), binding.evSendCmntToAdminContent.text.toString(), date, uid!!, "author_name", admins!![0].admin_key, admins!![0].admin_name, false)
                        commentDAO.uploadComment(comment)
                    }

                    withContext(Main) {
                        if (!uploadResult) {
                            Toast.makeText(this@SendingCmntToAdminActivity, "Failed to upload comment. Try Again", Toast.LENGTH_SHORT).show()
                        } else {
                            val intent = Intent(this@SendingCmntToAdminActivity, SentCmntListActivity::class.java)
                            startActivity(intent)
                        }
                    }
                }


            }
        }
    }

    override fun onResume() {
        getAdmin()
        super.onResume()
    }

    fun getAdmin() {
        binding.pbSendingCmntToAdmin.visibility = View.VISIBLE

        lifecycleScope.launch(Dispatchers.IO) {

            withContext(Dispatchers.IO) {
                admins = adminDAO.getAllAdmin()

                if (admins == null) {
                    Toast.makeText(this@SendingCmntToAdminActivity, "Failed to get Administrator.", Toast.LENGTH_SHORT).show()
                }
            }

            withContext(Main) {
                binding.pbSendingCmntToAdmin.visibility = View.INVISIBLE
            }
        }
    }
}