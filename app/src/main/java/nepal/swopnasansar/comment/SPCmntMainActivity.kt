package nepal.swopnasansar.comment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nepal.swopnasansar.comment.dao.CommentDAO
import nepal.swopnasansar.databinding.ActivitySpCmntMainBinding

class SPCmntMainActivity : AppCompatActivity() {
    val commentDao = CommentDAO()

    private lateinit var binding: ActivitySpCmntMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpCmntMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvSendCmntToAdmin.setOnClickListener {
            val intent = Intent(this, SendingCmntToAdminActivity::class.java)
            startActivity(intent)
        }

        binding.tvSendCmntToTeacher.setOnClickListener {
            val intent = Intent(this, SendingCmntActivity::class.java)
            intent.putExtra("targetRole", "teacher")
            startActivity(intent)
        }

        binding.tvReceivedCmntFromTeacher.setOnClickListener {
            val intent = Intent(this, ReceivedCmntListAcitivity::class.java)
            startActivity(intent)
        }

        binding.tvCheckSentCmntSp.setOnClickListener {
            val intent = Intent(this, SentCmntListActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        binding.pbCommentSpMain.visibility = View.VISIBLE

        lifecycleScope.launch {
            val cnt = withContext(Dispatchers.IO) {
                //@TODO key 값 변경 (로그인 되어 있는 키로)
                commentDao.countUnReadComments("oxZS7WpXGilaSTlt4ntY")
            }

            binding.unreadCommentsSp.text = cnt.toString()

            if (cnt == 0) {
                binding.unreadCommentsSp.visibility = View.INVISIBLE
            } else {
                binding.unreadCommentsSp.visibility = View.VISIBLE
            }
        }

        binding.pbCommentSpMain.visibility = View.INVISIBLE
    }
}