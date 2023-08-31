package nepal.swopnasansar.admin

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import nepal.swopnasansar.data.StudentDto
import nepal.swopnasansar.data.TempDto
import nepal.swopnasansar.databinding.ListStudentBinding


class StudentAdapter(private val activity: Activity, val studentList : ArrayList<TempDto>)
    : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {
    val TAG = "StudentAdapter"
    var firestore : FirebaseFirestore? = null
    var TempList = ArrayList<TempDto>() // 빈 ArrayList로 초기화
    var checkBoxList = ArrayList<Boolean>()

    init {
        firestore = FirebaseFirestore.getInstance()

        CoroutineScope(Dispatchers.IO).launch {
            firestore = FirebaseFirestore.getInstance()

            TempList.clear()
            val accountantQuerySnapshot = firestore?.collection("temp")?.whereEqualTo("role", "student")?.get()?.await()
            TempList.addAll(accountantQuerySnapshot?.toObjects(TempDto::class.java) ?: emptyList())

            // 데이터 처리 및 어댑터 갱신
            withContext(Dispatchers.Main) {
                studentList.clear()
                studentList.addAll(TempList)

                checkBoxList.clear()
                checkBoxList.addAll(List(studentList.size) { false })

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
    }

    fun onUpdateList(){
        firestore = FirebaseFirestore.getInstance()

        CoroutineScope(Dispatchers.IO).launch {
            firestore = FirebaseFirestore.getInstance()

            TempList.clear()
            val accountantQuerySnapshot = firestore?.collection("temp")?.whereEqualTo("role", "student")?.get()?.await()
            TempList.addAll(accountantQuerySnapshot?.toObjects(TempDto::class.java) ?: emptyList())

            // 데이터 처리 및 어댑터 갱신
            withContext(Dispatchers.Main) {
                studentList.clear()
                studentList.addAll(TempList)

                checkBoxList.clear()
                checkBoxList.addAll(List(studentList.size) { false })

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
    ): StudentAdapter.StudentViewHolder {
        val itemBinding =
            ListStudentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StudentAdapter.StudentViewHolder(itemBinding, cbListener)
    }

    override fun getItemCount(): Int {
        return studentList.size
    }

    override fun onBindViewHolder(holder: StudentAdapter.StudentViewHolder, position: Int)  {

        holder.name.text = studentList[position].name
        holder.email.text = studentList[position].email
        // 데이터를 가져올 때, 체크박스의 상태를 초기화 (체크 안되도록 설정)
        holder.deleteCheckbox.isChecked = false

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

        if(activity is StudentListActivity){
            holder.deleteCheckbox.visibility = View.INVISIBLE
        }
        if(activity is SelectStudentActivity){
            holder.deleteCheckbox.visibility = View.VISIBLE
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