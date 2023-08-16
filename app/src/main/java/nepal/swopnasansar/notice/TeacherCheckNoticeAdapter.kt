package nepal.swopnasansar.notice

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import nepal.swopnasansar.R
import nepal.swopnasansar.data.NoticeDto
import nepal.swopnasansar.data.RvCheckNoticeDto
import nepal.swopnasansar.databinding.ListCheckNoticeBinding

class TeacherCheckNoticeAdapter (val rvCheckNoticeList : ArrayList<RvCheckNoticeDto>, val activity: AppCompatActivity)
    : RecyclerView.Adapter<TeacherCheckNoticeAdapter.TeacherViewHolder>() {
    val TAG = "TeacherCheckNoticeAdapter"
    var firestore: FirebaseFirestore? = null
    var noticeTempList = ArrayList<NoticeDto>() // 빈 ArrayList로 초기화
    private val holderList = mutableListOf<TeacherViewHolder>()
    var selectedPosition = -1

    init {
        CoroutineScope(Dispatchers.IO).launch {
            firestore = FirebaseFirestore.getInstance()

            noticeTempList.clear()
            val classQuerySnapshot = firestore?.collection("notice")?.get()?.await()
            noticeTempList.addAll(classQuerySnapshot?.toObjects(NoticeDto::class.java) ?: emptyList())

            // 데이터 처리 및 어댑터 갱신
            withContext(Dispatchers.Main) {
                rvCheckNoticeList.clear()
                for (i in 0 until noticeTempList.size) {
                    rvCheckNoticeList.add(
                        RvCheckNoticeDto(noticeTempList.get(i).title, noticeTempList.get(i).content,
                            noticeTempList.get(i).receiver_name, noticeTempList.get(i).notice_key)
                    )
                }
                (activity as? TeacherCheckNoticeActivity)?.hideProgressBar()
                notifyDataSetChanged()
            }
        }
    }

    fun onUpdateList() {
        CoroutineScope(Dispatchers.IO).launch {
            firestore = FirebaseFirestore.getInstance()

            noticeTempList.clear()
            val classQuerySnapshot = firestore?.collection("notice")?.get()?.await()
            noticeTempList.addAll(classQuerySnapshot?.toObjects(NoticeDto::class.java) ?: emptyList())

            // 데이터 처리 및 어댑터 갱신
            withContext(Dispatchers.Main) {
                rvCheckNoticeList.clear()
                for (i in 0 until noticeTempList.size) {
                    rvCheckNoticeList.add(
                        RvCheckNoticeDto(noticeTempList.get(i).title, noticeTempList.get(i).content,
                            noticeTempList.get(i).receiver_name, noticeTempList.get(i).notice_key)
                    )
                }
                (activity as? TeacherCheckNoticeActivity)?.hideProgressBar()
                notifyDataSetChanged()
            }
        }
    }

    class TeacherViewHolder(
        val itemBinding: ListCheckNoticeBinding,
        listener: TeacherCheckNoticeAdapter.OnItemClickListener?
    ) : RecyclerView.ViewHolder(itemBinding.root) {
        val title = itemBinding.titleTv

        init {
            itemBinding.root.setOnClickListener {
                listener?.onClick(it, adapterPosition)
                true
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TeacherCheckNoticeAdapter.TeacherViewHolder {
        val itemBinding =
            ListCheckNoticeBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        // 뷰 홀더 생성과 함께 holderList에 추가
        val holder = TeacherCheckNoticeAdapter.TeacherViewHolder(itemBinding, listener)
        holderList.add(holder)

        return holder
    }

    override fun getItemCount(): Int {
        return rvCheckNoticeList.size
    }

    override fun onBindViewHolder(
        holder: TeacherCheckNoticeAdapter.TeacherViewHolder,
        position: Int
    ) {
        val isSelected = position == selectedPosition
        holder.title.text = rvCheckNoticeList[position].title

        // 선택한 홀더의 백그라운드 색 변경
        if (isSelected) {
            holder.itemView.setBackgroundColor(Color.LTGRAY)
        } else {
            holder.itemView.setBackgroundColor(Color.WHITE)
        }
    }

    interface OnItemClickListener {
        fun onClick(view: View, position: Int)
    }

    var listener: OnItemClickListener? = null

    fun setOnClickListener(listener: OnItemClickListener?) {
        this.listener = listener
    }
}