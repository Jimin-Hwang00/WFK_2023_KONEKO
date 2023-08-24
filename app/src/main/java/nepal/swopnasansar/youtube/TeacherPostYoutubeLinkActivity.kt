package nepal.swopnasansar.youtube

import android.content.Intent
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nepal.swopnasansar.dao.AuthDAO
import nepal.swopnasansar.databinding.ActivityTeacherPostYoutubeLinkBinding
import nepal.swopnasansar.dao.SubjectDAO
import nepal.swopnasansar.dto.Subject
import nepal.swopnasansar.dto.Youtube
import nepal.swopnasansar.login.CheckRoleActivity

class TeacherPostYoutubeLinkActivity : AppCompatActivity() {
    private val TAG = "TeacherPostYoutubeLinkActivity"

    private lateinit var binding: ActivityTeacherPostYoutubeLinkBinding

    private var subjects: ArrayList<Subject>? = ArrayList()
    private var subjectListAdapter = YoutubeSubjectListAdapter(subjects)

    var selectedSubject = Subject()

    private val authDao = AuthDAO()
    private val subjectDao = SubjectDAO()

    val uid = authDao.getUid()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeacherPostYoutubeLinkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecycler()

        if (uid == null) {
            Toast.makeText(applicationContext, "You have to login.", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, CheckRoleActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

        // set click listener that allow the users to select a subject
        subjectListAdapter.setOnItemClickListener(object: YoutubeSubjectListAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                if (subjects != null) {
                    selectedSubject = subjects!![position]

                    subjectListAdapter.selectedIdx = position
                    subjectListAdapter.notifyDataSetChanged()
                }
            }
        })

        // set click listener that allow the users to post the youtube url and title
        binding.btnUploadYoutube.setOnClickListener {
            val title = binding.evPostYoutubeTitle.text.toString()
            val url = binding.evPostYoutubeUrl.text.toString()
            val youtube = Youtube(url, title)

            if (title.isEmpty() || url.isEmpty()) {
                Toast.makeText(this@TeacherPostYoutubeLinkActivity, "You have to write title and url.", Toast.LENGTH_SHORT).show()
            } else if (!url.startsWith("http")) {
                Toast.makeText(this@TeacherPostYoutubeLinkActivity, "The format of the URL is incorrect.", Toast.LENGTH_SHORT).show()
            } else {
                lifecycleScope.launch(Dispatchers.IO) {
                    withContext(Main) {
                        binding.pbYoutubePost.visibility = View.VISIBLE
                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    }
                    val result = withContext(Dispatchers.IO) {
                        subjectDao.addYoutube(selectedSubject, youtube)
                    }

                    withContext(Main) {
                        if (result) {
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            Toast.makeText(
                                applicationContext,
                                "Success to upload youtube link.",
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.pbYoutubePost.visibility = View.INVISIBLE
                            finish()
                        } else {
                            binding.pbYoutubePost.visibility = View.INVISIBLE
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            Toast.makeText(
                                this@TeacherPostYoutubeLinkActivity,
                                "Fail to upload youtube link. Try again.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    // connect adapter with recycler view
    fun initRecycler() {
        if (subjects == null) {
            subjectListAdapter = YoutubeSubjectListAdapter(ArrayList())
        } else {
            subjectListAdapter = YoutubeSubjectListAdapter(subjects)
        }

        binding.rvPostYoutubeSubject.adapter = subjectListAdapter
        binding.rvPostYoutubeSubject.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        subjectListAdapter.notifyDataSetChanged()
    }

    // get all subjects and display them to the users
    override fun onResume() {
        if (subjects != null) {
            subjects!!.clear()
        }

        binding.evPostYoutubeUrl.setText("")
        binding.evPostYoutubeTitle.setText("")

        binding.pbYoutubePost.visibility = View.VISIBLE
        lifecycleScope.launch(Dispatchers.IO) {
            subjects = withContext(Dispatchers.IO) {
                subjectDao.getSubjectByTeacherKey(uid!!)
            }

            withContext(Main) {
                subjectListAdapter.subjects = subjects

                subjectListAdapter.notifyDataSetChanged()
                binding.pbYoutubePost.visibility = View.INVISIBLE
            }
        }

        super.onResume()
    }
}