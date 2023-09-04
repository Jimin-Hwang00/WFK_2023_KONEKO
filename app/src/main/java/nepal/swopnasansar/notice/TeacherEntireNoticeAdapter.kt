package nepal.swopnasansar.notice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import nepal.swopnasansar.R
import nepal.swopnasansar.data.ClassDto
import nepal.swopnasansar.data.RvEntireNoticeDto
import nepal.swopnasansar.data.SubjectDto
import nepal.swopnasansar.databinding.ListEntireNoticeBinding

class TeacherEntireNoticeAdapter (val rvEntireNoticeList : ArrayList<RvEntireNoticeDto>, val activity: AppCompatActivity)
    : RecyclerView.Adapter<TeacherEntireNoticeAdapter.TeacherViewHolder>() {
    val TAG = "TeacherAdapter"
    var firestore : FirebaseFirestore? = null
    var classTempList = ArrayList<ClassDto>() // 빈 ArrayList로 초기화
    var subjectTempList = ArrayList<SubjectDto>() // 빈 ArrayList로 초기화
    var checkedPosition : Int = -1
    private val holderList = mutableListOf<TeacherViewHolder>()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            firestore = FirebaseFirestore.getInstance()
            //모든 클래스 담고 학생 리스트 뽑고 그 학생 리스트에 해당하는 클래스들 다시 담고 그다음 과목 담으면 됨.

            // 과목 정보 우선 가져오기
            subjectTempList.clear()
            val subjectQuerySnapshot = firestore?.collection("subject")?.get()?.await()
            subjectTempList.addAll(subjectQuerySnapshot?.toObjects(SubjectDto::class.java) ?: emptyList())

            //클래스 정보 가져오기
            classTempList.clear()
            for(classInfo in subjectTempList) {
                val classQuerySnapshot = firestore?.collection("class")?.whereEqualTo("class_key", classInfo.class_key)?.get()?.await()
                classTempList.addAll(
                    classQuerySnapshot?.toObjects(ClassDto::class.java) ?: emptyList()
                )
            }

            for(stn in classTempList){
                Log.d(TAG, "교실 : ${stn.class_name}")
            }

            // 데이터 처리 및 어댑터 갱신
            withContext(Dispatchers.Main) {
                val minSize = minOf(classTempList.size, subjectTempList.size)
                rvEntireNoticeList.clear()
                for (i in 0 until minSize) {
                    rvEntireNoticeList.add(
                        RvEntireNoticeDto(classTempList[i].class_name, subjectTempList[i].subject_name,
                        subjectTempList[i].subject_key, classTempList[i].student_key, ArrayList())
                    )
                }

                for(stn in rvEntireNoticeList){
                    Log.d(TAG, "rv : ${stn.class_name}, ${stn.subject_key}")
                }
                (activity as? TeacherEntireNoticeActivity)?.hideProgressBar()
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

            for(stn in classTempList){
                Log.d(TAG, "교실 : ${stn.class_name}")
            }

            // 과목 정보 가져오기
            subjectTempList.clear()
            for(subjectInfo in classTempList){
                val subjectQuerySnapshot = firestore?.collection("subject")?.whereEqualTo("class_key", subjectInfo.class_key)?.get()?.await()
                subjectTempList.addAll(subjectQuerySnapshot?.toObjects(SubjectDto::class.java) ?: emptyList())
            }

            for(stn in subjectTempList){
                Log.d(TAG, "과목 : ${stn.subject_name}")
            }

            // 데이터 처리 및 어댑터 갱신
            withContext(Dispatchers.Main) {
                val minSize = minOf(classTempList.size, subjectTempList.size)
                rvEntireNoticeList.clear()
                for (i in 0 until minSize) {
                    rvEntireNoticeList.add(
                        RvEntireNoticeDto(classTempList[i].class_name, subjectTempList[i].subject_name,
                        subjectTempList[i].subject_key, classTempList[i].student_key, ArrayList())
                    )
                }

                for(stn in rvEntireNoticeList){
                    Log.d(TAG, "rv : ${stn.class_name}, ${stn.subject_key}")
                }
                (activity as? TeacherEntireNoticeActivity)?.hideProgressBar()
                notifyDataSetChanged()
            }
        }
    }

    class TeacherViewHolder(
        val itemBinding: ListEntireNoticeBinding,
        cbListener: TeacherEntireNoticeAdapter.onCheckBoxClickListener?
    ) : RecyclerView.ViewHolder(itemBinding.root) {
        val className = itemBinding.classNameTv
        val subjectName = itemBinding.subjectTv
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
    ): TeacherEntireNoticeAdapter.TeacherViewHolder {
        val itemBinding =
            ListEntireNoticeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        // 뷰 홀더 생성과 함께 holderList에 추가
        val holder = TeacherEntireNoticeAdapter.TeacherViewHolder(itemBinding, cbListener)
        holderList.add(holder)

        return holder
    }

    override fun getItemCount(): Int {
        return rvEntireNoticeList.size
    }

    override fun onBindViewHolder(holder: TeacherEntireNoticeAdapter.TeacherViewHolder, position: Int)  {
        holder.className.text = rvEntireNoticeList[position].class_name
        holder.subjectName.text = rvEntireNoticeList[position].subject_name

        // 현재 체크된 체크박스의 위치
        val selectedPosition = checkedPosition

        // 데이터를 가져올 때, 체크박스의 상태를 초기화 (체크 안되도록 설정)
        holder.deleteCheckbox.isChecked = false

        holder.deleteCheckbox.setOnClickListener { v ->
            if (holder.deleteCheckbox.isChecked()) {
                // 체크박스가 체크되었을 때
                checkedPosition = position
                cbListener?.onClickCheckBox(1, position)
            } else {
                // 체크박스가 체크 해제되었을 때
                checkedPosition = -1
                cbListener?.onClickCheckBox(0, position)
            }

            // 모든 뷰 홀더의 체크박스 상태 변경
            holderList.forEachIndexed { index, holder ->
                holder.deleteCheckbox.isChecked = index == checkedPosition
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