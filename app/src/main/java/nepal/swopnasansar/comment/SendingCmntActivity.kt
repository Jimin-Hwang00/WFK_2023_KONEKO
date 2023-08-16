package nepal.swopnasansar.comment

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.withContext
import nepal.swopnasansar.comment.dao.AdminDAO
import nepal.swopnasansar.comment.dao.ClassDAO
import nepal.swopnasansar.comment.dao.CommentDAO
import nepal.swopnasansar.comment.dao.StudentDAO
import nepal.swopnasansar.comment.dao.SubjectDAO
import nepal.swopnasansar.comment.dao.TeacherDAO
import nepal.swopnasansar.comment.dto.Administrator
import nepal.swopnasansar.comment.dto.Class
import nepal.swopnasansar.comment.dto.Comment
import nepal.swopnasansar.comment.dto.ReceiverTarget
import nepal.swopnasansar.comment.dto.Subject
import nepal.swopnasansar.comment.dto.Teacher
import nepal.swopnasansar.databinding.ActivitySendingCmntBinding
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class SendingCmntActivity : AppCompatActivity() {
    private val TAG = "SendingCommentActivity"

    private lateinit var binding: ActivitySendingCmntBinding

    val classDao = ClassDAO()
    val studentDao = StudentDAO()
    val teacherDao = TeacherDAO()
    val subjectDao = SubjectDAO()

    private var receiverTargets = ArrayList<ReceiverTarget>()

    private lateinit var sendingCommentReceiversAdapter: SendingCmntTargetsAdapter

    val date = Instant.ofEpochMilli(System.currentTimeMillis())
        .atOffset(ZoneOffset.ofHours(9))
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

    var selectedTargetIndex = -1

    val commentDAO = CommentDAO()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySendingCmntBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecyclerView()

        sendingCommentReceiversAdapter.setOnItemClickListener(object: SendingCmntTargetsAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                if (selectedTargetIndex != -1) {
                    receiverTargets[selectedTargetIndex].selected = false
                }

                receiverTargets[position].selected = true
                selectedTargetIndex = position

                sendingCommentReceiversAdapter?.notifyDataSetChanged()
            }
        })

        binding.sendingCmntBtn.setOnClickListener {
            lifecycleScope.launch {
                val result = withContext(Dispatchers.IO) {
                    val comment = Comment("", binding.evSendingCmntTitle.text.toString(), binding.evSendingCmntContent.text.toString(), date, "test_key", "author_name", receiverTargets[selectedTargetIndex].key, receiverTargets[selectedTargetIndex].name, false)
                    commentDAO.uploadComment(comment)
                }

                withContext(Main) {
                    if (result) {
                        val intent =
                            Intent(this@SendingCmntActivity, SentCmntListActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            this@SendingCmntActivity,
                            "Fail to upload comment. Try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    override fun onResume() {
        val targetRole = intent.getStringExtra("targetRole").toString()

        // @TODO 통합 작업할 때 role 체크!
        retrieveCommentTarget(targetRole)
        super.onResume()
    }

    fun retrieveCommentTarget(targetRole: String) {
        binding.pbSendingCmnt.visibility = View.VISIBLE

        if (targetRole.equals("student")) {
            var targets: ArrayList<ReceiverTarget> = ArrayList()

            lifecycleScope.launch {
                var classes: ArrayList<Class>? = null

                // 비동기로 클래스 데이터 가져오기
                withContext(Dispatchers.IO) {
                    classes = classDao.getAllClasses() as ArrayList<Class>
                }

                // 각 클래스에 속한 학생들의 이름을 비동기로 가져오기
                classes?.forEach { classItem ->
                    // 해당 클래스에 속한 학생들의 이름을 가져오기
                    classItem.student_key.forEach { studentKey ->
                        withContext(Dispatchers.IO) {
                            val stn = studentDao.getStudentByKey(studentKey)
                            if (stn != null) {
                                val target = ReceiverTarget(classItem.class_name, stn.stn_name, stn.stn_key, false)
                                targets.add(target)
                            }
                        }
                    }
                }

                withContext(Dispatchers.Main) {
                    binding.pbSendingCmnt.visibility = View.INVISIBLE
                    updateUI(targets)
                }
            }
        } else if (targetRole.equals("teacher")) {
            var targets: ArrayList<ReceiverTarget> = ArrayList()

            binding.pbSendingCmnt.visibility = View.VISIBLE
            lifecycleScope.launch {
                val teachers: ArrayList<Teacher>? = withContext(Dispatchers.IO) {
                    teacherDao.getAllTeachers()
                }

                if (teachers != null) {
                    for (teacher in teachers) {
                        val subjects: ArrayList<Subject>? = withContext(Dispatchers.IO) {
                            subjectDao.getSubjectByTeacherKey(teacher.teacher_key)
                        }

                        if (subjects != null) {
                            if (subjects.size >= 2) {
                                var subjectName = ""
                                subjects.forEach { subject ->
                                    subjectName.plus(", ${subject.subject_name}")
                                }

                                val target = ReceiverTarget(subjectName, teacher.teacher_name, teacher.teacher_key, false)
                                Log.d(TAG, target.toString())
                                targets.add(target)
                            } else if (subjects.size == 1){
                                val target = ReceiverTarget(subjects[0].subject_name, teacher.teacher_name, teacher.teacher_key, false)
                                Log.d(TAG, target.toString())
                                targets.add(target)
                            } else {
                                val target = ReceiverTarget("", teacher.teacher_name, teacher.teacher_key, false)
                                Log.d(TAG, target.toString())
                                targets.add(target)
                            }
                        } else {
                            Toast.makeText(this@SendingCmntActivity, "Fail to get subject info. Try again.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this@SendingCmntActivity, "Fail to get teacher info. Try again.", Toast.LENGTH_SHORT).show()
                }

                withContext(Dispatchers.Main) {
                    binding.pbSendingCmnt.visibility = View.INVISIBLE
                    updateUI(targets)
                }
            }
        }
    }

    fun initRecyclerView() {
        if (receiverTargets == null) {
            sendingCommentReceiversAdapter = SendingCmntTargetsAdapter(ArrayList(), this)
        } else {
            sendingCommentReceiversAdapter = SendingCmntTargetsAdapter(receiverTargets!!, this)
        }

        sendingCommentReceiversAdapter.notifyDataSetChanged()

        binding.rvSelectCmntTarget.adapter = sendingCommentReceiversAdapter
        binding.rvSelectCmntTarget.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }


    private fun updateUI(targets: ArrayList<ReceiverTarget>?) {
        // Null 체크 및 데이터 업데이트
        receiverTargets = targets ?: ArrayList()

        Log.d(TAG, "updateUI: receiverTargets size ${receiverTargets.size}")
        targets?.forEach {
            Log.d(TAG, "updateUI: ${it}")
        }

        sendingCommentReceiversAdapter?.notifyDataSetChanged()

        if (sendingCommentReceiversAdapter == null) {
            sendingCommentReceiversAdapter = SendingCmntTargetsAdapter(receiverTargets!!, this)
            binding.rvSelectCmntTarget.adapter = sendingCommentReceiversAdapter
            binding.rvSelectCmntTarget.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        } else {
            sendingCommentReceiversAdapter?.updateData(receiverTargets!!)
        }
    }
}