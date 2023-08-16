package nepal.swopnasansar.attendance

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import nepal.swopnasansar.R
import nepal.swopnasansar.attendance.data.RvParentAttDto
import nepal.swopnasansar.attendance.data.RvTeacherAttDto
import nepal.swopnasansar.attendance.data.StnAttDto
import nepal.swopnasansar.databinding.ActivityParentAttendanceBinding
import nepal.swopnasansar.databinding.ActivityTeacherAttendanceBinding

class ParentAttendanceActivity : AppCompatActivity() {
    lateinit var binding : ActivityParentAttendanceBinding
    lateinit var adapter : ParentAttendanceAdapter
    val TAG = "ParentAttendanceActivity"
    var progressBarVisible = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParentAttendanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val attCheckList = ArrayList<RvParentAttDto>()

        // Adapter 초기화
        adapter = ParentAttendanceAdapter(attCheckList)

        binding.rvParentAtt.layoutManager = LinearLayoutManager(this).apply {
            orientation = LinearLayoutManager.VERTICAL
        }
        binding.rvParentAtt.adapter = adapter
    }
    // 뷰 홀더가 생성되어 화면에 표시된 후에 ProgressBar를 숨기는 메서드
    fun hideProgressBar() {
        if (progressBarVisible) {
            progressBarVisible = false
            binding.progressBar.visibility = View.INVISIBLE
        }
    }
}