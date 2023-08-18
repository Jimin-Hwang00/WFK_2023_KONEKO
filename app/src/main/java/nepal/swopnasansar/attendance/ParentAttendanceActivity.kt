package nepal.swopnasansar.attendance

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import nepal.swopnasansar.dao.AuthDAO
import nepal.swopnasansar.data.RvParentAttDto
import nepal.swopnasansar.databinding.ActivityParentAttendanceBinding
import nepal.swopnasansar.login.CheckRoleActivity

class ParentAttendanceActivity : AppCompatActivity() {
    lateinit var binding : ActivityParentAttendanceBinding
    lateinit var adapter : ParentAttendanceAdapter
    val TAG = "ParentAttendanceActivity"
    var progressBarVisible = true
    private val authDao = AuthDAO()
    val uid = authDao.getUid()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParentAttendanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val attCheckList = ArrayList<RvParentAttDto>()

        if (uid == null) {
            Toast.makeText(applicationContext, "You have to login.", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, CheckRoleActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

        // Adapter 초기화
        adapter = ParentAttendanceAdapter(attCheckList, this)

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