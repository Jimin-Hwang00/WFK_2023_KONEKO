package nepal.swopnasansar.admin

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import nepal.swopnasansar.admin.data.AdminCalDao
import nepal.swopnasansar.admin.data.AdminCalDto
import nepal.swopnasansar.databinding.ActivityCheckEventBinding
import nepal.swopnasansar.databinding.ActivityMainBinding
import java.util.Calendar
import java.util.GregorianCalendar

class CheckEventActivity : AppCompatActivity() {
    lateinit var binding :ActivityCheckEventBinding
    lateinit var adapter : AdminCalAdapter
    val TAG = "CheckEventActivity"
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityCheckEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = Firebase.firestore
        val adminCalList = ArrayList<AdminCalDto>()

        adapter = AdminCalAdapter(adminCalList)
        binding.rvEvent.adapter = adapter

        binding.rvEvent.layoutManager = LinearLayoutManager(this).apply {
            orientation = LinearLayoutManager.VERTICAL
        }

        binding.addBt.setOnClickListener{
            val intent = Intent(this, CreateEventActivity::class.java)
            startActivity(intent) //액티비티 띄우
        }

        val onLongClickListener = object: AdminCalAdapter.OnItemLongClickListener {
            override fun onItemLongClick(view: View, position: Int) {
                AlertDialog.Builder(this@CheckEventActivity).run {
                    setTitle("Delete")
                    setMessage("Delete it?")
                    setNegativeButton("취소", null)
                    setCancelable(false)
                    setPositiveButton("확인", object: DialogInterface.OnClickListener {
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            db.collection("schedule").document(adminCalList[position].schedule_key).delete()
                                .addOnSuccessListener {
                                    adminCalList.removeAt(position) // ArrayList에서 항목 삭제
                                    adapter.notifyDataSetChanged() // 어댑터에 데이터 변경 알림
                                    Toast.makeText(this@CheckEventActivity, "delete success!!", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener { e ->
                                    // 삭제 중에 발생한 오류 처리
                                    Toast.makeText(this@CheckEventActivity, "delete failed: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                    })
                    show()
                }
            }
        }
        adapter.setOnItemLongClickListener(onLongClickListener)

//        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
//            val selectedDate = "$year/${month + 1}"
//            val tempList = ArrayList<AdminCalDto>()
//
//            for (i in adminCalList) {
//                if (i.date.startsWith(selectedDate)) {
//                    tempList.add(i)
//                }
//            }
//            adminCalList.clear()
//            adminCalList.addAll(tempList)
//
//            Log.d(TAG, "일정 바꾸기")
//            adapter.notifyDataSetChanged()
//        }
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }
}
