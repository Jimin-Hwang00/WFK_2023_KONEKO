package nepal.swopnasansar.admin

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import nepal.swopnasansar.admin.data.AccountantDto
import nepal.swopnasansar.admin.data.TeacherDto
import nepal.swopnasansar.databinding.ListTeacherAndAccountBinding

class AccountantAdapter(private val activity: Activity, val accountantList : ArrayList<AccountantDto>)
    : RecyclerView.Adapter<AccountantAdapter.AccountantViewHolder>() {
    val TAG = "AccountantAdapter"
    var firestore : FirebaseFirestore? = null

    init {
        firestore = FirebaseFirestore.getInstance()
        firestore?.collection("accountant")?.get()?.addOnSuccessListener { result ->
            val tempList = ArrayList<AccountantDto>() // 새로운 리스트를 만듦
            for (snapshot in result) {
                tempList.add(snapshot.toObject(AccountantDto::class.java))
            }
            // 기존의 adminCalList를 지우고 정렬된 요소들을 추가
            accountantList.clear()
            accountantList.addAll(tempList)

            for(i in accountantList){
                Log.d(TAG, "init ${i.accountant_key}, ${i.accountant_name}")
            }

            if(activity is AccountantListActivity){
                (activity as? AccountantListActivity)?.hideProgressBar()
            }
            if(activity is EditAccountantActivity){
                (activity as? EditAccountantActivity)?.hideProgressBar()
            }
            notifyDataSetChanged()
        }
    }

    fun onUpdateList(){
        firestore = FirebaseFirestore.getInstance()
        firestore?.collection("accountant")?.get()?.addOnSuccessListener { result ->
            val tempList = ArrayList<AccountantDto>() // 새로운 리스트를 만듦
            for (snapshot in result) {
                tempList.add(snapshot.toObject(AccountantDto::class.java))
            }
            // 기존의 adminCalList를 지우고 정렬된 요소들을 추가
            accountantList.clear()
            accountantList.addAll(tempList)

            if(activity is AccountantListActivity){
                (activity as? AccountantListActivity)?.hideProgressBar()
            }
            if(activity is EditAccountantActivity){
                (activity as? EditAccountantActivity)?.hideProgressBar()
            }
            notifyDataSetChanged()
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

        holder.name.text = accountantList[position].accountant_name
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