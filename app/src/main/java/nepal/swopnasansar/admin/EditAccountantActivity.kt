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
import com.google.firebase.auth.FirebaseAuthException
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
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import nepal.swopnasansar.R
import nepal.swopnasansar.data.AccountantDto
import nepal.swopnasansar.data.ClassDto
import nepal.swopnasansar.data.StudentDto
import nepal.swopnasansar.data.SubjectDto
import nepal.swopnasansar.data.TeacherDto
import nepal.swopnasansar.data.TempDto
import nepal.swopnasansar.databinding.ActivityAccountantListBinding
import nepal.swopnasansar.databinding.ActivityEditAccountantBinding
import nepal.swopnasansar.databinding.ActivityEditTeacherBinding
import nepal.swopnasansar.databinding.ListTeacherAndAccountBinding

class EditAccountantActivity : AppCompatActivity() {
    lateinit var binding: ActivityEditAccountantBinding
    lateinit var adapter: AccountantAdapter
    lateinit var itemBinding: ListTeacherAndAccountBinding
    val TAG = "EditAccountantActivity"
    var progressBarVisible = true
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    var firestore : FirebaseFirestore? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAccountantBinding.inflate(layoutInflater)
        itemBinding = ListTeacherAndAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = Firebase.firestore
        val accountantList = ArrayList<TempDto>()
        var checkedList = ArrayList<TempDto>()
        var checkedPos = ArrayList<Int>()

        adapter = AccountantAdapter(this, accountantList)
        binding.rvAccountantList.adapter = adapter

        binding.rvAccountantList.layoutManager = LinearLayoutManager(this).apply {
            orientation = LinearLayoutManager.VERTICAL
        }


        binding.addAccountantBt.setOnClickListener {
            firestore = FirebaseFirestore.getInstance()
            val name = binding.nameEt.text.toString()
            val email = binding.emailEt.text.toString()
            val tempList = ArrayList<TempDto>()
            val teacherList = ArrayList<TeacherDto>()
            val accountList = ArrayList<AccountantDto>()
            val studentList = ArrayList<StudentDto>()

            AlertDialog.Builder(this@EditAccountantActivity).run {
                val intent = Intent(this@EditAccountantActivity, EditAccountantActivity::class.java)
                setTitle("Add Accountant")
                setMessage("Are you sure to add Accountant \"${name}\"?")
                setNegativeButton("No", null)
                setCancelable(false)
                setPositiveButton("Yes", object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        //이미 존재하는 이메일 확인
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                tempList.clear()

                                val tempQuerySnapshot = firestore?.collection("temp")?.whereEqualTo("email", email)?.get()?.await()
                                tempList.addAll(tempQuerySnapshot?.toObjects(TempDto::class.java) ?: emptyList())

                                val teacherQuerySnapshot = firestore?.collection("teacher")?.whereEqualTo("email", email)?.get()?.await()
                                teacherList.addAll(teacherQuerySnapshot?.toObjects(TeacherDto::class.java) ?: emptyList())

                                val studentQuerySnapshot = firestore?.collection("student")?.whereEqualTo("email", email)?.get()?.await()
                                studentList.addAll(studentQuerySnapshot?.toObjects(StudentDto::class.java) ?: emptyList())

                                val accountantQuerySnapshot = firestore?.collection("accountant")?.whereEqualTo("email", email)?.get()?.await()
                                accountList.addAll(accountantQuerySnapshot?.toObjects(AccountantDto::class.java) ?: emptyList())

                                //겹치는 이메일이 없을 경우 사용
                                if(tempList.size == 0 && teacherList.size == 0 && accountList.size == 0 && studentList.size == 0){
                                    // 문서 ID를 저장한 뒤 문서에 데이터를 업데이트합니다.
                                    if(isEmailValid(email)){
                                        Firebase.auth.fetchSignInMethodsForEmail(email)
                                            .addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    val signInMethods = task.result?.signInMethods
                                                    if (signInMethods.isNullOrEmpty()) {
                                                        // 해당 이메일로 등록된 사용자가 없음
                                                        db.collection("temp").document(email)
                                                            .set(TempDto(email, name, "accountant"))
                                                            .addOnSuccessListener {
                                                                Toast.makeText(
                                                                    this@EditAccountantActivity,
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

                                                        Log.d(TAG, "해당 이메일로 등록된 사용자가 없습니다.")
                                                    } else {
                                                        // 해당 이메일로 이미 사용자가 등록됨
                                                        Log.d(TAG, "해당 이메일로 이미 사용자가 등록되었습니다.")
                                                    }
                                                } else {
                                                    Log.e(TAG, "사용자 조회 실패:", task.exception)
                                                }
                                            }
                                    }else{
                                        runOnUiThread {
                                            Toast.makeText(
                                                this@EditAccountantActivity,
                                                "Invalid email format",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                }else{
                                    runOnUiThread {
                                        Toast.makeText(
                                            this@EditAccountantActivity,
                                            "This email already exists",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }

                            } catch (e: Exception) {
                                // 오류 처리
                                println("Error: ${e.message}")
                            }
                        }
                    }
                })
                show()
            }
        }

        val onCheckBoxClickListener = object : AccountantAdapter.onCheckBoxClickListener {
            override fun onClickCheckBox(flag: Int, position: Int) {
                Log.d(TAG, "${flag}값과 현재 position : ${position}")
                if(flag == 1){
                    checkedList.add(accountantList[position])
                    checkedPos.add(position)
                    for (item in checkedList) {
                        Log.d(TAG, "checkedList item: ${item.email}")
                    }
                }else{
                    // flag 값이 0인 경우, 해당 객체를 checkedList에서 제거
                    val targetObject = accountantList[position]
                    checkedList.remove(targetObject)
                    checkedPos.remove(position)
                    for (item in checkedList) {
                        Log.d(TAG, "checkedList item: ${item.email}")
                    }
                }
            }
        }
        adapter.setOnCheckBoxClickListener(onCheckBoxClickListener)

        binding.deleteAccountantBt.setOnClickListener {
            AlertDialog.Builder(this@EditAccountantActivity).run {
                setTitle("delete Accountant(s)")
                setMessage("Are you sure to delete?")
                setNegativeButton("No", null)
                setCancelable(false)
                for (item in accountantList) {
                    Log.d(TAG, "adapter accountant item: ${item.email}, ${item.email}")
                }
                setPositiveButton("Yes", object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        val tempCheckedList = ArrayList<TempDto>() // 임시 리스트 생성
                        for (i in checkedPos.indices) {
                            val pos = checkedPos[i]
                            if (pos >= 0 && pos < accountantList.size) { // 유효한 위치인지 확인
                                val ac = accountantList[pos]
                                Log.d(TAG, "${ac.email}")
                                db.collection("temp").document(ac.email).delete()
                                    .addOnSuccessListener {
                                        adapter.onUpdateList()
                                        adapter.notifyDataSetChanged()
                                        Toast.makeText(this@EditAccountantActivity, "delete success!!", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("DeleteError", "Delete failed for accountant key: ${ac.email}, Error: ${e.message}")
                                        Toast.makeText(this@EditAccountantActivity, "delete failed: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                tempCheckedList.add(ac) // 삭제할 데이터를 임시 리스트에 추가
                            }
                        }
                        accountantList.removeIf { ac -> tempCheckedList.any { it.email == ac.email } } // 삭제할 데이터를 실제 리스트에서 제거
                        adapter.onUpdateList()
                        adapter.notifyDataSetChanged()
                        checkedPos.clear()
                        checkedList.clear()
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