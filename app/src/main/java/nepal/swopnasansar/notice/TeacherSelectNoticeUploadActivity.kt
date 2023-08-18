package nepal.swopnasansar.notice

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import nepal.swopnasansar.R
import nepal.swopnasansar.dao.AuthDAO
import nepal.swopnasansar.data.NoticeDto
import nepal.swopnasansar.databinding.ActivityTeacherSelectNoticeUploadBinding

class TeacherSelectNoticeUploadActivity : AppCompatActivity() {
    lateinit var binding : ActivityTeacherSelectNoticeUploadBinding
    val TAG = "TeacherSelectNoticeUploadActivity"
    private val authDao = AuthDAO()
    val uid = authDao.getUid()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeacherSelectNoticeUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = Firebase.firestore
        val stnKeyList: ArrayList<String> = intent.getStringArrayListExtra("stnKeyList") as ArrayList<String>
        val stnNameList: ArrayList<String> = intent.getStringArrayListExtra("stnNameList") as ArrayList<String>

        binding.selectNoticeUploadBt.setOnClickListener{
            val title = binding.titleEdText.text.toString()
            val content = binding.contentEditText.text.toString()

            AlertDialog.Builder(this@TeacherSelectNoticeUploadActivity).run {
                setTitle("Notice")
                setMessage("Are you sure to notice \"${title}\"?")
                setNegativeButton("No", null)
                setCancelable(false)
                setPositiveButton("Yes", object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        // 문서를 추가하고 자동으로 생성된 키 값을 받아옵니다.
                        db.collection("notice").add(NoticeDto("", "", "", ArrayList(), ArrayList(), "", ""))
                            .addOnSuccessListener { documentReference ->
                                val documentId = documentReference.id
                                // 문서 ID를 저장한 뒤 문서에 데이터를 업데이트합니다.
                                db.collection("notice").document(documentId).set(
                                    NoticeDto(documentId, content, title,
                                    stnKeyList, stnNameList, "", uid.toString()
                                )
                                )
                                    .addOnSuccessListener {
                                        val intent = Intent(this@TeacherSelectNoticeUploadActivity, TeacherSelectNoticeActivity::class.java)
                                        binding.contentEditText.setText("")
                                        binding.titleEdText.setText("")
                                        Toast.makeText(this@TeacherSelectNoticeUploadActivity, "save completed", Toast.LENGTH_LONG).show()
                                        startActivity(intent)
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