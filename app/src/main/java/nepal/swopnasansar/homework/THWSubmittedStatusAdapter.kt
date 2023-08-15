package nepal.swopnasansar.homework

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import nepal.swopnasansar.R
import nepal.swopnasansar.homework.dto.TSubmitItem

class THWSubmittedStatusAdapter(var itemList: ArrayList<TSubmitItem>): RecyclerView.Adapter<THWSubmittedStatusAdapter.ViewHolder>() {
    private var clickListener: OnItemClickListener? = null

    var selectedIdx = -1

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var tvStnName = itemView.findViewById<TextView>(R.id.tv_submitted_hw_item_stn_name)
        var tvStatus = itemView.findViewById<TextView>(R.id.tv_submitted_hw_item_status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): THWSubmittedStatusAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.t_submitted_hw_item, parent, false)
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

        holder.tvStnName.text = item.stnName
        if (item.idx != null) {
            holder.tvStatus.text = "O"
        } else {
            holder.tvStatus.text = "X"
        }

        holder.itemView.setOnClickListener { view ->
            clickListener?.onItemClick(position)
        }

        if (position == selectedIdx) {
            holder.itemView.setBackgroundColor(Color.LTGRAY)
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#00000000"))
        }
    }

    fun updateData(newItems: ArrayList<TSubmitItem>) {
        itemList.clear()
        itemList.addAll(newItems)
        notifyDataSetChanged()
    }

}