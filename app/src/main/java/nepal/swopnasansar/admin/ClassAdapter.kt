package nepal.swopnasansar.admin

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import nepal.swopnasansar.data.ClassDto
import nepal.swopnasansar.data.RvClassListDto
import nepal.swopnasansar.data.SubjectDto
import nepal.swopnasansar.data.TeacherDto
import nepal.swopnasansar.databinding.ListClassBinding
import nepal.swopnasansar.databinding.ListTeacherAndAccountBinding

class ClassAdapter(private val activity: Activity, val rvClassList : ArrayList<RvClassListDto>)
    : RecyclerView.Adapter<ClassAdapter.ClassViewHolder>() {
    val TAG = "ClassAdapter"
    var firestore : FirebaseFirestore? = null
    var classTempList = ArrayList<ClassDto>() // 빈 ArrayList로 초기화
    var teacherTempList = ArrayList<TeacherDto>() // 빈 ArrayList로 초기화

    init {
        CoroutineScope(Dispatchers.IO).launch {
            firestore = FirebaseFirestore.getInstance()

            // 클래스 정보 가져오기
            classTempList.clear()
            val classQuerySnapshot = firestore?.collection("class")?.get()?.await()
            classTempList.addAll(classQuerySnapshot?.toObjects(ClassDto::class.java) ?: emptyList())

            for(c in classTempList){
                Log.d(TAG, "${c.class_name}")
            }

            // 선생님 정보 가져오기
            teacherTempList.clear()
            for (c in classTempList) {
                Log.d(TAG, "선생키.. 가져완거 : ${c.teacher_key}")
                val teacherQuerySnapshot = firestore?.collection("teacher")?.whereEqualTo("teacher_key", c.teacher_key)?.get()?.await()
                teacherTempList.addAll(teacherQuerySnapshot?.toObjects(TeacherDto::class.java) ?: emptyList())
            }

            for(t in teacherTempList){
                Log.d(TAG, "${t.teacher_name}")
            }

            // 데이터 처리 및 어댑터 갱신
            withContext(Dispatchers.Main) {
                val minSize = minOf(classTempList.size, teacherTempList.size)
                rvClassList.clear()
                for (i in 0 until minSize) {
                    rvClassList.add(
                        RvClassListDto(classTempList[i].class_name, teacherTempList[i].teacher_name,
                        "", classTempList[i].class_key, "")
                    )
                }
                (activity as? ClassListActivity)?.hideProgressBar()
                notifyDataSetChanged()
            }
        }
    }

    class ClassViewHolder(
        val itemBinding: ListClassBinding,
        lcListener: ClassAdapter.OnItemLongClickListener?
    ) : RecyclerView.ViewHolder(itemBinding.root) {
        val name = itemBinding.nameTv
        val className = itemBinding.classNameTv

        init {
            /*list_item 의 root 항목(ConstraintLayout) 롱클릭 시*/
            itemBinding.root.setOnLongClickListener {
                lcListener?.onItemLongClick(it, adapterPosition)
                true
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ClassAdapter.ClassViewHolder {
        val itemBinding =
            ListClassBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ClassAdapter.ClassViewHolder(itemBinding, lcListener)
    }

    override fun getItemCount(): Int {
        return rvClassList.size
    }

    override fun onBindViewHolder(holder: ClassAdapter.ClassViewHolder, position: Int)  {
        holder.name.text = rvClassList[position].teacher_name
        holder.className.text = rvClassList[position].class_name

    }

    //이벤트 삭제시
    interface OnItemLongClickListener {
        fun onItemLongClick(view: View, position: Int)
    }

    var lcListener: OnItemLongClickListener? = null

    fun setOnItemLongClickListener(listener: OnItemLongClickListener?) {
        this.lcListener = listener
    }
}