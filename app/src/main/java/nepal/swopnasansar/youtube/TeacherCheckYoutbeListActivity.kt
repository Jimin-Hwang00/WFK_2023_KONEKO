package nepal.swopnasansar.youtube

import android.content.Intent
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
import nepal.swopnasansar.dao.AuthDAO
import nepal.swopnasansar.databinding.ActivityTeacherCheckYoutbeListBinding
import nepal.swopnasansar.dao.SubjectDAO
import nepal.swopnasansar.dto.Youtube
import nepal.swopnasansar.dto.Subject
import nepal.swopnasansar.dto.YoutubeListItem
import nepal.swopnasansar.login.CheckRoleActivity

class TeacherCheckYoutbeListActivity : AppCompatActivity() {
    private val TAG = "TeacherCheckYoutubeList"

    private lateinit var binding: ActivityTeacherCheckYoutbeListBinding

    val youtubeItems = ArrayList<YoutubeListItem>()
    var selectedYoutubeItem = YoutubeListItem()

    private var youtubeLinkAdapter = YoutubeLinkAdapter(youtubeItems)

    private val authDao = AuthDAO()
    private val subjectDao = SubjectDAO()

    val uid = authDao.getUid()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeacherCheckYoutbeListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val detailViews = listOf(binding.youtubeTitleText, binding.youtubeUrlText, binding.tvYoutubeDetailTitle, binding.tvYoutubeDetailUrl, binding.wvYoutube, binding.btnYoutubeDelete)

        initRecycler()

        if (uid == null) {
            Toast.makeText(applicationContext, "You have to login.", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, CheckRoleActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

        val webSettings: WebSettings = binding.wvYoutube.settings
        webSettings.javaScriptEnabled = true

        binding.wvYoutube.webChromeClient = WebChromeClient()

        // set click listener that can display the url and video
        youtubeLinkAdapter.setOnItemClickListener(object: YoutubeLinkAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                for (view in detailViews) {
                    view.visibility = View.VISIBLE
                }

                youtubeLinkAdapter.selectedIdx = position
                youtubeLinkAdapter.notifyDataSetChanged()

                selectedYoutubeItem = youtubeItems!![position]

                binding.tvYoutubeDetailTitle.text = selectedYoutubeItem.title
                binding.tvYoutubeDetailUrl.text = selectedYoutubeItem.url

                loadYouTubeVideo(selectedYoutubeItem.url)
            }
        })

        // set click listener that allow the users to delete youtube link and title
        binding.btnYoutubeDelete.setOnClickListener {
            for (view in detailViews) {
                view.visibility = View.INVISIBLE
            }

            lifecycleScope.launch(Dispatchers.IO) {
                val result = withContext(Dispatchers.IO)  {
                    subjectDao.removeYoutube(selectedYoutubeItem.subject_key, Youtube(selectedYoutubeItem.url, selectedYoutubeItem.title))
                }

                if (result) {
                    getYoutubeItem()
                    youtubeLinkAdapter.selectedIdx = -1

                    lifecycleScope.launch(Main) {
                        youtubeLinkAdapter.notifyDataSetChanged()
                        Toast.makeText(this@TeacherCheckYoutbeListActivity, "Success to delete youtube link.", Toast.LENGTH_SHORT).show()
                    }
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

        binding.ivPlusBtn.setOnClickListener {
            val intent = Intent(this, TeacherPostYoutubeLinkActivity::class.java)
            startActivity(intent)
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
        youtubeItems.clear()

        lifecycleScope.launch(Dispatchers.IO) {
            val subjects: ArrayList<Subject>? = withContext(Dispatchers.IO) {
                subjectDao.getSubjectByTeacherKey(uid!!)
            }

            withContext(Main) {
                if (subjects != null) {
                    for (subject in subjects!!) {
                        for (youtube in subject.youTube) {
                            val youtubeItem = YoutubeListItem(subject.subject_key, subject.subject_name, youtube.title, youtube.url)
                            youtubeItems.add(youtubeItem)
                        }
                    }
                }

                binding.pbYoutubeCheck.visibility = View.INVISIBLE

                if (youtubeItems != null) {
                    youtubeLinkAdapter.youtubeItems = youtubeItems
                    youtubeLinkAdapter.notifyDataSetChanged()
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