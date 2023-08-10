package nepal.swopnasansar.youtube

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import nepal.swopnasansar.R
import nepal.swopnasansar.youtube.dto.YoutubeListItem

class YoutubeLinkAdapter(private var itemList: ArrayList<YoutubeListItem>?): RecyclerView.Adapter<YoutubeLinkAdapter.ViewHolder>() {
    var youtubeItems = itemList

    private var clickListener: YoutubeLinkAdapter.OnItemClickListener? = null

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvSubject = itemView.findViewById<TextView>(R.id.tv_check_youtube_item_subject)
        val tvTitle = itemView.findViewById<TextView>(R.id.tv_check_youtube_item_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.check_youtube_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        if (youtubeItems != null) {
            return youtubeItems!!.size
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
        if (youtubeItems != null) {
            val youtubeItem = youtubeItems!![position]

            holder.tvSubject.text = youtubeItem.subject
            holder.tvTitle.text = youtubeItem.title
        } else {
            holder.tvSubject.text = ""
            holder.tvTitle.text = ""
        }

        holder.itemView.setOnClickListener { view ->
            clickListener?.onItemClick(position)
        }
    }

}