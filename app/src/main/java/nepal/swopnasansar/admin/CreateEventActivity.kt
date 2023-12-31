package nepal.swopnasansar.admin

import android.R
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import nepal.swopnasansar.data.AdminCalDao
import nepal.swopnasansar.data.AdminCalDto
import nepal.swopnasansar.data.ClassDto
import nepal.swopnasansar.databinding.ActivityCreateEventBinding
import java.util.Calendar
import java.util.GregorianCalendar


class CreateEventActivity : AppCompatActivity() {
    lateinit var binding : ActivityCreateEventBinding
    val TAG = "CreateEventActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateEventBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val db = Firebase.firestore

        binding.calendarBt.setOnClickListener{
            val today = GregorianCalendar()
            val year: Int = today.get(Calendar.YEAR)
            val month: Int = today.get(Calendar.MONTH)
            val date: Int = today.get(Calendar.DATE)
            val dlg = DatePickerDialog(this, object : DatePickerDialog.OnDateSetListener {
                override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {

                    binding.dateTv.setText("${year}-${month+1}-${dayOfMonth}")
                }
            }, year, month, date)
            dlg.show()
        }

        binding.eventSaveBt.setOnClickListener {
            val intent = Intent(this, CheckEventActivity::class.java)
            val event = binding.eventEt.text.toString()
            val date = binding.dateTv.text.toString()

            AlertDialog.Builder(this@CreateEventActivity).run {
                setTitle("Add Class")
                setMessage("Are you sure to add \"date : ${date} event : ${event}\"?")
                setNegativeButton("No", null)
                setCancelable(false)
                setPositiveButton("Yes", object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        // 문서를 추가하고 자동으로 생성된 키 값을 받아옵니다.
                        db.collection("schedule").add(AdminCalDto("", event, date))
                            .addOnSuccessListener { documentReference ->
                                val documentId = documentReference.id
                                // 문서 ID를 저장한 뒤 문서에 데이터를 업데이트합니다.
                                db.collection("schedule").document(documentId).set(AdminCalDto(documentId, event, date))
                                    .addOnSuccessListener {
                                        Toast.makeText(this@CreateEventActivity, "Save completed", Toast.LENGTH_SHORT).show()
                                        startActivity(intent) //액티비티 띄우기
                                    }
                                    .addOnFailureListener { exception ->
                                        println("Error creating document: $exception")
                                    }
                            }
                            .addOnFailureListener { exception ->
                                println("Error creating document: $exception")
                            }
                    }
                })
                show()
            }
        }
    }
}