package nepal.swopnasansar.admin

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import nepal.swopnasansar.data.StudentDto
import nepal.swopnasansar.data.TeacherDto
import nepal.swopnasansar.databinding.ListTeacherAndAccountBinding

class TeacherAdapter (private val activity: Activity, val teacherList : ArrayList<TeacherDto>)
    : RecyclerView.Adapter<TeacherAdapter.TeacherViewHolder>() {
    val TAG = "TeacherAdapter"
    var firestore : FirebaseFirestore? = null

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

            if(activity is TeacherListActivity){
                (activity as? TeacherListActivity)?.hideProgressBar()
            }
            if(activity is EditTeacherActivity){
                (activity as? EditTeacherActivity)?.hideProgressBar()
            }
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

            if(activity is TeacherListActivity){
                (activity as? TeacherListActivity)?.hideProgressBar()
            }
            if(activity is EditTeacherActivity){
                (activity as? EditTeacherActivity)?.hideProgressBar()
            }
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
    ): TeacherAdapter.TeacherViewHolder {
        val itemBinding =
            ListTeacherAndAccountBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TeacherAdapter.TeacherViewHolder(itemBinding, cbListener)
    }

    override fun getItemCount(): Int {
        return teacherList.size
    }

    override fun onBindViewHolder(holder: TeacherAdapter.TeacherViewHolder, position: Int)  {
        val teachersToRemove = mutableListOf<TeacherDto>()

        holder.name.text = teacherList[position].teacher_name
        // 데이터를 가져올 때, 체크박스의 상태를 초기화 (체크 안되도록 설정)
        holder.deleteCheckbox.isChecked = false

        holder.deleteCheckbox.setOnClickListener { v ->
            if (holder.deleteCheckbox.isChecked()) {
                // 체크가 되어 있음
                Log.d(TAG, "체크 됨 1")
                cbListener?.onClickCheckBox(1, position)
            } else {
                // 체크가 되어있지 않음
                cbListener?.onClickCheckBox(0, position)
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