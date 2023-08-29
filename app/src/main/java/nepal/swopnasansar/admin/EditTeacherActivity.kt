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
import kotlinx.coroutines.withContext
import nepal.swopnasansar.R
import nepal.swopnasansar.data.AccountantDto
import nepal.swopnasansar.data.StudentDto
import nepal.swopnasansar.data.TeacherDto
import nepal.swopnasansar.data.TempDto
import nepal.swopnasansar.databinding.ActivityEditStudentBinding
import nepal.swopnasansar.databinding.ActivityEditTeacherBinding
import nepal.swopnasansar.databinding.ListTeacherAndAccountBinding

class EditTeacherActivity : AppCompatActivity() {
    lateinit var binding: ActivityEditTeacherBinding
    lateinit var adapter: TeacherAdapter
    lateinit var itemBinding: ListTeacherAndAccountBinding
    val TAG = "EditTeacherActivity"
    var progressBarVisible = true
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    var firestore : FirebaseFirestore? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditTeacherBinding.inflate(layoutInflater)
        itemBinding = ListTeacherAndAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = Firebase.firestore
        val teacherList = ArrayList<TempDto>()
        var checkedList = ArrayList<TempDto>()
        var checkedPos = ArrayList<Int>()

        adapter = TeacherAdapter(this, teacherList)
        binding.rvTeacherList.adapter = adapter

        binding.rvTeacherList.layoutManager = LinearLayoutManager(this).apply {
            orientation = LinearLayoutManager.VERTICAL
        }


        binding.addTeacherBt.setOnClickListener {
            firestore = FirebaseFirestore.getInstance()
            var teacher : String = ""

            for(t in checkedList){
                teacher += "${t.name}"
            }

            if(checkedList.size == 1) {
                AlertDialog.Builder(this@EditTeacherActivity).run {
                    val intent = Intent(this@EditTeacherActivity, EditTeacherActivity::class.java)
                    setTitle("Accept Teacher(s)")
                    setMessage("Are you sure to accept Teacher(s) \"${teacher}\"?")
                    setNegativeButton("No", null)
                    setCancelable(false)
                    setPositiveButton("Yes", object : DialogInterface.OnClickListener {
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            CoroutineScope(Dispatchers.IO).launch {
                                for(t in checkedList){
                                        //auth 등록
                                        Log.d(TAG, "auth 등록완료")
                                        val authResult = auth.createUserWithEmailAndPassword(t.email, t.email.substringBefore("@")).await()
                                        val user: FirebaseUser? = authResult.user
                                        var uid = user?.uid ?: ""
                                        println("User UID: $uid")

                                        if(!uid.equals("")){
                                            // 문서 ID를 저장한 뒤 문서에 데이터를 업데이트합니다.
                                            db.collection("teacher").document(uid)
                                                .set(TeacherDto(uid, t.name, t.email))
                                                .addOnSuccessListener {
                                                   Log.d(TAG, "accept Teacher${t.email}")
                                                }
                                                .addOnFailureListener { exception ->
                                                    println("Error creating document: $exception")
                                                }
                                        }


                                    //temp에서 삭제
                                    db.collection("temp").document(t.email).delete()
                                        .addOnSuccessListener {
                                            adapter.notifyDataSetChanged() // 어댑터에 데이터 변경 알림
                                            Log.d(TAG, "accept teacher(s)")
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
                                withContext(Dispatchers.Main) {
                                    adapter.onUpdateList()
                                    adapter.notifyDataSetChanged()
                                }
                            }
                            Toast.makeText(
                                this@EditTeacherActivity,
                                "Accept completed",
                                Toast.LENGTH_SHORT
                            )
                                .show()

                            checkedList.clear()
                            checkedPos.clear()
                        }
                    })
                    show()
                }
            } else{
                Toast.makeText(this@EditTeacherActivity, "Please check one person at a time", Toast.LENGTH_LONG).show()
            }
        }

        val onCheckBoxClickListener = object : TeacherAdapter.onCheckBoxClickListener {
            override fun onClickCheckBox(flag: Int, position: Int) {
                Log.d(TAG, "${flag}값과 현재 position : ${position}")
                if(flag == 1){
                    checkedList.add(teacherList[position])
                    checkedPos.add(position)
                    for (item in checkedList) {
                        Log.d(TAG, "checkedList item: ${item.email}")
                    }
                }else{
                    // flag 값이 0인 경우, 해당 객체를 checkedList에서 제거
                    val targetObject = teacherList[position]
                    checkedList.remove(targetObject)
                    checkedPos.remove(position)
                    for (item in checkedList) {
                        Log.d(TAG, "checkedList item: ${item.email}")
                    }
                }
            }
        }
        adapter.setOnCheckBoxClickListener(onCheckBoxClickListener)

        binding.deleteTeacherBt.setOnClickListener{
            AlertDialog.Builder(this@EditTeacherActivity).run {
                setTitle("delete Teacher(s)")
                setMessage("Are you sure to delete?")
                setNegativeButton("No", null)
                setCancelable(false)
                setPositiveButton("Yes", object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        for(t in checkedList){
                            db.collection("temp").document(t.email).delete()
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