package nepal.swopnasansar.comment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nepal.swopnasansar.R
import nepal.swopnasansar.dao.*
import nepal.swopnasansar.dto.Class
import nepal.swopnasansar.dto.Comment
import nepal.swopnasansar.dto.CmntTargetItem
import nepal.swopnasansar.dto.Subject
import nepal.swopnasansar.dto.Teacher
import nepal.swopnasansar.databinding.ActivitySendingCmntBinding
import nepal.swopnasansar.login.CheckRoleActivity
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class SendingCmntActivity : AppCompatActivity() {
    private val TAG = "SendingCmntActivity"

    private lateinit var binding: ActivitySendingCmntBinding

    private val authDao = AuthDAO()
    private val classDao = ClassDAO()
    private val studentDao = StudentDAO()
    private val teacherDao = TeacherDAO()
    private val subjectDao = SubjectDAO()

    val uid = authDao.getUid()

    private var receiverTargets = ArrayList<CmntTargetItem>()

    private lateinit var sendingCommentReceiversAdapter: SendingCmntTargetsAdapter

    val date = Instant.ofEpochMilli(System.currentTimeMillis())
        .atOffset(ZoneOffset.ofHours(9))
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

    var selectedTargetIndex = -1

    val commentDAO = CommentDAO()

    lateinit var targetRole: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySendingCmntBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecyclerView()

        if (uid == null) {
            Toast.makeText(applicationContext, "You have to login.", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, CheckRoleActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

        targetRole = intent.getStringExtra("targetRole").toString()

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
            if (selectedTargetIndex == -1) {
                Toast.makeText(this@SendingCmntActivity, "Please select the target for leaving a comment first.", Toast.LENGTH_SHORT).show()
            } else {
                binding.pbSendingCmnt.visibility = View.VISIBLE
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                lifecycleScope.launch {
                    lateinit var userName: String
                    Log.d(TAG, "targetRole: ${targetRole}")
                    if (targetRole.equals(getString(R.string.teacher))) {
                        val student = withContext(Dispatchers.IO) {
                            studentDao.getStudentByKey(uid!!)
                        }
                        if (student != null) {
                            userName = student.stn_name
                            uploadingComment(userName)
                        } else {
                            Toast.makeText(this@SendingCmntActivity, "Fail to upload comment. Try again.", Toast.LENGTH_SHORT).show()
                            binding.pbSendingCmnt.visibility = View.INVISIBLE
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        }
                    } else {
                        val teacher = withContext(Dispatchers.IO) {
                            teacherDao.getTeacherByKey(uid!!)
                        }
                        if (teacher != null) {
                            userName = teacher.teacher_name
                            uploadingComment(userName)
                        } else {
                            Toast.makeText(this@SendingCmntActivity, "Fail to upload comment. Try again.", Toast.LENGTH_SHORT).show()
                            binding.pbSendingCmnt.visibility = View.INVISIBLE
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        retrieveCommentTarget(targetRole)
        super.onResume()
    }

    fun retrieveCommentTarget(targetRole: String) {
        binding.pbSendingCmnt.visibility = View.VISIBLE

        if (targetRole.equals(getString(R.string.student))) {
            var targets: ArrayList<CmntTargetItem> = ArrayList()

            lifecycleScope.launch {
                val subjects = withContext(Dispatchers.IO) {
                    subjectDao.getSubjectByTeacherKey(uid!!)
                }

                subjects?.forEach { subject ->
                    val classItem = withContext(Dispatchers.IO) {
                        classDao.getClassByClassKey(subject.class_key)
                    }

                    classItem?.student_key?.forEach { stnKey ->
                        val student = withContext(Dispatchers.IO) {
                            studentDao.getStudentByKey(stnKey)
                        }

                        if (student != null) {
                            val target = CmntTargetItem(
                                subject.subject_name,
                                student.stn_name,
                                student.stn_key,
                                false
                            )
                            Log.d(TAG, "target : ${target}")
                            targets.add(target)
                        } else {
                            withContext(Main) {
                                Toast.makeText(
                                    this@SendingCmntActivity,
                                    "Fail to get student info. Try again.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }

                    withContext(Dispatchers.Main) {
                        binding.pbSendingCmnt.visibility = View.INVISIBLE
                        updateUI(targets)
                    }
                }
            }
        } else if (targetRole.equals(getString(R.string.teacher))) {
            var targets: ArrayList<CmntTargetItem> = ArrayList()

            binding.pbSendingCmnt.visibility = View.VISIBLE
            lifecycleScope.launch {
                val teachers: ArrayList<Teacher>? = withContext(Dispatchers.IO) {
                    teacherDao.getAllTeachers()
                }

                if (teachers != null) {
                    for (teacher in teachers) {
                        Log.d(TAG, "get target - teacher name : ${teacher.teacher_name}")
                        val subjects: ArrayList<Subject>? = withContext(Dispatchers.IO) {
                            subjectDao.getSubjectByTeacherKey(teacher.teacher_key)
                        }

                        if (subjects != null) {
                            if (subjects.size >= 2) {
                                var subjectName = ""
                                subjects.forEach { subject ->
                                    subjectName = subjectName.plus(", ${subject.subject_name}")
                                }
                                subjectName = subjectName.substring(2)

                                val target = CmntTargetItem(subjectName, teacher.teacher_name, teacher.teacher_key, false)
                                targets.add(target)
                            } else if (subjects.size == 1){
                                val target = CmntTargetItem(subjects[0].subject_name, teacher.teacher_name, teacher.teacher_key, false)
                                targets.add(target)
                            } else {
                                val target = CmntTargetItem("", teacher.teacher_name, teacher.teacher_key, false)
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


    private fun updateUI(targets: ArrayList<CmntTargetItem>?) {
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

    private fun uploadingComment(userName: String) {
        lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) {
                // @TODO
                val comment = Comment("", binding.evSendingCmntTitle.text.toString(), binding.evSendingCmntContent.text.toString(), date, uid!!, userName, receiverTargets[selectedTargetIndex].key, receiverTargets[selectedTargetIndex].name, false)
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

                    binding.pbSendingCmnt.visibility = View.INVISIBLE
                }
            }
        }
    }
}