package nepal.swopnasansar.notice

import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import nepal.swopnasansar.data.ClassDto
import nepal.swopnasansar.data.RvSelectNoticeDto
import nepal.swopnasansar.data.StudentDto
import nepal.swopnasansar.data.SubjectDto
import nepal.swopnasansar.databinding.ListSelectNoticeBinding

class TeacherSelectNoticeAdapter (val rvSelectNoticeList : ArrayList<RvSelectNoticeDto>, val activity: AppCompatActivity)
    : RecyclerView.Adapter<TeacherSelectNoticeAdapter.TeacherViewHolder>() {
    val TAG = "TeacherSelectNoticeAdapter"
    var firestore : FirebaseFirestore? = null
    var classTempList = ArrayList<ClassDto>() // 빈 ArrayList로 초기화
    var studentTempList = ArrayList<StudentDto>() // 빈 ArrayList로 초기화
    var subjectTempList = ArrayList<SubjectDto>() // 빈 ArrayList로 초기화
    var allClassTempList = ArrayList<ClassDto>()
    var checkBoxList = ArrayList<Boolean>()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            firestore = FirebaseFirestore.getInstance()
            //모든 클래스 담고 학생 리스트 뽑고 그 학생 리스트에 해당하는 클래스들 다시 담고 그다음 과목 담으면 됨.

            //클래스 정보 우선 가져오기
            classTempList.clear()
            val classQuerySnapshot = firestore?.collection("class")?.get()?.await()
            classTempList.addAll(classQuerySnapshot?.toObjects(ClassDto::class.java) ?: emptyList())



            // 학생리스트 정보 가져오기
            studentTempList.clear()
            allClassTempList.clear()
            for (studentInfo in classTempList) {
                for(stn_keyInfo in studentInfo.student_key){
                    val studentQuerySnapshot = firestore?.collection("student")?.whereEqualTo("stn_key", stn_keyInfo)?.get()?.await()
                    studentTempList.addAll(studentQuerySnapshot?.toObjects(StudentDto::class.java) ?: emptyList())
                    allClassTempList.addAll(listOf(studentInfo))
                }
            }

            for(stn in studentTempList){
                Log.d(TAG, "${stn.stn_name}, ${stn.stn_key}")
            }

            for(stn in allClassTempList){
                Log.d(TAG, "${stn.class_name}")
            }

            subjectTempList.clear()
            // 과목 정보 가져오기
            for(subjectInfo in allClassTempList){
                val subjectQuerySnapshot = firestore?.collection("subject")?.whereEqualTo("class_key", subjectInfo.class_key)?.get()?.await()
                subjectTempList.addAll(subjectQuerySnapshot?.toObjects(SubjectDto::class.java) ?: emptyList())
            }

            for(stn in subjectTempList){
                Log.d(TAG, "${stn.subject_name}")
            }

            // 데이터 처리 및 어댑터 갱신
            withContext(Dispatchers.Main) {
                val minSize = minOf(classTempList.size, subjectTempList.size, studentTempList.size)
                rvSelectNoticeList.clear()
                for (i in 0 until minSize) {
                    rvSelectNoticeList.add(RvSelectNoticeDto(classTempList[i].class_name, subjectTempList[i].subject_name, studentTempList[i].stn_name, studentTempList[i].stn_key))
                }
                for(stn in rvSelectNoticeList){
                    Log.d(TAG, "${stn.class_name}, ${stn.subject_name}, ${stn.student_name}")
                }

                checkBoxList.clear()
                checkBoxList.addAll(List(rvSelectNoticeList.size) { false })

                (activity as? TeacherSelectNoticeActivity)?.hideProgressBar()
                notifyDataSetChanged()
            }
        }
    }

    fun onUpdateList(){
        CoroutineScope(Dispatchers.IO).launch {
            firestore = FirebaseFirestore.getInstance()
            //모든 클래스 담고 학생 리스트 뽑고 그 학생 리스트에 해당하는 클래스들 다시 담고 그다음 과목 담으면 됨.

            //클래스 정보 우선 가져오기
            classTempList.clear()
            val classQuerySnapshot = firestore?.collection("class")?.get()?.await()
            classTempList.addAll(classQuerySnapshot?.toObjects(ClassDto::class.java) ?: emptyList())



            // 학생리스트 정보 가져오기
            studentTempList.clear()
            allClassTempList.clear()
            for (studentInfo in classTempList) {
                for(stn_keyInfo in studentInfo.student_key){
                    val studentQuerySnapshot = firestore?.collection("student")?.whereEqualTo("stn_key", stn_keyInfo)?.get()?.await()
                    studentTempList.addAll(studentQuerySnapshot?.toObjects(StudentDto::class.java) ?: emptyList())
                    allClassTempList.addAll(listOf(studentInfo))
                }
            }

            for(stn in studentTempList){
                Log.d(TAG, "${stn.stn_name}, ${stn.stn_key}")
            }

            for(stn in allClassTempList){
                Log.d(TAG, "${stn.class_name}")
            }

            subjectTempList.clear()
            // 과목 정보 가져오기
            for(subjectInfo in allClassTempList){
                val subjectQuerySnapshot = firestore?.collection("subject")?.whereEqualTo("class_key", subjectInfo.class_key)?.get()?.await()
                subjectTempList.addAll(subjectQuerySnapshot?.toObjects(SubjectDto::class.java) ?: emptyList())
            }

            for(stn in subjectTempList){
                Log.d(TAG, "${stn.subject_name}")
            }

            // 데이터 처리 및 어댑터 갱신
            withContext(Dispatchers.Main) {
                val minSize = minOf(classTempList.size, subjectTempList.size, studentTempList.size)
                rvSelectNoticeList.clear()
                for (i in 0 until minSize) {
                    rvSelectNoticeList.add(RvSelectNoticeDto(classTempList[i].class_name, subjectTempList[i].subject_name, studentTempList[i].stn_name, studentTempList[i].stn_key))
                }
                for(stn in rvSelectNoticeList){
                    Log.d(TAG, "${stn.class_name}, ${stn.subject_name}, ${stn.student_name}")
                }

                checkBoxList.clear()
                checkBoxList.addAll(List(rvSelectNoticeList.size) { false })

                (activity as? TeacherSelectNoticeActivity)?.hideProgressBar()
                notifyDataSetChanged()
            }
        }
    }

    class TeacherViewHolder(
        val itemBinding: ListSelectNoticeBinding,
        cbListener: onCheckBoxClickListener?
    ) : RecyclerView.ViewHolder(itemBinding.root) {
        val className = itemBinding.classNameTv
        val subjectName = itemBinding.subjectTv
        val studentName = itemBinding.stnNameTv

        val deleteCheckbox = itemBinding.deleteCheckBox

        init {
            itemBinding.deleteCheckBox.setOnClickListener() {
                cbListener?.onClickCheckBox(0, adapterPosition)
                true
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TeacherSelectNoticeAdapter.TeacherViewHolder {
        val itemBinding =
            ListSelectNoticeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TeacherSelectNoticeAdapter.TeacherViewHolder(itemBinding, cbListener)
    }

    override fun getItemCount(): Int {
        return rvSelectNoticeList.size
    }

    override fun onBindViewHolder(holder: TeacherSelectNoticeAdapter.TeacherViewHolder, position: Int)  {
        holder.className.text = rvSelectNoticeList[position].class_name
        holder.subjectName.text = rvSelectNoticeList[position].subject_name
        holder.studentName.text = rvSelectNoticeList[position].student_name

        // 데이터를 가져올 때, 체크박스의 상태를 초기화 (체크 안되도록 설정)
        holder.deleteCheckbox.isChecked = checkBoxList[position]

        holder.deleteCheckbox.setOnClickListener { v ->
            if (holder.deleteCheckbox.isChecked()) {
                // 체크가 되어 있음
                Log.d(TAG, "체크 됨 1")
                cbListener?.onClickCheckBox(1, position)
                checkBoxList[position] = true
            } else {
                // 체크가 되어있지 않음
                cbListener?.onClickCheckBox(0, position)
                checkBoxList[position] = false
            }
        }
    }

    //체크박스 선택시
    interface onCheckBoxClickListener {
        fun onClickCheckBox(flag : Int, position: Int)
    }

    var cbListener: onCheckBoxClickListener? = null

    fun setOnCheckBoxClickListener(listener: onCheckBoxClickListener) {
        this.cbListener = listener
    }
}