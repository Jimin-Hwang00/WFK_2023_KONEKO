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

class SentCmntListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCmntListBinding

    private lateinit var sentCommentListAdapter: CmntListAdapter

    var sentComments: ArrayList<Comment>? = ArrayList()

    val commentDAO = CommentDAO()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCmntListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecyclerView()

        sentCommentListAdapter.setOnItemClickListener(object: CmntListAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                binding.tvCmntContent.text = sentCommentListAdapter.itemList[position].content

                binding.cmntContentText.visibility = View.VISIBLE
                binding.tvCmntContent.visibility = View.VISIBLE

                sentCommentListAdapter.selectedIdx = position
                sentCommentListAdapter.notifyDataSetChanged()
            }
        })
    }

    override fun onResume() {
        lifecycleScope.launch {
            binding.pbCmntList.visibility = View.VISIBLE

            val comments: ArrayList<Comment>? = withContext(Dispatchers.IO) {
                // @TODO key 변경 (로그인 uid로 변경)
                commentDAO.getCommentByAuthorKey("test_key")
            }

            if (comments != null) {
                comments.sortByDescending { it.date }
                sentCommentListAdapter.notifyDataSetChanged()
            }

            binding.pbCmntList.visibility = View.INVISIBLE

            if (comments != null) {
                sentComments = comments
                updateUI(comments) // UI 업데이트 함수 호출
            } else {
                Toast.makeText(this@SentCmntListActivity, "Fail to get received comments. Try again!", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        super.onResume()
    }

    fun initRecyclerView() {
        if (sentComments == null) {
            sentCommentListAdapter = CmntListAdapter(ArrayList(), this)
        } else {
            sentCommentListAdapter = CmntListAdapter(sentComments!!, this)
        }

        sentCommentListAdapter.notifyDataSetChanged()

        binding.rvCmnt.adapter = sentCommentListAdapter
        binding.rvCmnt.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    private fun updateUI(comments: ArrayList<Comment>?) {
        // Null 체크 및 데이터 업데이트
        sentComments = comments ?: ArrayList()

        if (sentCommentListAdapter == null) {
            sentCommentListAdapter = CmntListAdapter(sentComments!!, this)
            binding.rvCmnt.adapter = sentCommentListAdapter
            binding.rvCmnt.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        } else {
            sentCommentListAdapter?.updateData(sentComments!!)
        }
    }
}