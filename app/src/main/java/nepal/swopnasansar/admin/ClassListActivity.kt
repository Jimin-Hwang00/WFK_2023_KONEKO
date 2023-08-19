package nepal.swopnasansar.admin

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import nepal.swopnasansar.R
import nepal.swopnasansar.data.AccountantDto
import nepal.swopnasansar.data.RvClassListDto
import nepal.swopnasansar.data.SubjectDto
import nepal.swopnasansar.data.TeacherDto
import nepal.swopnasansar.databinding.ActivityAccountantListBinding
import nepal.swopnasansar.databinding.ActivityClassListBinding
import nepal.swopnasansar.dto.Homework
import javax.security.auth.Subject

class ClassListActivity : AppCompatActivity() {
    lateinit var binding : ActivityClassListBinding
    lateinit var adapter : ClassAdapter
    var progressBarVisible = true
    val TAG = "ClassListActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClassListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = Firebase.firestore
        val classList = ArrayList<RvClassListDto>()
        val pref : SharedPreferences = getSharedPreferences("save_state", 0)
        val editor : SharedPreferences.Editor = pref.edit()
        var subjectList = ArrayList<SubjectDto>()
        var homeworkList = ArrayList<Homework>()

        adapter = ClassAdapter(this, classList)
        binding.rvClassList.adapter = adapter

        binding.rvClassList.layoutManager = LinearLayoutManager(this).apply {
            orientation = LinearLayoutManager.VERTICAL
        }

        val onLongClickListener = object: ClassAdapter.OnItemLongClickListener {
            override fun onItemLongClick(view: View, position: Int) {
                AlertDialog.Builder(this@ClassListActivity).run {
                    setTitle("Delete Class")
                    setMessage("Delete it?")
                    setNegativeButton("No", null)
                    setCancelable(false)
                    setPositiveButton("Yes", object: DialogInterface.OnClickListener {
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            CoroutineScope(Dispatchers.IO).launch {
                                // 과목 정보
                                subjectList.clear()
                                val subjectQuerySnapshot = db?.collection("subject")
                                    ?.whereEqualTo("class_key", classList[position].class_key)?.get()?.await()
                                subjectList.addAll(
                                    subjectQuerySnapshot?.toObjects(SubjectDto::class.java)
                                        ?: emptyList()
                                )

                                // 숙제 정보
                                homeworkList.clear()
                                val homeworkQuerySnapshot = db?.collection("homework")
                                    ?.whereEqualTo("class_key", classList[position].class_key)?.get()?.await()
                                homeworkList.addAll(
                                    homeworkQuerySnapshot?.toObjects(Homework::class.java)
                                        ?: emptyList()
                                )

                                for(s in subjectList){
                                    Log.d(TAG, "${s.subject_key}")
                                    db.collection("subject").document(s.subject_key)
                                        .delete()
                                        .addOnSuccessListener {
                                         Log.d(TAG, "delete success!!")
                                        }
                                        .addOnFailureListener { e ->
                                            // 삭제 중에 발생한 오류 처리
                                            Toast.makeText(
                                                this@ClassListActivity,
                                                "delete failed: ${e.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }.await()
                                }

                                for(h in homeworkList){
                                    Log.d(TAG, "${h.homework_key}")
                                    db.collection("homework").document(h.homework_key)
                                        .delete()
                                        .addOnSuccessListener {
                                            Log.d(TAG, "delete success!!")
                                        }
                                        .addOnFailureListener { e ->
                                            // 삭제 중에 발생한 오류 처리
                                            Toast.makeText(
                                                this@ClassListActivity,
                                                "delete failed: ${e.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }.await()
                                }

                                db.collection("class").document(classList[position].class_key)
                                    .delete()
                                    .addOnSuccessListener {
                                        classList.removeAt(position) // ArrayList에서 항목 삭제
                                        adapter.notifyDataSetChanged() // 어댑터에 데이터 변경 알림
                                        Toast.makeText(
                                            this@ClassListActivity,
                                            "delete success!!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    .addOnFailureListener { e ->
                                        // 삭제 중에 발생한 오류 처리
                                        Toast.makeText(
                                            this@ClassListActivity,
                                            "delete failed: ${e.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }.await()
                            }
                        }
                    })
                    show()
                }
            }
        }
        adapter.setOnItemLongClickListener(onLongClickListener)

        binding.classListEdBt.setOnClickListener{
            val intent = Intent(this@ClassListActivity, CreateClassActivity::class.java)

            editor.putString("classEt", "")
            editor.putString("selectedTeacher", "")
            editor.putString("selectedStudentListText", "")
            editor.putString("subjectEt", "")
            editor.commit()

            startActivity(intent)
        }
    }

    // 뷰 홀더가 생성되어 화면에 표시된 후에 ProgressBar를 숨기는 메서드
    fun hideProgressBar() {
        if (progressBarVisible) {
            progressBarVisible = false
            binding.progressBar.visibility = View.INVISIBLE
        }
    }

}