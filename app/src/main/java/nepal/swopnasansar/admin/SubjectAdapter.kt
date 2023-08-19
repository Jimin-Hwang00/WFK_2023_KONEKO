package nepal.swopnasansar.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import nepal.swopnasansar.R
import nepal.swopnasansar.comment.CmntListAdapter
import nepal.swopnasansar.dto.Subject
import nepal.swopnasansar.dto.SubjectListItem

class SubjectAdapter(var itemList: ArrayList<SubjectListItem>): RecyclerView.Adapter<SubjectAdapter.ViewHolder>() {
    private var longClickListener: OnItemLongClickListener? = null

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val className = itemView.findViewById<TextView>(R.id.tv_subject_rv_class)
        val teacherName = itemView.findViewById<TextView>(R.id.tv_subject_rv_teacher)
        val subjectName = itemView.findViewById<TextView>(R.id.tv_subject_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_subject_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.count()
    }

    interface OnItemLongClickListener {
        fun onLongItemClick(position: Int, subjectListItem: SubjectListItem)
    }

    fun setOnItemLongClickListener(listener: OnItemLongClickListener) {
        this.longClickListener = listener
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.className.text = itemList[position].className
        holder.teacherName.text = itemList[position].teacherName
        holder.subjectName.text = itemList[position].subjectName

        holder.itemView.setOnLongClickListener { view ->
            longClickListener?.onLongItemClick(position, itemList[position])
            true
        }
    }

}