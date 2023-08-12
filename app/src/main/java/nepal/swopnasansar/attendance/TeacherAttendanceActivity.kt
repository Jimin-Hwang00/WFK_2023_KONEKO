package nepal.swopnasansar.attendance

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nepal.swopnasansar.R
import nepal.swopnasansar.attendance.data.AttendanceDto
import nepal.swopnasansar.attendance.data.RvTeacherAttDto
import nepal.swopnasansar.attendance.data.StnAttDto
import nepal.swopnasansar.databinding.ActivityTeacherAttendanceBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TeacherAttendanceActivity : AppCompatActivity() {
    lateinit var binding : ActivityTeacherAttendanceBinding
    lateinit var adapter : TeacherAttendanceAdapter
    val TAG = "TeacherAttendanceActivity"
    var progressBarVisible = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeacherAttendanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = Firebase.firestore
        val attCheckList = ArrayList<RvTeacherAttDto>()
        var checkedList = ArrayList<StnAttDto>()
        val pref : SharedPreferences = getSharedPreferences("save_state", 0)
        val editor : SharedPreferences.Editor = pref.edit()
        var attKey = pref.getString("attKey", null)?.toString() ?: ""
        var todayDate = getTodayDate()

        if(!pref.getString("date", null).equals(todayDate)){
            attKey = ""
        }

        // Adapter 초기화
        adapter = TeacherAttendanceAdapter(attCheckList, attKey)

        binding.rvTeacherAtt.layoutManager = LinearLayoutManager(this).apply {
            orientation = LinearLayoutManager.VERTICAL
        }
        binding.rvTeacherAtt.adapter = adapter

        val OnItemClickListener = object : TeacherAttendanceAdapter.OnItemClickListener {
            override fun onClick(view: View, position: Int) {
                adapter.notifyDataSetChanged()
                adapter.onUpdateList()
            }
        }
        adapter.setOnClickListener(OnItemClickListener)

        binding.attCheckBt.setOnClickListener {

            for(att in attCheckList){
                if(att.check.equals("O")){
                    checkedList.add(StnAttDto(att.stn_key, att.stn_name, true))
                }else if(att.check.equals("X")){
                    checkedList.add(StnAttDto(att.stn_key, att.stn_name, false))
                }
            }

            if(pref.getString("date", null).equals(attCheckList[0].date)){
                val attKey = pref.getString("attKey", null).toString()
                // 문서 ID를 저장한 뒤 문서에 데이터를 업데이트합니다.
                db.collection("attendance").document(attKey).set(AttendanceDto(attKey,
                    attCheckList[0].date, checkedList
                ))
                    .addOnSuccessListener {
                        checkedList.clear()
                        Toast.makeText(this@TeacherAttendanceActivity, "Save Completed", Toast.LENGTH_LONG).show()
                    }
                    .addOnFailureListener { exception ->
                        println("Error creating document: $exception")
                    }
            }else{
                // 문서를 추가하고 자동으로 생성된 키 값을 받아옵니다.
                db.collection("attendance").add(AttendanceDto("", "", ArrayList<StnAttDto>()))
                    .addOnSuccessListener { documentReference ->
                        val documentId = documentReference.id

                        editor.putString("attKey", documentId)
                        editor.putString("date", attCheckList[0].date)
                        editor.commit()
                        // 문서 ID를 저장한 뒤 문서에 데이터를 업데이트합니다.
                        db.collection("attendance").document(documentId).set(AttendanceDto(documentId,
                            attCheckList[0].date, checkedList
                        ))
                            .addOnSuccessListener {
                                checkedList.clear()
                                Toast.makeText(this@TeacherAttendanceActivity, "Save Completed", Toast.LENGTH_LONG).show()
                            }
                            .addOnFailureListener { exception ->
                                println("Error creating document: $exception")
                            }
                    }
                    .addOnFailureListener { exception ->
                        println("Error creating document: $exception")
                    }
            }
        }
    }
    fun getTodayDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = Date()
        return dateFormat.format(today)
    }

    // 뷰 홀더가 생성되어 화면에 표시된 후에 ProgressBar를 숨기는 메서드
    fun hideProgressBar() {
        if (progressBarVisible) {
            progressBarVisible = false
            binding.progressBar.visibility = View.INVISIBLE
        }
    }
}