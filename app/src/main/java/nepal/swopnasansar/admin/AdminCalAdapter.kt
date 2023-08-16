package nepal.swopnasansar.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import nepal.swopnasansar.admin.data.AdminCalDto
import nepal.swopnasansar.databinding.ListItemBinding
import java.text.SimpleDateFormat
import java.util.Locale

class AdminCalAdapter(var adminCalList : ArrayList<AdminCalDto>, val activity: AppCompatActivity)
    : RecyclerView.Adapter<AdminCalAdapter.AdminCalViewHolder>() {
    val TAG = "AdminCalAdapter"
    var firestore : FirebaseFirestore? = null

    init {
        firestore = FirebaseFirestore.getInstance()
        firestore?.collection("schedule")?.get()?.addOnSuccessListener { result ->
            val tempList = ArrayList<AdminCalDto>() // 새로운 리스트를 만듦
            for (snapshot in result) {
                tempList.add(snapshot.toObject(AdminCalDto::class.java))
            }

            // date 값을 비교하여 정렬
            tempList.sortWith(Comparator { o1, o2 ->
                val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val date1 = format.parse(o1.date)
                val date2 = format.parse(o2.date)
                date1?.compareTo(date2) ?: 0
            })

            // 기존의 adminCalList를 지우고 정렬된 요소들을 추가
            adminCalList.clear()
            adminCalList.addAll(tempList)

            if(activity is CheckEventActivity){
                (activity as? CheckEventActivity)?.hideProgressBar()
            }
            if(activity is UserCheckEventActivity){
                (activity as? UserCheckEventActivity)?.hideProgressBar()
            }
            notifyDataSetChanged()
        }
    }

    class AdminCalViewHolder(
        val itemBinding: ListItemBinding,
        lcListener: OnItemLongClickListener?
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        val dayOfMonth = itemBinding.dayOfMonth
        val event = itemBinding.eventViewTv

        init {
            /*list_item 의 root 항목(ConstraintLayout) 롱클릭 시*/
            itemBinding.root.setOnLongClickListener {
                lcListener?.onItemLongClick(it, adapterPosition)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminCalViewHolder {
        val itemBinding =
            ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdminCalViewHolder(itemBinding, lcListener)
    }

    override fun getItemCount(): Int {
        return adminCalList.size
    }

    override fun onBindViewHolder(holder: AdminCalViewHolder, position: Int) {
        holder.event.text = adminCalList[position].event
        val spliteddateList = adminCalList[position].date.split("-")
        holder.dayOfMonth.text = spliteddateList[2]
    }

    //이벤트 삭제시
    interface OnItemLongClickListener {
        fun onItemLongClick(view: View, position: Int)
    }

    var lcListener: OnItemLongClickListener? = null

    fun setOnItemLongClickListener(listener: OnItemLongClickListener?) {
        this.lcListener = listener
    }
    fun updateList(newList: ArrayList<AdminCalDto>) {
        adminCalList = newList
        notifyDataSetChanged()
    }
}