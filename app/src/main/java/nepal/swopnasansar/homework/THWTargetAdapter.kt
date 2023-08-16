package nepal.swopnasansar.homework

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import nepal.swopnasansar.R
import nepal.swopnasansar.dto.HWTargetItem

class THWTargetAdapter(var itemList: ArrayList<HWTargetItem>): RecyclerView.Adapter<THWTargetAdapter.ViewHolder>() {
    var selectedIdx = -1

    private var clickListener: OnItemClickListener? = null

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var tvClassName = itemView.findViewById<TextView>(R.id.tv_hw_target_class)
        var tvSubjectName = itemView.findViewById<TextView>(R.id.tv_hw_target_subject)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): THWTargetAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.t_hw_target_item, parent, false)
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
        val item = itemList[position]

        holder.tvClassName.text = item.class_name
        holder.tvSubjectName.text = item.subject_name

        if (position == selectedIdx) {
            holder.itemView.setBackgroundColor(Color.LTGRAY)
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#00000000"))
        }

        holder.itemView.setOnClickListener { view ->
            clickListener?.onItemClick(position)
        }
    }

    fun updateData(newItems: ArrayList<HWTargetItem>) {
        itemList.clear()
        itemList.addAll(newItems)
        notifyDataSetChanged()
    }
}