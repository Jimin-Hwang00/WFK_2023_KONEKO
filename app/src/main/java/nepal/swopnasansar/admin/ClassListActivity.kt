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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import nepal.swopnasansar.R
import nepal.swopnasansar.admin.data.AccountantDto
import nepal.swopnasansar.admin.data.RvClassListDto
import nepal.swopnasansar.databinding.ActivityAccountantListBinding
import nepal.swopnasansar.databinding.ActivityClassListBinding

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
                    setNegativeButton("취소", null)
                    setCancelable(false)
                    setPositiveButton("확인", object: DialogInterface.OnClickListener {
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            db.collection("class").document(classList[position].class_key).delete()
                                .addOnSuccessListener {
                                    Log.d(TAG, "delete success")
                                }
                                .addOnFailureListener { e ->
                                    // 삭제 중에 발생한 오류 처리
                                    Toast.makeText(this@ClassListActivity, "delete failed: ${e.message}", Toast.LENGTH_SHORT).show()
                                }

                            db.collection("subject").document(classList[position].subject_key).delete()
                                .addOnSuccessListener {
                                    classList.removeAt(position) // ArrayList에서 항목 삭제
                                    adapter.notifyDataSetChanged() // 어댑터에 데이터 변경 알림
                                    Toast.makeText(this@ClassListActivity, "delete success!!", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener { e ->
                                    // 삭제 중에 발생한 오류 처리
                                    Toast.makeText(this@ClassListActivity, "delete failed: ${e.message}", Toast.LENGTH_SHORT).show()
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