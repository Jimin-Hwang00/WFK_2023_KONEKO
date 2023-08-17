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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import nepal.swopnasansar.R
import nepal.swopnasansar.data.AccountantDto
import nepal.swopnasansar.data.AdminCalDto
import nepal.swopnasansar.data.StudentDto
import nepal.swopnasansar.databinding.ActivityEditListBinding
import nepal.swopnasansar.databinding.ActivityEditStudentBinding
import nepal.swopnasansar.databinding.ActivityStudentListBinding
import nepal.swopnasansar.databinding.ListItemBinding
import nepal.swopnasansar.databinding.ListStudentBinding

class EditStudentActivity : AppCompatActivity() {
    lateinit var binding: ActivityEditStudentBinding
    lateinit var adapter: StudentAdapter
    lateinit var itemBinding: ListStudentBinding
    val TAG = "EditStudentActivity"
    var progressBarVisible = true
    val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditStudentBinding.inflate(layoutInflater)
        itemBinding = ListStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = Firebase.firestore
        val studentList = ArrayList<StudentDto>()
        var checkedList = ArrayList<StudentDto>()
        var checkedPos = ArrayList<Int>()

        adapter = StudentAdapter(this, studentList)
        binding.rvStudentList.adapter = adapter

        binding.rvStudentList.layoutManager = LinearLayoutManager(this).apply {
            orientation = LinearLayoutManager.VERTICAL
        }


        binding.addStnBt.setOnClickListener {
            val name = binding.nameEt.text.toString()
            val email = binding.emailEt.text.toString()

            AlertDialog.Builder(this@EditStudentActivity).run {
                val intent = Intent(this@EditStudentActivity, EditStudentActivity::class.java)
                setTitle("Add Student")
                setMessage("Are you sure to add student \"${name}\"?")
                setNegativeButton("No", null)
                setCancelable(false)
                setPositiveButton("Yes", object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        CoroutineScope(Dispatchers.Main).launch {
                            try {
                                val authResult = auth.createUserWithEmailAndPassword(email, email.substringBefore("@")).await()
                                val user: FirebaseUser? = authResult.user
                                var uid = user?.uid ?: ""
                                println("User UID: $uid")

                                if(!uid.equals("")){
                                    // 문서 ID를 저장한 뒤 문서에 데이터를 업데이트합니다.
                                    db.collection("student").document(uid)
                                        .set(StudentDto(uid, name, email, "", false))
                                        .addOnSuccessListener {
                                            Toast.makeText(
                                                this@EditStudentActivity,
                                                "Save completed",
                                                Toast.LENGTH_SHORT
                                            )
                                                .show()
                                            startActivity(intent)
                                        }
                                        .addOnFailureListener { exception ->
                                            println("Error creating document: $exception")
                                        }
                                    binding.nameEt.setText("")
                                    binding.emailEt.setText("")
                                    adapter.onUpdateList()
                                    adapter.notifyDataSetChanged()
                                }

                            } catch (e: FirebaseAuthUserCollisionException) {
                                // 이미 등록된 사용자인 경우 처리
                                Toast.makeText(this@EditStudentActivity,
                                    "The email address is already registered.", Toast.LENGTH_LONG).show()
                                println("Registration Error: ${e.message}")
                            } catch (e: FirebaseAuthInvalidCredentialsException) {
                                // 유효하지 않은 이메일 값인 경우 처리
                                Toast.makeText(this@EditStudentActivity,
                                    "The email address is invalid.", Toast.LENGTH_LONG).show()
                                println("Registration Error: ${e.message}")
                            } catch (e: Exception) {
                                // 기타 등록 오류 처리
                                Toast.makeText(this@EditStudentActivity,
                                    "Please try again.", Toast.LENGTH_LONG).show()
                                println("Registration Error: ${e.message}")
                            }
                        }
                    }
                })
                show()
            }
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

        binding.deleteStnBt.setOnClickListener{
            AlertDialog.Builder(this@EditStudentActivity).run {
                setTitle("delete Student(s)")
                setMessage("Are you sure to delete?")
                setNegativeButton("취소", null)
                setCancelable(false)
                setPositiveButton("확인", object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        for(stn in checkedList){
                            db.collection("student").document(stn.stn_key).delete()
                                .addOnSuccessListener {
                                    adapter.notifyDataSetChanged() // 어댑터에 데이터 변경 알림
                                    Toast.makeText(this@EditStudentActivity, "delete success!!", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener { e ->
                                    // 삭제 중에 발생한 오류 처리
                                    Toast.makeText(this@EditStudentActivity, "delete failed: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                        for(pos in checkedPos){
                            studentList.removeAt(pos)
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

