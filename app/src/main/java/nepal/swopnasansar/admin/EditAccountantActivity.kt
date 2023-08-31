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
import kotlinx.coroutines.withContext
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
            var accountant : String = ""

            for(ac in checkedList){
                accountant += "${ac.name}"
            }

            if(checkedList.size == 1) {
                AlertDialog.Builder(this@EditAccountantActivity).run {
                    val intent = Intent(this@EditAccountantActivity, EditAccountantActivity::class.java)
                    setTitle("Accept Accountant(s)")
                    setMessage("Are you sure to accept Accountant(s) \"${accountant}\"?")
                    setNegativeButton("No", null)
                    setCancelable(false)
                    setPositiveButton("Yes", object : DialogInterface.OnClickListener {
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            CoroutineScope(Dispatchers.IO).launch {
                                for(ac in checkedList){
                                    //auth 등록
                                    Log.d(TAG, "auth 등록완료")
                                    val authResult = auth.createUserWithEmailAndPassword(ac.email, ac.email.substringBefore("@")).await()
                                    val user: FirebaseUser? = authResult.user
                                    var uid = user?.uid ?: ""
                                    println("User UID: $uid")

                                    if(!uid.equals("")){
                                        // 문서 ID를 저장한 뒤 문서에 데이터를 업데이트합니다.
                                        db.collection("accountant").document(uid)
                                            .set(AccountantDto(uid, ac.name, ac.email))
                                            .addOnSuccessListener {
                                                Log.d(TAG, "accept Teacher${ac.email}")
                                            }
                                            .addOnFailureListener { exception ->
                                                println("Error creating document: $exception")
                                            }
                                    }


                                    //temp에서 삭제
                                    db.collection("temp").document(ac.email).delete()
                                        .addOnSuccessListener {
                                            adapter.notifyDataSetChanged() // 어댑터에 데이터 변경 알림
                                            Log.d(TAG, "accept teacher(s)")
                                        }
                                        .addOnFailureListener { e ->
                                            // 삭제 중에 발생한 오류 처리
                                            Toast.makeText(this@EditAccountantActivity, "delete failed: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                }
                                for(pos in checkedPos){
                                    accountantList.removeAt(pos)
                                    adapter.notifyDataSetChanged() // 어댑터에 데이터 변경 알림
                                }
                                withContext(Dispatchers.Main) {
                                    adapter.onUpdateList()
                                    adapter.notifyDataSetChanged()
                                }
                            }
                            Toast.makeText(
                                this@EditAccountantActivity,
                                "Accept completed",
                                Toast.LENGTH_SHORT
                            )
                                .show()

                            adapter.checkBoxList.clear()
                            checkedList.clear()
                            checkedPos.clear()
                        }
                    })
                    show()
                }
            } else{
                Toast.makeText(this@EditAccountantActivity, "Please check one person at a time", Toast.LENGTH_LONG).show()
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