package nepal.swopnasansar.admin

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import nepal.swopnasansar.data.AdminCalDto
import nepal.swopnasansar.data.ClassDto
import nepal.swopnasansar.data.StudentDto
import nepal.swopnasansar.data.SubjectDto
import nepal.swopnasansar.data.TeacherDto
import nepal.swopnasansar.databinding.ActivityCreateClassBinding
import java.io.Serializable
import javax.security.auth.Subject

class CreateClassActivity : AppCompatActivity() {
    lateinit var binding : ActivityCreateClassBinding
    val TAG = "CreateClassActivity"
    val REQ_TEACHER = 1
    val REQ_STUDENT = 2
    var studentKeyList : ArrayList<String> = ArrayList()
    var teacherInfo = TeacherDto("", "", "")
    var classKey : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateClassBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = Firebase.firestore
        val pref : SharedPreferences = getSharedPreferences("save_state", 0)
        val editor : SharedPreferences.Editor = pref.edit()

        binding.classEt.setText(pref.getString("classEt", null))
        binding.selectedTeacher.setText(pref.getString("selectedTeacher", null))
        binding.selectedStudentListText.setText(pref.getString("selectedStudentListText", null))
        binding.subjectEt.setText(pref.getString("subjectEt", null))

        binding.studentSelectBt.setOnClickListener{
            val intent = Intent(this@CreateClassActivity, SelectStudentActivity::class.java)

            editor.putString("classEt", binding.classEt.text.toString())
            editor.putString("selectedTeacher", binding.selectedTeacher.text.toString())
            editor.putString("selectedStudentListText", binding.selectedStudentListText.text.toString())
            editor.putString("subjectEt", binding.subjectEt.text.toString())
            editor.commit()

            startActivityForResult(intent, REQ_STUDENT)
        }
        binding.teacherSelectBt.setOnClickListener{
            val intent = Intent(this@CreateClassActivity, SelectTeacherActivity::class.java)

            editor.putString("classEt", binding.classEt.text.toString())
            editor.putString("selectedTeacher", binding.selectedTeacher.text.toString())
            editor.putString("selectedStudentListText", binding.selectedStudentListText.text.toString())
            editor.putString("subjectEt", binding.subjectEt.text.toString())
            editor.commit()

            startActivityForResult(intent, REQ_TEACHER)
        }
        binding.addClassBt.setOnClickListener{
            val className = binding.classEt.text.toString()
            val subjectName = binding.subjectEt.text.toString()

            if(className.equals("") or binding.selectedTeacher.text.toString().equals("")
                or binding.selectedStudentListText.text.toString().equals("")){
                Toast.makeText(this@CreateClassActivity, "Please fill in all the fields above", Toast.LENGTH_LONG).show()
            }else{
                AlertDialog.Builder(this@CreateClassActivity).run {
                    setTitle("Add Class")
                    setMessage("Are you sure to add \"${className}\"?")
                    setNegativeButton("No", null)
                    setCancelable(false)
                    setPositiveButton("Yes", object : DialogInterface.OnClickListener {
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            // 문서를 추가하고 자동으로 생성된 키 값을 받아옵니다.
                            db.collection("class").add(ClassDto("", "", ArrayList(), ""))
                                .addOnSuccessListener { documentReference ->
                                    val documentId = documentReference.id
                                    classKey = documentId
                                    // 문서 ID를 저장한 뒤 문서에 데이터를 업데이트합니다.
                                    db.collection("class").document(documentId).set(
                                        ClassDto(documentId, className,
                                        studentKeyList, teacherInfo.teacher_key)
                                    )
                                        .addOnSuccessListener {
                                            Log.d(TAG, "save completed")
                                        }
                                        .addOnFailureListener { exception ->
                                            println("Error creating document: $exception")
                                        }
                                }
                                .addOnFailureListener { exception ->
                                    println("Error creating document: $exception")
                                }

                            // 문서를 추가하고 자동으로 생성된 키 값을 받아옵니다.
                            db.collection("subject").add(SubjectDto("", "", "", "", ArrayList()))
                                .addOnSuccessListener { documentReference ->
                                    val documentId = documentReference.id
                                    // 문서 ID를 저장한 뒤 문서에 데이터를 업데이트합니다.
                                    db.collection("subject").document(documentId).set(
                                        SubjectDto(documentId,
                                        subjectName, teacherInfo.teacher_key, classKey, ArrayList())
                                    )
                                        .addOnSuccessListener {
                                            Toast.makeText(this@CreateClassActivity, "Save completed", Toast.LENGTH_SHORT).show()

                                            binding.classEt.setText("")
                                            binding.selectedTeacher.setText("")
                                            binding.selectedStudentListText.setText("")
                                            binding.subjectEt.setText("")

                                            editor.putString("classEt", binding.classEt.text.toString())
                                            editor.putString("selectedTeacher", binding.selectedTeacher.text.toString())
                                            editor.putString("selectedStudentListText", binding.selectedStudentListText.text.toString())
                                            editor.putString("subjectEt", binding.subjectEt.text.toString())
                                            editor.commit()
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

        binding.deleteClassBt.setOnClickListener{
            val intent = Intent(this, ClassListActivity::class.java)
            Toast.makeText(this@CreateClassActivity, "To delete the class, please long-click", Toast.LENGTH_LONG).show()
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQ_TEACHER -> {
                if (resultCode == RESULT_OK) {
                    teacherInfo = data?.getSerializableExtra("selected_teacher") as TeacherDto
                    binding.selectedTeacher.setText("Selected Teacher : ${teacherInfo.teacher_name}")
                } else {
                    Toast.makeText(this@CreateClassActivity, "try again.", Toast.LENGTH_SHORT).show()
                }
            }
            REQ_STUDENT -> {
                if (resultCode == RESULT_OK) {
                    studentKeyList.clear()
                    binding.selectedStudentListText.setText("-Selected Student List- \n ${data?.getStringExtra("selected_studentList")}")
                    studentKeyList.addAll(data?.getStringArrayListExtra("selected_student_keys") as Collection<String>)
                    Log.d(TAG, studentKeyList.toString())
                } else {
                    Toast.makeText(this@CreateClassActivity, "try again.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}