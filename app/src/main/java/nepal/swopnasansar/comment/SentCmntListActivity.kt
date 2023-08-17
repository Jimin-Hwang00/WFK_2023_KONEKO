package nepal.swopnasansar.comment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
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

class SentCmntListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCmntListBinding

    private lateinit var sentCommentListAdapter: CmntListAdapter

    var sentComments: ArrayList<Comment>? = ArrayList()

    private val authDao = AuthDAO()
    private val commentDAO = CommentDAO()

    val uid = authDao.getUid()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCmntListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (uid == null) {
            Toast.makeText(applicationContext, "You have to login.", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, CheckRoleActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            startActivity(intent)
        }

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

        binding.btnDeleteCmnt.setOnClickListener {
            binding.pbCmntList.visibility = View.VISIBLE
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

            lifecycleScope.launch {
                val result = withContext(Dispatchers.IO) {
                    commentDAO.removeCommentByKey(sentComments!![sentCommentListAdapter.selectedIdx].comment_key)
                }

                if (result) {
                    Toast.makeText(this@SentCmntListActivity, "The comment has been deleted.", Toast.LENGTH_SHORT).show()

                    sentCommentListAdapter.selectedIdx = -1
                    sentCommentListAdapter.notifyDataSetChanged()

                    binding.btnDeleteCmnt.visibility = View.GONE
                    binding.tvCmntContent.text = ""

                    getSentCommnets()

                    binding.pbCmntList.visibility = View.INVISIBLE
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                } else {
                    Toast.makeText(this@SentCmntListActivity, "Unable to delete the comment. Try again.", Toast.LENGTH_SHORT).show()
                    binding.pbCmntList.visibility = View.INVISIBLE
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }
            }
        }
    }

    override fun onResume() {
        getSentCommnets()

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

    fun getSentCommnets() {
        lifecycleScope.launch {
            binding.pbCmntList.visibility = View.VISIBLE

            val comments: ArrayList<Comment>? = withContext(Dispatchers.IO) {
                commentDAO.getCommentByAuthorKey(uid!!)
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
    }
}