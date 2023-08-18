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
import nepal.swopnasansar.data.AccountantDto
import nepal.swopnasansar.data.ClassDto
import nepal.swopnasansar.data.RvClassListDto
import nepal.swopnasansar.data.SubjectDto
import nepal.swopnasansar.data.TeacherDto
import nepal.swopnasansar.data.TempDto
import nepal.swopnasansar.databinding.ListTeacherAndAccountBinding

class AccountantAdapter(private val activity: Activity, val accountantList : ArrayList<TempDto>)
    : RecyclerView.Adapter<AccountantAdapter.AccountantViewHolder>() {
    val TAG = "AccountantAdapter"
    var firestore : FirebaseFirestore? = null
    var TempList = ArrayList<TempDto>() // 빈 ArrayList로 초기화

    init {
        firestore = FirebaseFirestore.getInstance()

        CoroutineScope(Dispatchers.IO).launch {
            firestore = FirebaseFirestore.getInstance()

            TempList.clear()
            val accountantQuerySnapshot = firestore?.collection("temp")?.whereEqualTo("role", "accountant")?.get()?.await()
            TempList.addAll(accountantQuerySnapshot?.toObjects(TempDto::class.java) ?: emptyList())

            // 데이터 처리 및 어댑터 갱신
            withContext(Dispatchers.Main) {
                accountantList.clear()
                accountantList.addAll(TempList)

                if(activity is AccountantListActivity){
                    (activity as? AccountantListActivity)?.hideProgressBar()
                }
                if(activity is EditAccountantActivity){
                    (activity as? EditAccountantActivity)?.hideProgressBar()
                }
                notifyDataSetChanged()
            }
        }
    }

    fun onUpdateList(){
        firestore = FirebaseFirestore.getInstance()

        CoroutineScope(Dispatchers.IO).launch {
            Log.d(TAG, "class어댑터 초기화")
            firestore = FirebaseFirestore.getInstance()

            TempList.clear()
            val accountantQuerySnapshot = firestore?.collection("temp")?.whereEqualTo("role", "accountant")?.get()?.await()
            TempList.addAll(accountantQuerySnapshot?.toObjects(TempDto::class.java) ?: emptyList())

            // 데이터 처리 및 어댑터 갱신
            withContext(Dispatchers.Main) {
                accountantList.clear()
                accountantList.addAll(TempList)

                if(activity is AccountantListActivity){
                    (activity as? AccountantListActivity)?.hideProgressBar()
                }
                if(activity is EditAccountantActivity){
                    (activity as? EditAccountantActivity)?.hideProgressBar()
                }
                notifyDataSetChanged()
            }
        }
    }

    class AccountantViewHolder(
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
    ): AccountantAdapter.AccountantViewHolder {
        val itemBinding =
            ListTeacherAndAccountBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AccountantAdapter.AccountantViewHolder(itemBinding, cbListener)
    }

    override fun getItemCount(): Int {
        return accountantList.size
    }

    override fun onBindViewHolder(holder: AccountantAdapter.AccountantViewHolder, position: Int)  {
        val accountantsToRemove = mutableListOf<AccountantDto>()

        holder.name.text = accountantList[position].name
        // 데이터를 가져올 때, 체크박스의 상태를 초기화 (체크 안되도록 설정)
        holder.deleteCheckbox.isChecked = false

        holder.deleteCheckbox.setOnClickListener { v ->
            if (holder.deleteCheckbox.isChecked()) {
                // 체크가 되어 있음
                Log.d(TAG, "체크 됨 1")
                cbListener?.onClickCheckBox(1, position)
            } else {
                // 체크가 되어있지 않음
                Log.d(TAG, "체크 안됨 0")
                cbListener?.onClickCheckBox(0, position)
            }
        }

        if(activity is AccountantListActivity){
            holder.deleteCheckbox.visibility = View.INVISIBLE
        }
        if(activity is EditAccountantActivity){
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