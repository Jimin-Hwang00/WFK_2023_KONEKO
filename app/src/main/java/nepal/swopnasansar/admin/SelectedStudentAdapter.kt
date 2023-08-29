package nepal.swopnasansar.admin

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import nepal.swopnasansar.data.StudentDto
import nepal.swopnasansar.databinding.ListStudentBinding

class SelectedStudentAdapter (private val activity: Activity, val studentList : ArrayList<StudentDto>)
    : RecyclerView.Adapter<SelectedStudentAdapter.StudentViewHolder>() {
    val TAG = "SelectedStudentAdapter"
    var firestore : FirebaseFirestore? = null
    var TempList = ArrayList<StudentDto>() // 빈 ArrayList로 초기화

    init {
        firestore = FirebaseFirestore.getInstance()
        firestore?.collection("student")?.get()?.addOnSuccessListener { result ->
            val tempList = ArrayList<StudentDto>() // 새로운 리스트를 만듦
            for (snapshot in result) {
                tempList.add(snapshot.toObject(StudentDto::class.java))
            }
            // 기존의 adminCalList를 지우고 정렬된 요소들을 추가
            studentList.clear()
            studentList.addAll(tempList)

            if(activity is StudentListActivity){
                (activity as? StudentListActivity)?.hideProgressBar()
            }
            if(activity is SelectStudentActivity){
                (activity as? SelectStudentActivity)?.hideProgressBar()
            }
            if(activity is EditStudentActivity){
                (activity as? EditStudentActivity)?.hideProgressBar()
            }
            notifyDataSetChanged()
        }
    }

    fun onUpdateList(){
        firestore = FirebaseFirestore.getInstance()
        firestore?.collection("student")?.get()?.addOnSuccessListener { result ->
            val tempList = ArrayList<StudentDto>() // 새로운 리스트를 만듦
            for (snapshot in result) {
                tempList.add(snapshot.toObject(StudentDto::class.java))
            }
            // 기존의 adminCalList를 지우고 정렬된 요소들을 추가
            studentList.clear()
            studentList.addAll(tempList)

            if(activity is StudentListActivity){
                (activity as? StudentListActivity)?.hideProgressBar()
            }
            if(activity is SelectStudentActivity){
                (activity as? SelectStudentActivity)?.hideProgressBar()
            }
            if(activity is EditStudentActivity){
                (activity as? EditStudentActivity)?.hideProgressBar()
            }

            notifyDataSetChanged()
        }
    }

    class StudentViewHolder(
        val itemBinding: ListStudentBinding,
        cbListener: onCheckBoxClickListener?
    ) : RecyclerView.ViewHolder(itemBinding.root) {
        val name = itemBinding.studentName
        val email = itemBinding.studentEmail
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
    ): SelectedStudentAdapter.StudentViewHolder {
        val itemBinding =
            ListStudentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SelectedStudentAdapter.StudentViewHolder(itemBinding, cbListener)
    }

    override fun getItemCount(): Int {
        return studentList.size
    }

    override fun onBindViewHolder(holder: SelectedStudentAdapter.StudentViewHolder, position: Int)  {
        val studentsToRemove = mutableListOf<StudentDto>()

        holder.name.text = studentList[position].stn_name
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

        if(activity is StudentListActivity){
            holder.deleteCheckbox.visibility = View.INVISIBLE
        }
        if(activity is SelectStudentActivity){
            holder.deleteCheckbox.visibility = View.VISIBLE
            holder.email.visibility = View.INVISIBLE
        }
        if(activity is EditStudentActivity){
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