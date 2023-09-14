package nepal.swopnasansar.comment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nepal.swopnasansar.dao.AuthDAO
import nepal.swopnasansar.dao.CommentDAO
import nepal.swopnasansar.databinding.ActivityAdminCmntMainBinding
import nepal.swopnasansar.login.CheckRoleActivity

class AdminCmntMainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminCmntMainBinding

    private val commentDao = CommentDAO()

    var uid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminCmntMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref: SharedPreferences = getSharedPreferences("save_state", Context.MODE_PRIVATE)
        uid = pref.getString("uid", null)

        if (uid == null) {
            Toast.makeText(applicationContext, "You have to login.", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, CheckRoleActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

        binding.tvSendCmntToTeacherFromAd.setOnClickListener {
            val intent = Intent(this@AdminCmntMainActivity, SendingCmntToTFromAd::class.java)
            startActivity(intent)
        }

        binding.arrowSendCmntToTFromAd.setOnClickListener {
            val intent = Intent(this@AdminCmntMainActivity, SendingCmntToTFromAd::class.java)
            startActivity(intent)
        }

        binding.tvReceivedCmntAd.setOnClickListener {
            val intent = Intent(this@AdminCmntMainActivity, ReceivedCmntListAcitivity::class.java)
            startActivity(intent)
        }

        binding.arrowCheckReceivedCmntAd.setOnClickListener {
            val intent = Intent(this@AdminCmntMainActivity, ReceivedCmntListAcitivity::class.java)
            startActivity(intent)
        }

        binding.tvSentCmntAd.setOnClickListener {
            val intent = Intent(this@AdminCmntMainActivity, SentCmntListActivity::class.java)
            startActivity(intent)
        }

        binding.arrowCheckSentCmntAd.setOnClickListener {
            val intent = Intent(this@AdminCmntMainActivity, SentCmntListActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        binding.pbAdminCmntMain.visibility = View.VISIBLE

        lifecycleScope.launch {
            val cnt = withContext(Dispatchers.IO) {
                commentDao.countUnReadComments(uid!!)
            }

            binding.unreadCommentsAdmin.text = cnt.toString()

            if (cnt == 0) {
                binding.unreadCommentsAdmin.visibility = View.INVISIBLE
            } else {
                binding.unreadCommentsAdmin.visibility = View.VISIBLE
            }
        }

        binding.pbAdminCmntMain.visibility = View.GONE
    }
}