package nepal.swopnasansar.comment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nepal.swopnasansar.dao.AuthDAO
import nepal.swopnasansar.dao.CommentDAO
import nepal.swopnasansar.dto.Comment
import nepal.swopnasansar.databinding.ActivityCmntListBinding
import nepal.swopnasansar.login.CheckRoleActivity

class ReceivedCmntListAcitivity : AppCompatActivity() {
    private val TAG = "ReceivedCmntListActivity"
    private lateinit var binding: ActivityCmntListBinding

    private lateinit var receivedCommentListAdapter: CmntListAdapter

    var receivedComments: ArrayList<Comment>? = ArrayList()

    private val commentDao = CommentDAO()
    private val authDao = AuthDAO()


    var uid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCmntListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref: SharedPreferences = getSharedPreferences("save_state", Context.MODE_PRIVATE)
        uid = pref.getString("uid", authDao.getUid())
        Log.d(TAG, "uid : ${uid}")

        if (uid == null) {
            Toast.makeText(applicationContext, "You have to login.", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, CheckRoleActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

        initRecyclerView()

        binding.btnDeleteCmnt.visibility = View.GONE

        receivedCommentListAdapter.setOnItemClickListener(object: CmntListAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                if (!receivedCommentListAdapter.itemList[position].read) {
                    lifecycleScope.launch {
                        val updateResult = withContext(Dispatchers.IO) {
                            commentDao.updateReadStatusToTrue(receivedCommentListAdapter.itemList[position].comment_key)
                        }

                        if (updateResult) {
                            receivedCommentListAdapter.itemList[position].read = true
                            receivedCommentListAdapter.notifyDataSetChanged()
                        }
                    }
                }

                binding.tvCmntContent.text = receivedCommentListAdapter.itemList[position].content

                binding.tvCmntContent.visibility = View.VISIBLE
                binding.cmntContentText.visibility = View.VISIBLE

                receivedCommentListAdapter.selectedIdx = position
                receivedCommentListAdapter.notifyDataSetChanged()
            }
        })
    }

    override fun onResume() {
        lifecycleScope.launch {
            binding.pbCmntList.visibility = View.VISIBLE

            val comments = withContext(Dispatchers.IO) {
                commentDao.getCommentByReceiverKey(uid!!)
            }

            binding.pbCmntList.visibility = View.INVISIBLE

            if (comments != null) {
                receivedComments = comments
                updateUI(comments) // UI 업데이트 함수 호출
            } else {
                Toast.makeText(this@ReceivedCmntListAcitivity, "Fail to get received comments. Try again!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        super.onResume()
    }

    fun initRecyclerView() {
        if (receivedComments == null) {
            receivedCommentListAdapter = CmntListAdapter(ArrayList(), this)
        } else {
            receivedCommentListAdapter = CmntListAdapter(receivedComments!!, this)
        }

        receivedCommentListAdapter.notifyDataSetChanged()

        binding.rvCmnt.adapter = receivedCommentListAdapter
        binding.rvCmnt.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    private fun updateUI(comments: ArrayList<Comment>?) {
        // Null 체크 및 데이터 업데이트
        receivedComments = comments ?: ArrayList()

        if (receivedCommentListAdapter == null) {
            receivedCommentListAdapter = CmntListAdapter(receivedComments!!, this)
            binding.rvCmnt.adapter = receivedCommentListAdapter
            binding.rvCmnt.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        } else {
            receivedCommentListAdapter?.updateData(receivedComments!!)
        }
    }

}