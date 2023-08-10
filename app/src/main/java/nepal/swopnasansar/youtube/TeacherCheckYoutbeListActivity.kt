package nepal.swopnasansar.youtube

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nepal.swopnasansar.databinding.ActivityTeacherCheckYoutbeListBinding
import nepal.swopnasansar.youtube.dao.SubjectDAO
import nepal.swopnasansar.youtube.dto.Subject
import nepal.swopnasansar.youtube.dto.Youtube
import nepal.swopnasansar.youtube.dto.YoutubeListItem

class TeacherCheckYoutbeListActivity : AppCompatActivity() {
    private val TAG = "TeacherCheckYoutubeList"

    private lateinit var binding: ActivityTeacherCheckYoutbeListBinding

    val youtubeItems = ArrayList<YoutubeListItem>()
    var selectedYoutubeItem = YoutubeListItem()
    var subjects: ArrayList<Subject>? = ArrayList()

    private var youtubeLinkAdapter = YoutubeLinkAdapter(youtubeItems)

    val subjectDao = SubjectDAO()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeacherCheckYoutbeListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val detailViews = listOf(binding.youtubeTitleText, binding.youtubeUrlText, binding.tvYoutubeDetailTitle, binding.tvYoutubeDetailUrl, binding.wvYoutube, binding.btnYoutubeDelete)

        initRecycler()

        val webSettings: WebSettings = binding.wvYoutube.settings
        webSettings.javaScriptEnabled = true

        binding.wvYoutube.webChromeClient = WebChromeClient()

        // set click listener that can display the url and video
        youtubeLinkAdapter.setOnItemClickListener(object: YoutubeLinkAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                for (view in detailViews) {
                    view.visibility = View.VISIBLE
                }

                selectedYoutubeItem = youtubeItems!![position]

                binding.tvYoutubeDetailTitle.text = selectedYoutubeItem.title
                binding.tvYoutubeDetailUrl.text = selectedYoutubeItem.url

                loadYouTubeVideo(selectedYoutubeItem.url)
            }
        })

        // set click listener that allow the users to delete youtube link and title
        binding.btnYoutubeDelete.setOnClickListener {
            lateinit var updatedSubject: Subject

            for (view in detailViews) {
                view.visibility = View.INVISIBLE
            }

            subjects!!.forEach { subject ->
                if (subject.subject_key == selectedYoutubeItem.subject_key) {
                    updatedSubject = subject
                    updatedSubject.youTube.remove(Youtube(selectedYoutubeItem.url, selectedYoutubeItem.title))
                }
            }

            var result = false
            lifecycleScope.launch(Dispatchers.IO) {
                result = withContext(Dispatchers.IO)  {
                    subjectDao.updateSubject(updatedSubject)
                }

                if (result) {
                    getYoutubeItem()
                } else {
                    lifecycleScope.launch(Main) {
                        Toast.makeText(
                            this@TeacherCheckYoutbeListActivity,
                            "Fail to delete youtube link. Try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    override fun onResume() {
        getYoutubeItem()
        super.onResume()
    }

    // load youtube video in web video view using youtube video id
    private fun loadYouTubeVideo(url: String) {
        val videoId = extractYouTubeVideoId(url)

        videoId?.let {
            val embedUrl = "https://www.youtube.com/embed/$videoId"
            binding.wvYoutube.loadUrl(embedUrl)
        }
    }

    // extract youtube video id to display only video
    private fun extractYouTubeVideoId(url: String): String? {
        val uri = Uri.parse(url)
        if (uri.host != null && uri.host!!.contains("youtube.com")) {
            val queryParams = uri.query?.split("=")
            if (queryParams != null && queryParams.size == 2 && queryParams[0] == "v") {
                return queryParams[1]
            }
        }
        return null
    }


    // get all youtube links and titles and display them to the users
    fun getYoutubeItem() {
        lifecycleScope.launch(Dispatchers.IO) {
            withContext(Main) {
                binding.pbYoutubeCheck.visibility = View.VISIBLE
            }
            val results = withContext(Dispatchers.IO) {
                // @TODO key값 수정
                subjectDao.getSubjectsByTeacherKey("qxLHhh9StYOfogNqLN9G")
            }

            withContext(Main) {
                subjects = results
                youtubeItems.clear()

                if (subjects != null) {
                    for (subject in subjects!!) {
                        for (youtube in subject.youTube) {
                            val youtubeItem = YoutubeListItem(subject.subject_key, subject.subject_name, youtube.title, youtube.url)
                            youtubeItems.add(youtubeItem)
                        }
                    }

                    youtubeLinkAdapter.youtubeItems = youtubeItems
                    youtubeLinkAdapter.notifyDataSetChanged()

                    binding.pbYoutubeCheck.visibility = View.INVISIBLE
                } else {
                    Toast.makeText(this@TeacherCheckYoutbeListActivity, "Fail to get youtube links. Try again.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // connect adapter with recycler view
    fun initRecycler() {
        if (youtubeItems == null) {
            youtubeLinkAdapter = YoutubeLinkAdapter(ArrayList())
        } else {
            youtubeLinkAdapter = YoutubeLinkAdapter(youtubeItems)
        }

        binding.rvYoutubeList.adapter = youtubeLinkAdapter
        binding.rvYoutubeList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        youtubeLinkAdapter.notifyDataSetChanged()
    }
}