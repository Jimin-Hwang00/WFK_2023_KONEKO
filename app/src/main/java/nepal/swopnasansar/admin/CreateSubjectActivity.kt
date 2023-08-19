package nepal.swopnasansar.admin

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nepal.swopnasansar.dao.SubjectDAO
import nepal.swopnasansar.data.ClassDto
import nepal.swopnasansar.databinding.ActivityCreateSubjectBinding
import nepal.swopnasansar.dto.Subject

class CreateSubjectActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateSubjectBinding

    private val subjectDao = SubjectDAO()

    private val REQ_CLASS = 10000
    private val REQ_TEACHER = 20000

    private var selectedClassKey: String? = null
    private var selectedTeacherKey: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateSubjectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSelectClassForSubject.setOnClickListener {
            val intent = Intent(this@CreateSubjectActivity, SelectClassActivity::class.java)
            startActivityForResult(intent, REQ_CLASS)
        }

        binding.btnSelectTeacherForSubject.setOnClickListener {
            val intent = Intent(this@CreateSubjectActivity, SelectTeacherForSubjectActivity::class.java)
            startActivityForResult(intent, REQ_TEACHER)
        }

        binding.btnAddSubject.setOnClickListener {
            val subjectName = binding.evAdminCreateSubject.text.toString()
            if (subjectName.isNullOrBlank()) {
                Toast.makeText(this@CreateSubjectActivity, "Please write down subject name first.", Toast.LENGTH_SHORT).show()
            } else if (selectedClassKey == null) {
                Toast.makeText(this@CreateSubjectActivity, "Please select class first.", Toast.LENGTH_SHORT).show()
            } else if (selectedTeacherKey == null) {
                Toast.makeText(this@CreateSubjectActivity, "Please select teacher first.", Toast.LENGTH_SHORT).show()
            } else {
                askForAddingSubject()
            }
        }

        binding.btnDeleteSubject.setOnClickListener{
            val intent = Intent(this, SubjectListActivity::class.java)
            Toast.makeText(this@CreateSubjectActivity, "To delete the class, please long-click", Toast.LENGTH_LONG).show()
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("CreateSubjectActivity", "onActivityResult")
        when (requestCode) {
            REQ_CLASS -> {
                if (resultCode == RESULT_OK && data != null) {
                    selectedClassKey = data.getStringExtra("class_key")
                    val className = data.getStringExtra("class_name")

                    binding.tvSelectClassForSubject.text = "selected class : ${className}"
                } else {
                    Toast.makeText(this@CreateSubjectActivity, "Fail to get selected class.", Toast.LENGTH_SHORT).show()
                }
            }
            REQ_TEACHER -> {
                if (resultCode == RESULT_OK && data != null) {
                    selectedTeacherKey = data.getStringExtra("teacher_key")
                    val teacherName = data.getStringExtra("teacher_name")

                    binding.tvSelectedTeacherForSubject.text = "selected teacher: ${teacherName}"
                }
            }
        }
    }

    private fun addComment() {
        lifecycleScope.launch {
            val subjectName = binding.evAdminCreateSubject.text.toString()

            binding.pbCreateSubject.visibility = View.VISIBLE

            val key = withContext(Dispatchers.IO) {
                subjectDao.createSubject(
                    Subject(
                        "", subjectName,
                        selectedTeacherKey!!,
                        selectedClassKey!!,
                        ArrayList()
                    )
                )
            }

            if (key != null) {
                val updateResult = withContext(Dispatchers.IO) {
                    subjectDao.updateSubject(key, mapOf("subject_key" to key))
                }

                if (updateResult) {
                    Toast.makeText(this@CreateSubjectActivity, "Success to add subject.", Toast.LENGTH_SHORT).show()

                    binding.evAdminCreateSubject.setText("")
                    binding.tvSelectedTeacherForSubject.text = ""
                    binding.tvSelectClassForSubject.text = ""

                    selectedClassKey = null
                    selectedTeacherKey = null

                    binding.pbCreateSubject.visibility = View.INVISIBLE
                } else {
                    Toast.makeText(this@CreateSubjectActivity, "Fail to add subject. Try again.", Toast.LENGTH_SHORT).show()
                    binding.pbCreateSubject.visibility = View.INVISIBLE
                }
            } else {
                Toast.makeText(this@CreateSubjectActivity, "Fail to add subject. Try again.", Toast.LENGTH_SHORT).show()
                binding.pbCreateSubject.visibility = View.INVISIBLE
            }
        }
    }

    private fun askForAddingSubject() {
        AlertDialog.Builder(this@CreateSubjectActivity).run {
            setTitle("Add Subject")
            setMessage("Are you sure to add \"${binding.evAdminCreateSubject.text}\"?")
            setNegativeButton("No", null)
            setCancelable(false)
            setPositiveButton("Yes", object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                   addComment()
                }
            })
            show()
        }

    }
}