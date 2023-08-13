package nepal.swopnasansar.notice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import nepal.swopnasansar.R
import nepal.swopnasansar.databinding.ActivityParentCheckNoticeBinding
import nepal.swopnasansar.notice.data.RvParentNoticeDto

class ParentCheckNoticeActivity : AppCompatActivity() {
    lateinit var binding : ActivityParentCheckNoticeBinding
    lateinit var adapter : ParentCheckNoticeAdapter
    var progressBarVisible = true
    val TAG = "ParentCheckNoticeActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParentCheckNoticeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = Firebase.firestore
        val checkNoticeList = ArrayList<RvParentNoticeDto>()

        adapter = ParentCheckNoticeAdapter(checkNoticeList, this)
        binding.rvParentNoticeList.adapter = adapter

        binding.rvParentNoticeList.layoutManager = LinearLayoutManager(this).apply {
            orientation = LinearLayoutManager.VERTICAL
        }

        val OnItemClickListener = object : ParentCheckNoticeAdapter.OnItemClickListener {
            override fun onClick(view: View, position: Int) {
                val title = checkNoticeList[position].title
                val content = checkNoticeList[position].content

                binding.titleTv.setText(title)
                binding.contentEditText.setText(content)

                // selectedPosition을 adapter에 설정하여 변경
                adapter.selectedPosition = position
                adapter.notifyDataSetChanged()
            }
        }
        adapter.setOnClickListener(OnItemClickListener)
    }
    // 뷰 홀더가 생성되어 화면에 표시된 후에 ProgressBar를 숨기는 메서드
    fun hideProgressBar() {
        if (progressBarVisible) {
            progressBarVisible = false
            binding.progressBar.visibility = View.INVISIBLE
        }
    }

}