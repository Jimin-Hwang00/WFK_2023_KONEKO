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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
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
import nepal.swopnasansar.data.TeacherDto
import nepal.swopnasansar.data.TempDto
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
    var firestore : FirebaseFirestore? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditStudentBinding.inflate(layoutInflater)
        itemBinding = ListStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = Firebase.firestore
        val studentList = ArrayList<TempDto>()
        var checkedList = ArrayList<TempDto>()
        var checkedPos = ArrayList<Int>()

        adapter = StudentAdapter(this, studentList)
        binding.rvStudentList.adapter = adapter

        binding.rvStudentList.layoutManager = LinearLayoutManager(this).apply {
            orientation = LinearLayoutManager.VERTICAL
        }


        binding.addStnBt.setOnClickListener {
            firestore = FirebaseFirestore.getInstance()
            var students : String = ""

            for(stn in checkedList){
                students += "${stn.name} "
            }

            if(checkedList.size != 0) {
                AlertDialog.Builder(this@EditStudentActivity).run {
                    val intent = Intent(this@EditStudentActivity, EditStudentActivity::class.java)
                    setTitle("Accept Student(s)")
                    setMessage("Are you sure to accept student(s) \"${students}\"?")
                    setNegativeButton("No", null)
                    setCancelable(false)
                    setPositiveButton("Yes", object : DialogInterface.OnClickListener {
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            CoroutineScope(Dispatchers.IO).launch {
                                for(stn in checkedList){
                                    try {
                                        //auth 등록
                                        val authResult = auth.createUserWithEmailAndPassword(stn.email, stn.email.substringBefore("@")).await()
                                        val user: FirebaseUser? = authResult.user
                                        var uid = user?.uid ?: ""
                                        println("User UID: $uid")

                                        if(!uid.equals("")){
                                            // 문서 ID를 저장한 뒤 문서에 데이터를 업데이트합니다.
                                            db.collection("student").document(uid)
                                                .set(StudentDto(uid, stn.name, stn.email, "", false))
                                                .addOnSuccessListener {
                                                    Toast.makeText(
                                                        this@EditStudentActivity,
                                                        "Accept completed",
                                                        Toast.LENGTH_SHORT
                                                    )
                                                        .show()
                                                    startActivity(intent)
                                                }
                                                .addOnFailureListener { exception ->
                                                    println("Error creating document: $exception")
                                                }
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

                                    //temp에서 삭제
                                    db.collection("temp").document(stn.email).delete()
                                        .addOnSuccessListener {
                                            adapter.notifyDataSetChanged() // 어댑터에 데이터 변경 알림
                                            Log.d(TAG, "accept student(s)")
                                        }
                                        .addOnFailureListener { e ->
                                            // 삭제 중에 발생한 오류 처리
                                            Toast.makeText(this@EditStudentActivity, "delete failed: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }.await()
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
                        }
                    })
                    show()
                }
            } else{
                Toast.makeText(this@EditStudentActivity, "Please check the student(s)", Toast.LENGTH_LONG).show()
            }
        }

        val onCheckBoxClickListener = object : StudentAdapter.onCheckBoxClickListener {
            override fun onClickCheckBox(flag: Int, position: Int) {
                Log.d(TAG, "${flag}값과 현재 position : ${position}")
                if(flag == 1){
                    checkedList.add(studentList[position])
                    checkedPos.add(position)
                    for (item in checkedList) {
                        Log.d(TAG, "checkedList item: ${item.email}")
                    }
                }else{
                    // flag 값이 0인 경우, 해당 객체를 checkedList에서 제거
                    val targetObject = studentList[position]
                    checkedList.remove(targetObject)
                    checkedPos.remove(position)
                    for (item in checkedList) {
                        Log.d(TAG, "checkedList item: ${item.email}")
                    }
                }
            }
        }
        adapter.setOnCheckBoxClickListener(onCheckBoxClickListener)

        binding.deleteStnBt.setOnClickListener{
            AlertDialog.Builder(this@EditStudentActivity).run {
                setTitle("delete Student(s)")
                setMessage("Are you sure to delete?")
                setNegativeButton("No", null)
                setCancelable(false)
                setPositiveButton("Yes", object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        for(stn in checkedList){
                            db.collection("temp").document(stn.email).delete()
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
    fun isEmailValid(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})\$".toRegex()
        return email.matches(emailRegex)
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

