package nepal.swopnasansar.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import nepal.swopnasansar.R
import nepal.swopnasansar.admin.data.StudentDto
import nepal.swopnasansar.admin.data.TeacherDto
import nepal.swopnasansar.databinding.ActivityStudentListBinding
import nepal.swopnasansar.databinding.ActivityTeacherListBinding

class TeacherListActivity : AppCompatActivity() {
    lateinit var binding : ActivityTeacherListBinding
    lateinit var adapter : TeacherAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeacherListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val teacherList = ArrayList<TeacherDto>()

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
}