package nepal.swopnasansar.notice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import nepal.swopnasansar.R
import nepal.swopnasansar.data.RvSelectNoticeDto
import nepal.swopnasansar.databinding.ActivityTeacherSelectNoticeBinding
class TeacherSelectNoticeActivity : AppCompatActivity() {
    lateinit var binding : ActivityTeacherSelectNoticeBinding
    lateinit var adapter : TeacherSelectNoticeAdapter
    var progressBarVisible = true
    val TAG = "ClassListActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeacherSelectNoticeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = Firebase.firestore
        val selectNoticeList = ArrayList<RvSelectNoticeDto>()
        var checkedList = ArrayList<RvSelectNoticeDto>()
        var checkedPos = ArrayList<Int>()

        adapter = TeacherSelectNoticeAdapter(selectNoticeList, this)
        binding.rvSelectNotice.adapter = adapter

        binding.rvSelectNotice.layoutManager = LinearLayoutManager(this).apply {
            orientation = LinearLayoutManager.VERTICAL
        }

        val onCheckBoxClickListener = object : TeacherSelectNoticeAdapter.onCheckBoxClickListener {
            override fun onClickCheckBox(flag: Int, position: Int) {
                Log.d(TAG, "${flag}값과 현재 position : ${position}")
                if(flag == 1){
                    checkedList.add(selectNoticeList[position])
                    checkedPos.add(position)
                    for (item in checkedList) {
                        Log.d(TAG, "checkedList item: ${item.class_name}")
                    }
                }else{
                    // flag 값이 0인 경우, 해당 객체를 checkedList에서 제거
                    val targetObject = selectNoticeList[position]
                    checkedList.remove(targetObject)
                    checkedPos.remove(position)
                    for (item in checkedList) {
                        Log.d(TAG, "checkedList item: ${item.class_name}")
                    }
                }
            }
        }
        adapter.setOnCheckBoxClickListener(onCheckBoxClickListener)

        binding.selectBt.setOnClickListener{
            val intent = Intent(this@TeacherSelectNoticeActivity, TeacherSelectNoticeUploadActivity::class.java)
            val studentKeyList : ArrayList<String> = ArrayList()
            val studentNameList : ArrayList<String> = ArrayList()

            for(stn in checkedList){
                studentKeyList.add(stn.student_key)
                studentNameList.add(stn.student_name)
            }

            if(checkedList.size != 0){
                intent.putStringArrayListExtra("stnNameList", studentNameList)
                intent.putStringArrayListExtra("stnKeyList", studentKeyList)
                startActivity(intent)
            }else{
                Toast.makeText(this@TeacherSelectNoticeActivity, "At least one student must be selected.", Toast.LENGTH_SHORT).show()
            }
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