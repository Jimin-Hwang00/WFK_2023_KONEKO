package nepal.swopnasansar.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import nepal.swopnasansar.R
import nepal.swopnasansar.admin.data.StudentDto
import nepal.swopnasansar.databinding.ActivityEditStudentBinding
import nepal.swopnasansar.databinding.ActivityStudentListBinding

class StudentListActivity : AppCompatActivity() {
    lateinit var binding : ActivityStudentListBinding
    lateinit var adapter : StudentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val studentList = ArrayList<StudentDto>()

        adapter = StudentAdapter(this,studentList)
        binding.rvStudentList.adapter = adapter

        binding.rvStudentList.layoutManager = LinearLayoutManager(this).apply {
            orientation = LinearLayoutManager.VERTICAL
        }

        binding.studentListEdBt.setOnClickListener{
            val intent = Intent(this@StudentListActivity, EditStudentActivity::class.java)
            startActivity(intent)
        }
    }
}