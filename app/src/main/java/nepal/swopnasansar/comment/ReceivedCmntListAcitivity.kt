package nepal.swopnasansar.comment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nepal.swopnasansar.comment.dao.CommentDAO
import nepal.swopnasansar.comment.dto.Comment
import nepal.swopnasansar.databinding.ActivityCmntListBinding

class ReceivedCmntListAcitivity : AppCompatActivity() {
    private val TAG = "ReceivedCmntListActivity"
    private lateinit var binding: ActivityCmntListBinding

    private lateinit var receivedCommentListAdapter: CmntListAdapter

    var receivedComments: ArrayList<Comment>? = ArrayList()

    val dao = CommentDAO()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCmntListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val key = intent.getStringExtra("key").toString()

        initRecyclerView()

        receivedCommentListAdapter.setOnItemClickListener(object: CmntListAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                dao.updateReadStatusToTrue(receivedCommentListAdapter.itemList[position].comment_key)

                receivedCommentListAdapter.itemList[position].read = true
                receivedCommentListAdapter.notifyDataSetChanged()

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
                // @TODO key 값 변경 (로그인한 uid로)
                dao.getCommentByReceiverKey("oxZS7WpXGilaSTlt4ntY")
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