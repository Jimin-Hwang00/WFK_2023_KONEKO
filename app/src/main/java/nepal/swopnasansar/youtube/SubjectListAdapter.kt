package nepal.swopnasansar.youtube

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import nepal.swopnasansar.R
import nepal.swopnasansar.youtube.dto.Subject

class SubjectListAdapter(var itemList: ArrayList<Subject>?): RecyclerView.Adapter<SubjectListAdapter.ViewHolder>() {
    var subjects = itemList

    private var clickListener: OnItemClickListener? = null

    var selectedIdx = -1

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var tvSubject = itemView.findViewById<TextView>(R.id.tv_post_youtube_item_subject)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_youtube_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        if (subjects != null) {
            return subjects!!.size
        } else {
            return 0
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.clickListener = listener
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (subjects != null) {
            holder.tvSubject.text = subjects!![position].subject_name
        } else {
            holder.tvSubject.text = ""
        }

        holder.itemView.setOnClickListener { view ->
            clickListener?.onItemClick(position)
        }

        // make the selected subject appear in gray
        if (position == selectedIdx) {
            holder.itemView.setBackgroundColor(Color.LTGRAY)
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#00000000"))
        }
    }

}