package nepal.swopnasansar.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import nepal.swopnasansar.R
import nepal.swopnasansar.data.StudentDto
import nepal.swopnasansar.data.TeacherDto
import nepal.swopnasansar.databinding.ActivitySelectStudentBinding
import nepal.swopnasansar.databinding.ActivityStudentListBinding

class SelectStudentActivity : AppCompatActivity() {
    lateinit var binding : ActivitySelectStudentBinding
    lateinit var adapter : StudentAdapter
    var progressBarVisible = true
    val TAG = "SelectStudentActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val studentList = ArrayList<StudentDto>()
        var checkedList = ArrayList<StudentDto>()
        var checkedPos = ArrayList<Int>()
        var studentListText : String = ""

        adapter = StudentAdapter(this,studentList)
        binding.rvStudentList.adapter = adapter

        binding.rvStudentList.layoutManager = LinearLayoutManager(this).apply {
            orientation = LinearLayoutManager.VERTICAL
        }

        val onCheckBoxClickListener = object : StudentAdapter.onCheckBoxClickListener {
            override fun onClickCheckBox(flag: Int, position: Int) {
                Log.d(TAG, "${flag}값과 현재 position : ${position}")
                if(flag == 1){
                    checkedList.add(studentList[position])
                    checkedPos.add(position)
                    for (item in checkedList) {
                        Log.d(TAG, "checkedList item: ${item.stn_name}")
                    }
                }else{
                    // flag 값이 0인 경우, 해당 객체를 checkedList에서 제거
                    val targetObject = studentList[position]
                    checkedList.remove(targetObject)
                    checkedPos.remove(position)
                    for (item in checkedList) {
                        Log.d(TAG, "checkedList item: ${item.stn_name}")
                    }
                }
            }
        }
        adapter.setOnCheckBoxClickListener(onCheckBoxClickListener)

        binding.studentListSelectBt.setOnClickListener{
            val resultIntent = Intent()
            var studentKeyList : ArrayList<String> = ArrayList()
            Toast.makeText(this@SelectStudentActivity, "selected studentList", Toast.LENGTH_SHORT).show()

            var num = 0
            for( stn in checkedList){
                if(num < checkedList.size){
                    studentListText += "${num + 1} : ${stn.stn_name}   "
                    studentKeyList.add(stn.stn_key)
                }
                num++
            }

            resultIntent.putStringArrayListExtra("selected_student_keys", studentKeyList)
            resultIntent.putExtra("selected_studentList", studentListText)
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