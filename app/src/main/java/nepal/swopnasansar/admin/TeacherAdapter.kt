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
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import nepal.swopnasansar.data.StudentDto
import nepal.swopnasansar.data.TeacherDto
import nepal.swopnasansar.data.TempDto
import nepal.swopnasansar.databinding.ListTeacherAndAccountBinding

class TeacherAdapter (private val activity: Activity, val teacherList : ArrayList<TempDto>)
    : RecyclerView.Adapter<TeacherAdapter.TeacherViewHolder>() {
    val TAG = "TeacherAdapter"
    var firestore : FirebaseFirestore? = null
    var TempList = ArrayList<TempDto>() // 빈 ArrayList로 초기화

    init {
        firestore = FirebaseFirestore.getInstance()

        CoroutineScope(Dispatchers.IO).launch {
            firestore = FirebaseFirestore.getInstance()

            TempList.clear()
            val accountantQuerySnapshot = firestore?.collection("temp")?.whereEqualTo("role", "teacher")?.get()?.await()
            TempList.addAll(accountantQuerySnapshot?.toObjects(TempDto::class.java) ?: emptyList())

            // 데이터 처리 및 어댑터 갱신
            withContext(Dispatchers.Main) {
                teacherList.clear()
                teacherList.addAll(TempList)

                if(activity is TeacherListActivity){
                    (activity as? TeacherListActivity)?.hideProgressBar()
                }
                if(activity is EditTeacherActivity){
                    (activity as? EditTeacherActivity)?.hideProgressBar()
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
            val accountantQuerySnapshot = firestore?.collection("temp")?.whereEqualTo("role", "teacher")?.get()?.await()
            TempList.addAll(accountantQuerySnapshot?.toObjects(TempDto::class.java) ?: emptyList())

            // 데이터 처리 및 어댑터 갱신
            withContext(Dispatchers.Main) {
                teacherList.clear()
                teacherList.addAll(TempList)

                if(activity is TeacherListActivity){
                    (activity as? TeacherListActivity)?.hideProgressBar()
                }
                if(activity is EditTeacherActivity){
                    (activity as? EditTeacherActivity)?.hideProgressBar()
                }
                notifyDataSetChanged()
            }
        }
    }

    class TeacherViewHolder(
        val itemBinding: ListTeacherAndAccountBinding,
        cbListener: onCheckBoxClickListener?
    ) : RecyclerView.ViewHolder(itemBinding.root) {
        val name = itemBinding.nameTv
        val email = itemBinding.emailTv
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

        holder.name.text = teacherList[position].name
        holder.email.text = teacherList[position].email
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