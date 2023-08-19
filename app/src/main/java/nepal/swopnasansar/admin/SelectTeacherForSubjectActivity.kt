package nepal.swopnasansar.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nepal.swopnasansar.dao.ClassDAO
import nepal.swopnasansar.dao.TeacherDAO
import nepal.swopnasansar.dto.Class
import nepal.swopnasansar.databinding.ActivitySelectClassBinding
import nepal.swopnasansar.databinding.ActivitySelectTeacherBinding
import nepal.swopnasansar.dto.Teacher

class SelectTeacherForSubjectActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectTeacherBinding

    private val teacherDao = TeacherDAO()

    private var selectedTeacherForSubjectAdapter: SelectedTeacherForSubjectAdapter? = null
    private var teachers: ArrayList<Teacher>? = ArrayList()
    private var selectedTeacher: Teacher? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectTeacherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecycler()

        Log.d("SelectTeacherForSubjectActivity", "selected : ${selectedTeacher}")

        binding.teacherListSelectBt.setOnClickListener {
            Log.d("SelectTeacherForSubjectActivity", "selected : ${selectedTeacher}")
            if (selectedTeacher != null) {
                Log.d("SelectTeacherForSubjectActivity", "selected : ${selectedTeacher}")
                val intent = Intent(this@SelectTeacherForSubjectActivity, CreateSubjectActivity::class.java)
                intent.putExtra("teacher_key", selectedTeacher?.teacher_key)
                intent.putExtra("teacher_name", selectedTeacher?.teacher_name)
                setResult(RESULT_OK, intent)
                finish()
            } else {
                Toast.makeText(this@SelectTeacherForSubjectActivity, "Please select teacher.", Toast.LENGTH_SHORT).show()
            }
        }

        selectedTeacherForSubjectAdapter?.setItemClickListener(object: SelectedTeacherForSubjectAdapter.ItemClickListener {
            override fun onClick(view: View, classItem: Teacher) {
                selectedTeacher = classItem
            }
        })
    }

    override fun onResume() {
        lifecycleScope.launch {
            binding.progressBar.visibility = View.VISIBLE

            val teachers = withContext(Dispatchers.IO) {
                teacherDao.getAllTeachers()
            }

            if (teachers != null) {
                selectedTeacherForSubjectAdapter?.items = teachers
                selectedTeacherForSubjectAdapter?.notifyDataSetChanged()
                binding.progressBar.visibility = View.INVISIBLE
            } else {
                Toast.makeText(this@SelectTeacherForSubjectActivity, "Fail to get classes. Try again.", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.INVISIBLE
            }
        }
        super.onResume()
    }

    private fun initRecycler() {
        selectedTeacherForSubjectAdapter = SelectedTeacherForSubjectAdapter()

        if (teachers != null) {
            selectedTeacherForSubjectAdapter?.setItem(teachers!!)
        } else {
            selectedTeacherForSubjectAdapter?.setItem(ArrayList())
        }

        binding.rvTeacherList.adapter = selectedTeacherForSubjectAdapter
        binding.rvTeacherList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }
}