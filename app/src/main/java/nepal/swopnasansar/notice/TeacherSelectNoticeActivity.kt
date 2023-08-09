package nepal.swopnasansar.notice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import nepal.swopnasansar.R
import nepal.swopnasansar.databinding.ActivityTeacherSelectNoticeBinding
import nepal.swopnasansar.notice.data.RvSelectNoticeDto

class TeacherSelectNoticeActivity : AppCompatActivity() {
    lateinit var binding : ActivityTeacherSelectNoticeBinding
    lateinit var adapter : TeacherSelectNoticeAdapter
    val TAG = "ClassListActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeacherSelectNoticeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = Firebase.firestore
        val selectNoticeList = ArrayList<RvSelectNoticeDto>()
        var checkedList = ArrayList<RvSelectNoticeDto>()
        var checkedPos = ArrayList<Int>()

        adapter = TeacherSelectNoticeAdapter(selectNoticeList)
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

            intent.putStringArrayListExtra("stnNameList", studentNameList)
            intent.putStringArrayListExtra("stnKeyList", studentKeyList)
            startActivity(intent)
        }
    }
}