package nepal.swopnasansar.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import nepal.swopnasansar.R
import nepal.swopnasansar.data.TeacherDto
import nepal.swopnasansar.databinding.ActivitySelectTeacherBinding
import nepal.swopnasansar.databinding.ActivityTeacherListBinding

class SelectTeacherActivity : AppCompatActivity() {
    lateinit var binding : ActivitySelectTeacherBinding
    var progressBarVisible = true
    lateinit var adapter : SelectedTeacherAdapter
    val TAG = "SelectTeacherActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectTeacherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val teacherList = ArrayList<TeacherDto>()
        var teacherInfo : TeacherDto
        var checkedPosition: Int = -1
        var checkedTeacher = TeacherDto("", "", "")

        adapter = SelectedTeacherAdapter(this,teacherList)
        binding.rvTeacherList.adapter = adapter

        binding.rvTeacherList.layoutManager = LinearLayoutManager(this).apply {
            orientation = LinearLayoutManager.VERTICAL
        }

        val onCheckBoxClickListener = object : SelectedTeacherAdapter.onCheckBoxClickListener {
            override fun onClickCheckBox(flag: Int, position: Int) {
                Log.d(TAG, "${flag}값과 현재 position : ${position}")

                if(checkedPosition == -1){
                    checkedTeacher = teacherList[position]
                }else{
                    adapter.notifyDataSetChanged()
                    checkedPosition = position
                    checkedTeacher = teacherList[position]
                }
            }
        }
        adapter.setOnCheckBoxClickListener(onCheckBoxClickListener)

        binding.teacherListSelectBt.setOnClickListener{
            val resultIntent = Intent()

            Toast.makeText(this@SelectTeacherActivity, "selected teacher", Toast.LENGTH_SHORT).show()

            teacherInfo = checkedTeacher
            resultIntent.putExtra("selected_teacher", teacherInfo)
            setResult(RESULT_OK, resultIntent)

            finish()
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