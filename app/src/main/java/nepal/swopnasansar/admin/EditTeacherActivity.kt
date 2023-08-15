package nepal.swopnasansar.admin

import android.app.AlertDialog
import android.content.DialogInterface
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
import nepal.swopnasansar.admin.data.StudentDto
import nepal.swopnasansar.admin.data.TeacherDto
import nepal.swopnasansar.databinding.ActivityEditStudentBinding
import nepal.swopnasansar.databinding.ActivityEditTeacherBinding
import nepal.swopnasansar.databinding.ListTeacherAndAccountBinding

class EditTeacherActivity : AppCompatActivity() {
    lateinit var binding: ActivityEditTeacherBinding
    lateinit var adapter: TeacherAdapter
    lateinit var itemBinding: ListTeacherAndAccountBinding
    val TAG = "EditTeacherActivity"
    var progressBarVisible = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditTeacherBinding.inflate(layoutInflater)
        itemBinding = ListTeacherAndAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = Firebase.firestore
        val teacherList = ArrayList<TeacherDto>()
        var checkedList = ArrayList<TeacherDto>()
        var checkedPos = ArrayList<Int>()

        adapter = TeacherAdapter(this, teacherList)
        binding.rvTeacherList.adapter = adapter

        binding.rvTeacherList.layoutManager = LinearLayoutManager(this).apply {
            orientation = LinearLayoutManager.VERTICAL
        }


        binding.addTeacherBt.setOnClickListener {
            val name = binding.nameEt.text.toString()
            val email = binding.emailEt.text.toString()

            AlertDialog.Builder(this@EditTeacherActivity).run {
                val intent = Intent(this@EditTeacherActivity, EditTeacherActivity::class.java)
                setTitle("Add Teacher")
                setMessage("Are you sure to add teacher \"${name}\"?")
                setNegativeButton("No", null)
                setCancelable(false)
                setPositiveButton("Yes", object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        // 문서를 추가하고 자동으로 생성된 키 값을 받아옵니다.
                        db.collection("teacher").add(TeacherDto("", name, email))
                            .addOnSuccessListener { documentReference ->
                                val documentId = documentReference.id
                                // 문서 ID를 저장한 뒤 문서에 데이터를 업데이트합니다.
                                db.collection("teacher").document(documentId)
                                    .set(TeacherDto(documentId, name, email))
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            this@EditTeacherActivity,
                                            "Save completed",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                        startActivity(intent)
                                    }
                                    .addOnFailureListener { exception ->
                                        println("Error creating document: $exception")
                                    }
                            }
                            .addOnFailureListener { exception ->
                                println("Error creating document: $exception")
                            }

                        binding.nameEt.setText("")
                        binding.emailEt.setText("")
                        adapter.onUpdateList()
                        adapter.notifyDataSetChanged()
                    }
                })
                show()
            }
        }

        val onCheckBoxClickListener = object : TeacherAdapter.onCheckBoxClickListener {
            override fun onClickCheckBox(flag: Int, position: Int) {
                Log.d(TAG, "${flag}값과 현재 position : ${position}")
                if(flag == 1){
                    checkedList.add(teacherList[position])
                    checkedPos.add(position)
                    for (item in checkedList) {
                        Log.d(TAG, "checkedList item: ${item.teacher_name}")
                    }
                }else{
                    // flag 값이 0인 경우, 해당 객체를 checkedList에서 제거
                    val targetObject = teacherList[position]
                    checkedList.remove(targetObject)
                    checkedPos.remove(position)
                    for (item in checkedList) {
                        Log.d(TAG, "checkedList item: ${item.teacher_name}")
                    }
                }
            }
        }
        adapter.setOnCheckBoxClickListener(onCheckBoxClickListener)

        binding.deleteTeacherBt.setOnClickListener{
            AlertDialog.Builder(this@EditTeacherActivity).run {
                setTitle("delete Teacher(s)")
                setMessage("Are you sure to delete?")
                setNegativeButton("취소", null)
                setCancelable(false)
                setPositiveButton("확인", object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        for(t in checkedList){
                            db.collection("teacher").document(t.teacher_key).delete()
                                .addOnSuccessListener {
                                    adapter.notifyDataSetChanged() // 어댑터에 데이터 변경 알림
                                    Toast.makeText(this@EditTeacherActivity, "delete success!!", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener { e ->
                                    // 삭제 중에 발생한 오류 처리
                                    Toast.makeText(this@EditTeacherActivity, "delete failed: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                        for(pos in checkedPos){
                            teacherList.removeAt(pos)
                            adapter.notifyDataSetChanged() // 어댑터에 데이터 변경 알림
                        }
                        adapter.onUpdateList()
                        adapter.notifyDataSetChanged()
                        checkedList.clear()
                        checkedPos.clear()
                    }
                })
                show()
            }
        }
    }
    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }

    // 뷰 홀더가 생성되어 화면에 표시된 후에 ProgressBar를 숨기는 메서드
    fun hideProgressBar() {
        if (progressBarVisible) {
            progressBarVisible = false
            binding.progressBar.visibility = View.INVISIBLE
        }
    }

}