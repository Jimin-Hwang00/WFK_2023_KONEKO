package nepal.swopnasansar.youtube

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nepal.swopnasansar.databinding.ActivityTeacherPostYoutubeLinkBinding
import nepal.swopnasansar.youtube.dao.SubjectDAO
import nepal.swopnasansar.youtube.dto.Subject
import nepal.swopnasansar.youtube.dto.Youtube

class TeacherPostYoutubeLinkActivity : AppCompatActivity() {
    private val TAG = "TeacherPostYoutubeLinkActivity"

    private lateinit var binding: ActivityTeacherPostYoutubeLinkBinding

    private var subjects: ArrayList<Subject>? = ArrayList()
    private var subjectListAdapter = SubjectListAdapter(subjects)

    var selectedSubject = Subject()

    val subjectDao = SubjectDAO()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeacherPostYoutubeLinkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecycler()

        // set click listener that allow the users to select a subject
        subjectListAdapter.setOnItemClickListener(object: SubjectListAdapter.OnItemClickListener {
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
            var youtubes = selectedSubject.youTube

            val title = binding.evPostYoutubeTitle.text.toString()
            val url = binding.evPostYoutubeUrl.text.toString()
            val youtube = Youtube(url, title)

            if (title.isEmpty() || url.isEmpty()) {
                Toast.makeText(this@TeacherPostYoutubeLinkActivity, "You have to write title and url.", Toast.LENGTH_SHORT).show()
            } else if (!url.startsWith("http")) {
                Toast.makeText(this@TeacherPostYoutubeLinkActivity, "The format of the URL is incorrect.", Toast.LENGTH_SHORT).show()
            } else {
                youtubes.add(youtube)
                selectedSubject.youTube = youtubes

                lifecycleScope.launch(Dispatchers.IO) {
                    val result = withContext(Dispatchers.IO) {
                        subjectDao.updateSubject(selectedSubject)
                    }

                    withContext(Main) {
                        if (result) {
                            val intent = Intent(
                                this@TeacherPostYoutubeLinkActivity,
                                TeacherCheckYoutbeListActivity::class.java
                            )
                            startActivity(intent)
                        } else {
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
            subjectListAdapter = SubjectListAdapter(ArrayList())
        } else {
            subjectListAdapter = SubjectListAdapter(subjects)
        }

        binding.rvPostYoutubeSubject.adapter = subjectListAdapter
        binding.rvPostYoutubeSubject.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        subjectListAdapter.notifyDataSetChanged()
    }

    // get all subjects and display them to the users
    override fun onResume() {
        binding.evPostYoutubeUrl.setText("")
        binding.evPostYoutubeTitle.setText("")

        binding.pbYoutubePost.visibility = View.VISIBLE
        lifecycleScope.launch(Dispatchers.IO) {
            subjects = withContext(Dispatchers.IO) {
                // @TODO key값 수정
                subjectDao.getSubjectsByTeacherKey("qxLHhh9StYOfogNqLN9G")
            }

            subjectListAdapter.subjects = subjects

            withContext(Main) {
                subjectListAdapter.notifyDataSetChanged()
                binding.pbYoutubePost.visibility = View.INVISIBLE
            }
        }

        super.onResume()
    }
}