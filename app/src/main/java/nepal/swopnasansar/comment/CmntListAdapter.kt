package nepal.swopnasansar.comment

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import nepal.swopnasansar.R
import nepal.swopnasansar.dto.Comment

class CmntListAdapter(var itemList: ArrayList<Comment>, var context: Context): RecyclerView.Adapter<CmntListAdapter.ViewHolder>() {
    private val TAG = "CmntListAdapter"

    var selectedIdx = -1

    private var clickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CmntListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cmnt_item, parent, false)
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
        holder.title_text.text = itemList[position].title
        holder.name_text.text = itemList[position].author_name
        holder.date_text.text = itemList[position].date.substring(0, 10)

        Log.d(TAG, context.toString())

        if (context is ReceivedCmntListAcitivity) {
            if (selectedIdx == position) {
                holder.itemView.setBackgroundColor(Color.LTGRAY)
            } else if (itemList[position].read) {
                holder.itemView.setBackgroundColor(Color.parseColor("#00000000"))
            } else {
                holder.itemView.setBackgroundColor(Color.WHITE)
            }
        } else {
            if (selectedIdx == position) {
                holder.itemView.setBackgroundColor(Color.LTGRAY)
            } else {
                holder.itemView.setBackgroundColor(Color.parseColor("#00000000"))
            }
        }

        holder.itemView.setOnClickListener { view ->
            clickListener?.onItemClick(position)
        }
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var title_text = itemView.findViewById<TextView>(R.id.tv_received_cmnt_title)
        var name_text = itemView.findViewById<TextView>(R.id.tv_received_cmnt_author)
        var date_text = itemView.findViewById<TextView>(R.id.tv_received_cmnt_date)
    }

    fun updateData(newItems: ArrayList<Comment>) {
        itemList.clear()
        itemList.addAll(newItems)
        notifyDataSetChanged()
    }
}