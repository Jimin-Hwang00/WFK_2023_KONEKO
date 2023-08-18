package nepal.swopnasansar.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import nepal.swopnasansar.R
import nepal.swopnasansar.data.StudentDto
import nepal.swopnasansar.data.TeacherDto
import nepal.swopnasansar.data.TempDto
import nepal.swopnasansar.databinding.ActivityStudentListBinding
import nepal.swopnasansar.databinding.ActivityTeacherListBinding

class TeacherListActivity : AppCompatActivity() {
    lateinit var binding : ActivityTeacherListBinding
    lateinit var adapter : TeacherAdapter
    var progressBarVisible = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeacherListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val teacherList = ArrayList<TempDto>()

        adapter = TeacherAdapter(this,teacherList)
        binding.rvTeacherList.adapter = adapter

        binding.rvTeacherList.layoutManager = LinearLayoutManager(this).apply {
            orientation = LinearLayoutManager.VERTICAL
        }

        binding.teacherListEdBt.setOnClickListener{
            val intent = Intent(this@TeacherListActivity, EditTeacherActivity::class.java)
            startActivity(intent)
        }
    }
    // 뷰 홀더가 생성되어 화면에 표시된 후에 ProgressBar를 숨기는 메서드
    fun hideProgressBar() {
        if (progressBarVisible) {
            progressBarVisible = false
            binding.progressBar.visibility = View.INVISIBLE
        }
    }

}