package nepal.swopnasansar.notice

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
import nepal.swopnasansar.dao.AuthDAO
import nepal.swopnasansar.data.NoticeDto
import nepal.swopnasansar.data.RvEntireNoticeDto
import nepal.swopnasansar.data.StudentDto
import nepal.swopnasansar.databinding.ActivityTeacherEntireNoticeBinding
import nepal.swopnasansar.login.CheckRoleActivity

class TeacherEntireNoticeActivity : AppCompatActivity() {
    lateinit var binding : ActivityTeacherEntireNoticeBinding
    lateinit var adapter : TeacherEntireNoticeAdapter
    var progressBarVisible = true
    val TAG = "TeacherEntireNoticeActivity"
    private val authDao = AuthDAO()
    val uid = authDao.getUid()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeacherEntireNoticeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = Firebase.firestore
        val entireNoticeList = ArrayList<RvEntireNoticeDto>()
        var checkedPosition: Int = -1
        var checkedClass = RvEntireNoticeDto("", "", "", ArrayList(), ArrayList())
        var studentNameList : ArrayList<String> = ArrayList()

        if (uid == null) {
            Toast.makeText(applicationContext, "You have to login.", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, CheckRoleActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

        adapter = TeacherEntireNoticeAdapter(entireNoticeList, this)
        binding.rvEntireNotice.adapter = adapter

        binding.rvEntireNotice.layoutManager = LinearLayoutManager(this).apply {
            orientation = LinearLayoutManager.VERTICAL
        }

        val onCheckBoxClickListener = object : TeacherEntireNoticeAdapter.onCheckBoxClickListener {
            override fun onClickCheckBox(flag: Int, position: Int) {
                Log.d(TAG, "${flag}값과 현재 position : ${position}")

                if(checkedPosition == -1){
                    checkedClass = entireNoticeList[position]
                }else{
                    adapter.notifyDataSetChanged()
                    checkedPosition = position
                    checkedClass = entireNoticeList[position]
                }
            }
        }
        adapter.setOnCheckBoxClickListener(onCheckBoxClickListener)

        binding.entireNoticeUploadBt.setOnClickListener{
            val title = binding.titleEdText.text.toString()
            val content = binding.contentEditText.text.toString()

            AlertDialog.Builder(this@TeacherEntireNoticeActivity).run {
                setTitle("Notice")
                setMessage("Are you sure to notice \"${title}\"?")
                setNegativeButton("No", null)
                setCancelable(false)
                setPositiveButton("Yes", object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        for(stn_key in checkedClass.studentKeyList) {
                            db?.collection("student")?.whereEqualTo("stn_key", stn_key)?.get()
                                ?.addOnSuccessListener { result ->
                                    for (snapshot in result) {
                                        studentNameList.add(snapshot.toObject(StudentDto::class.java).stn_name)
                                    }
                                }
                        }

                        // 문서를 추가하고 자동으로 생성된 키 값을 받아옵니다.
                        db.collection("notice").add(
                            NoticeDto("", "", "", ArrayList()
                            , ArrayList(), "", "")
                        )
                            .addOnSuccessListener { documentReference ->
                                val documentId = documentReference.id
                                // 문서 ID를 저장한 뒤 문서에 데이터를 업데이트합니다.
                                db.collection("notice").document(documentId).set(
                                    NoticeDto(documentId, content, title, checkedClass.studentKeyList, studentNameList, checkedClass.subject_key, uid.toString()
                                    )
                                )
                                    .addOnSuccessListener {
                                        binding.contentEditText.setText("")
                                        binding.titleEdText.setText("")
                                        adapter.notifyDataSetChanged()
                                        adapter.onUpdateList()
                                        Toast.makeText(this@TeacherEntireNoticeActivity, "save completed", Toast.LENGTH_LONG).show()
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
    // 뷰 홀더가 생성되어 화면에 표시된 후에 ProgressBar를 숨기는 메서드
    fun hideProgressBar() {
        if (progressBarVisible) {
            progressBarVisible = false
            binding.progressBar.visibility = View.INVISIBLE
        }
    }

}