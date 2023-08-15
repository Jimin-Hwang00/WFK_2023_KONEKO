package nepal.swopnasansar.homework

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import nepal.swopnasansar.R
import nepal.swopnasansar.homework.dto.Homework

class THWAdapter(var itemList: ArrayList<Homework>): RecyclerView.Adapter<THWAdapter.ViewHolder>() {
    var selectedIdx = -1

    private var clickListener: OnItemClickListener? = null

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var tvClassName = itemView.findViewById<TextView>(R.id.tv_uploaded_hw_item_class)
        var tvSubjectName = itemView.findViewById<TextView>(R.id.tv_uploaded_hw_item_subject)
        var tvTitle = itemView.findViewById<TextView>(R.id.tv_uploaded_hw_item_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): THWAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.t_hw_item, parent, false)
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
        holder.tvTitle.text = item.title

        if (position == selectedIdx) {
            holder.itemView.setBackgroundColor(Color.LTGRAY)
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#00000000"))
        }

        holder.itemView.setOnClickListener { view ->
            clickListener?.onItemClick(position)
        }
    }

    fun updateData(newItems: ArrayList<Homework>) {
        itemList.clear()
        itemList.addAll(newItems)
        notifyDataSetChanged()
    }
}