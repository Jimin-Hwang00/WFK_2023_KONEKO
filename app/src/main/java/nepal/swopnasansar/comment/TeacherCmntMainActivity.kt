package nepal.swopnasansar.comment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nepal.swopnasansar.R
import nepal.swopnasansar.dao.AuthDAO
import nepal.swopnasansar.dao.CommentDAO
import nepal.swopnasansar.databinding.ActivityTeacherCmntMainBinding
import nepal.swopnasansar.login.CheckRoleActivity

class TeacherCmntMainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTeacherCmntMainBinding

    private val authDao = AuthDAO()
    private val commentDao = CommentDAO()

    val uid = authDao.getUid()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeacherCmntMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (uid == null) {
            Toast.makeText(applicationContext, "You have to login.", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, CheckRoleActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

        binding.tvReceivedCmntFromSp.setOnClickListener {
            val intent  = Intent(this, ReceivedCmntListAcitivity::class.java)
            startActivity(intent)
        }

        binding.tvSentCmntTeacher.setOnClickListener {
            val intent = Intent(this, SentCmntListActivity::class.java)
            startActivity(intent)
        }

        binding.tvSendCmntToSp.setOnClickListener {
            val intent = Intent(this, SendingCmntActivity::class.java)
            intent.putExtra("targetRole", getString(R.string.student))
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        binding.pbCommentTeacherMain.visibility = View.VISIBLE

        lifecycleScope.launch {
            val cnt = withContext(Dispatchers.IO) {
                commentDao.countUnReadComments(uid!!)
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