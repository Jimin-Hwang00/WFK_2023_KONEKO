package nepal.swopnasansar.homework

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import nepal.swopnasansar.R
import nepal.swopnasansar.dto.SPSubmitItem

class SPSubmittedHWAdapter(var itemList: ArrayList<SPSubmitItem>): RecyclerView.Adapter<SPSubmittedHWAdapter.ViewHolder>() {
    var selectedIdx = -1

    private var clickListener: OnItemClickListener? = null

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvSubjectName = itemView.findViewById<TextView>(R.id.tv_sp_hw_item_subject)
        val tvTitle = itemView.findViewById<TextView>(R.id.tv_sp_hw_item_title)
        val tvDate = itemView.findViewById<TextView>(R.id.tv_sp_hw_item_date)
        val tvSubmit = itemView.findViewById<TextView>(R.id.tv_sp_hw_item_submit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sp_hw_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.clickListener = listener
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item = itemList[position]

        holder.tvSubjectName.text = item.subjectName
        holder.tvTitle.text = item.title
        holder.tvDate.text = item.date.substring(0, 10)
        if (item.idx != null) {
            holder.tvSubmit.text = "O"
        } else {
            holder.tvSubmit.text = "X"
        }

        if (position == selectedIdx) {
            holder.itemView.setBackgroundColor(Color.LTGRAY)
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#00000000"))
        }

        holder.itemView.setOnClickListener { view ->
            clickListener?.onItemClick(position)
        }
    }

    fun updateData(newItems: ArrayList<SPSubmitItem>) {
        itemList.clear()
        itemList.addAll(newItems)
        notifyDataSetChanged()
    }
}