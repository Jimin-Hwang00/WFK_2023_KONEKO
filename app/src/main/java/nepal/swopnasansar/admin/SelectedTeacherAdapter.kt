package nepal.swopnasansar.admin

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import nepal.swopnasansar.admin.data.TeacherDto
import nepal.swopnasansar.databinding.ListTeacherAndAccountBinding

class SelectedTeacherAdapter (private val activity: Activity, val teacherList : ArrayList<TeacherDto>)
    : RecyclerView.Adapter<SelectedTeacherAdapter.TeacherViewHolder>() {
    val TAG = "SelectedTeacherAdapter"
    var firestore : FirebaseFirestore? = null
    var checkedPosition : Int = -1
    private val holderList = mutableListOf<TeacherViewHolder>()

    init {
        firestore = FirebaseFirestore.getInstance()
        firestore?.collection("teacher")?.get()?.addOnSuccessListener { result ->
            val tempList = ArrayList<TeacherDto>() // 새로운 리스트를 만듦
            for (snapshot in result) {
                tempList.add(snapshot.toObject(TeacherDto::class.java))
            }
            // 기존의 adminCalList를 지우고 정렬된 요소들을 추가
            teacherList.clear()
            teacherList.addAll(tempList)

            (activity as? SelectTeacherActivity)?.hideProgressBar()
            notifyDataSetChanged()
        }
    }

    fun onUpdateList(){
        firestore = FirebaseFirestore.getInstance()
        firestore?.collection("teacher")?.get()?.addOnSuccessListener { result ->
            val tempList = ArrayList<TeacherDto>() // 새로운 리스트를 만듦
            for (snapshot in result) {
                tempList.add(snapshot.toObject(TeacherDto::class.java))
            }
            // 기존의 adminCalList를 지우고 정렬된 요소들을 추가
            teacherList.clear()
            teacherList.addAll(tempList)

            (activity as? SelectTeacherActivity)?.hideProgressBar()
            notifyDataSetChanged()
        }
    }

    class TeacherViewHolder(
        val itemBinding: ListTeacherAndAccountBinding,
        cbListener: onCheckBoxClickListener?
    ) : RecyclerView.ViewHolder(itemBinding.root) {
        val name = itemBinding.nameTv
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
    ): SelectedTeacherAdapter.TeacherViewHolder {
        val itemBinding =
            ListTeacherAndAccountBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        // 뷰 홀더 생성과 함께 holderList에 추가
        val holder = SelectedTeacherAdapter.TeacherViewHolder(itemBinding, cbListener)
        holderList.add(holder)

        return holder
    }

    override fun getItemCount(): Int {
        return teacherList.size
    }

    override fun onBindViewHolder(holder: SelectedTeacherAdapter.TeacherViewHolder, position: Int)  {
        val teachersToRemove = mutableListOf<TeacherDto>()
        // 현재 체크된 체크박스의 위치
        val selectedPosition = checkedPosition

        holder.name.text = teacherList[position].teacher_name
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

        if(activity is TeacherListActivity){
            holder.deleteCheckbox.visibility = View.INVISIBLE
        }
        if(activity is SelectTeacherActivity){
            holder.deleteCheckbox.visibility = View.VISIBLE
        }
        if(activity is EditTeacherActivity){
            holder.deleteCheckbox.visibility = View.VISIBLE
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