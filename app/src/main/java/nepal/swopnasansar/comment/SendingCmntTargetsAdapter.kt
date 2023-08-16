package nepal.swopnasansar.comment

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import nepal.swopnasansar.R
import nepal.swopnasansar.comment.dao.CommentDAO
import nepal.swopnasansar.comment.dto.ReceiverTarget

class SendingCmntTargetsAdapter(var itemList: ArrayList<ReceiverTarget>, var context: Context): RecyclerView.Adapter<SendingCmntTargetsAdapter.ViewHolder>() {
    val dao = CommentDAO()

    private var clickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SendingCmntTargetsAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.select_cmnt_target_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.count()
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.clickListener = listener
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.target_text.text = itemList[position].category
        holder.name_text.text = itemList[position].name

        holder.itemView.setOnClickListener { view ->
            clickListener?.onItemClick(position)
        }

        if (itemList[position].selected) {
            holder.itemView.setBackgroundColor(Color.LTGRAY)
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#00000000"))
        }
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var target_text = itemView.findViewById<TextView>(R.id.tv_sending_cmnt_item_category)
        var name_text = itemView.findViewById<TextView>(R.id.tv_sending_cmnt_item_name)
    }

    fun updateData(newItems: ArrayList<ReceiverTarget>) {
        itemList.clear()
        itemList.addAll(newItems)
        notifyDataSetChanged()
    }


}