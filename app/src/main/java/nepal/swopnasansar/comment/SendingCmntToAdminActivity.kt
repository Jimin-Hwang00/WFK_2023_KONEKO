package nepal.swopnasansar.comment

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nepal.swopnasansar.R
import nepal.swopnasansar.comment.dao.AdminDAO
import nepal.swopnasansar.comment.dao.CommentDAO
import nepal.swopnasansar.comment.dto.Administrator
import nepal.swopnasansar.comment.dto.Comment
import nepal.swopnasansar.comment.dto.ReceiverTarget
import nepal.swopnasansar.databinding.ActivitySendingCmntToAdminBinding
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class SendingCmntToAdminActivity : AppCompatActivity() {
    private val TAG = "SendingCmntToAdminActivity"

    private lateinit var binding: ActivitySendingCmntToAdminBinding

    var admins: ArrayList<Administrator>? = ArrayList()

    val adminDAO = AdminDAO()
    val commentDAO = CommentDAO()

    val date = Instant.ofEpochMilli(System.currentTimeMillis())
        .atOffset(ZoneOffset.ofHours(9))
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySendingCmntToAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnUploadCmntToAdmin.setOnClickListener {
            if (binding.evCmntToAdminTitle.text.isBlank()) {
                Toast.makeText(this@SendingCmntToAdminActivity, "Please write down the title", Toast.LENGTH_SHORT).show()
            } else {
                lifecycleScope.launch(Dispatchers.IO) {
                    var uploadResult =  withContext(Dispatchers.IO) {
                        //@TODO author_key 확인 필요 !! - upload 부분 (로그인되어 있는 키로 변경)
                        val comment = Comment("", binding.evCmntToAdminTitle.text.toString(), binding.evSendCmntToAdminContent.text.toString(), date, "test_key", "author_name", admins!![0].admin_key, admins!![0].admin_name, false)
                        commentDAO.uploadComment(comment)
                    }

                    withContext(Main) {
                        if (!uploadResult) {
                            Toast.makeText(this@SendingCmntToAdminActivity, "Failed to upload comment. Try Again", Toast.LENGTH_SHORT).show()
                        } else {
                            val intent = Intent(this@SendingCmntToAdminActivity, SentCmntListActivity::class.java)
                            //@TODO author_key 확인 필요 !! -  보낸 코멘트 확인 (로그인되어 있는 키로 변경)
                            intent.putExtra("author_key", "test_key")
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