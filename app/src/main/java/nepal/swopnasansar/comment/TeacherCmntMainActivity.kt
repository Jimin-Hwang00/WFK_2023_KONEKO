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
import nepal.swopnasansar.databinding.ActivityTeacherCmntMainBinding

class TeacherCmntMainActivity : AppCompatActivity() {
    val commentDao = CommentDAO()

    private lateinit var binding: ActivityTeacherCmntMainBinding

    private lateinit var receivedKey: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeacherCmntMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvReceivedCmntFromSp.setOnClickListener {
            val intent  = Intent(this, ReceivedCmntListAcitivity::class.java)
            intent.putExtra("key", receivedKey) //@TODO 실제 사용자 키를 입력해야 함.
            startActivity(intent)
        }

        binding.tvSentCmntTeacher.setOnClickListener {
            val intent = Intent(this, SentCmntListActivity::class.java)
            intent.putExtra("key", receivedKey) // @TODO 실제 사용자 키를 입력해야 함.
            startActivity(intent)
        }

        binding.tvSendCmntToSp.setOnClickListener {
            val intent = Intent(this, SendingCmntActivity::class.java)
            intent.putExtra("targetRole", "student")
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        binding.pbCommentTeacherMain.visibility = View.VISIBLE

        lifecycleScope.launch {
            val cnt = withContext(Dispatchers.IO) {
                // @TODO key 변경 (로그인 uid로)
                commentDao.countUnReadComments("")
            }

            binding.unreadCommentsSp.text = cnt.toString()

            if (cnt == 0) {
                binding.unreadCommentsSp.visibility = View.INVISIBLE
            } else {
                binding.unreadCommentsSp.visibility = View.VISIBLE
            }
        }

        binding.pbCommentTeacherMain.visibility = View.INVISIBLE
    }
}