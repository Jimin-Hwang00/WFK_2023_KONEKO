package nepal.swopnasansar.notice

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import nepal.swopnasansar.dao.AuthDAO
import nepal.swopnasansar.data.RvCheckNoticeDto
import nepal.swopnasansar.databinding.ActivityTeacherCheckNoticeBinding
import nepal.swopnasansar.login.CheckRoleActivity

class TeacherCheckNoticeActivity : AppCompatActivity() {
    lateinit var binding : ActivityTeacherCheckNoticeBinding
    lateinit var adapter : TeacherCheckNoticeAdapter
    var progressBarVisible = true
    val TAG = "TeacherCheckNoticeActivity"
    private val authDao = AuthDAO()
    val uid = authDao.getUid()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeacherCheckNoticeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = Firebase.firestore
        val checkNoticeList = ArrayList<RvCheckNoticeDto>()

        if (uid == null) {
            Toast.makeText(applicationContext, "You have to login.", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, CheckRoleActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

        adapter = TeacherCheckNoticeAdapter(checkNoticeList, this)
        binding.rvDeleteList.adapter = adapter

        binding.rvDeleteList.layoutManager = LinearLayoutManager(this).apply {
            orientation = LinearLayoutManager.VERTICAL
        }

        val OnItemClickListener = object : TeacherCheckNoticeAdapter.OnItemClickListener {
            override fun onClick(view: View, position: Int) {
                val content = checkNoticeList[position].content
                var receiverList : String = ""

                for(receiver in checkNoticeList[position].studentNameList){
                    receiverList += "${receiver}  "
                }

                binding.contentEditText.setText(content)
                binding.receiverListTv.setText("receiver List\n ${receiverList}")

                // selectedPosition을 adapter에 설정하여 변경
                adapter.selectedPosition = position
                adapter.notifyDataSetChanged()

                binding.noticeDeleteBt.setOnClickListener{
                    AlertDialog.Builder(this@TeacherCheckNoticeActivity).run {
                        setTitle("Notice")
                        setMessage("Are you sure to delete \"${checkNoticeList[position].title}\"?")
                        setNegativeButton("No", null)
                        setCancelable(false)
                        setPositiveButton("Yes", object : DialogInterface.OnClickListener {
                            override fun onClick(p0: DialogInterface?, p1: Int) {
                                db.collection("notice").document(checkNoticeList[position].notice_key).delete()
                                    .addOnSuccessListener {
                                        checkNoticeList.removeAt(position)
                                        binding.contentEditText.setText("")
                                        binding.receiverListTv.setText("receiver List\n")
                                        adapter.notifyDataSetChanged() // 어댑터에 데이터 변경 알림
                                        Toast.makeText(this@TeacherCheckNoticeActivity, "delete success!!", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener { e ->
                                        // 삭제 중에 발생한 오류 처리
                                        Toast.makeText(this@TeacherCheckNoticeActivity, "delete failed: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        })
                        show()
                    }
                }
            }
        }

        adapter.setOnClickListener(OnItemClickListener)
    }
    // 뷰 홀더가 생성되어 화면에 표시된 후에 ProgressBar를 숨기는 메서드
    fun hideProgressBar() {
        if (progressBarVisible) {
            progressBarVisible = false
            binding.progressBar.visibility = View.INVISIBLE
        }
    }

}