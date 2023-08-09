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
import nepal.swopnasansar.databinding.ListCheckNoticeBinding
import nepal.swopnasansar.notice.data.NoticeDto
import nepal.swopnasansar.notice.data.RvParentNoticeDto

class ParentCheckNoticeAdapter (val rvCheckNoticeList : ArrayList<RvParentNoticeDto>)
    : RecyclerView.Adapter<ParentCheckNoticeAdapter.ParentViewHolder>() {
    val TAG = "ParentCheckNoticeAdapter"
    var firestore: FirebaseFirestore? = null
    var noticeTempList = ArrayList<NoticeDto>() // 빈 ArrayList로 초기화
    private val holderList = mutableListOf<ParentViewHolder>()
    var selectedPosition = -1
    val uid = "test_key"

    init {
        CoroutineScope(Dispatchers.IO).launch {
            firestore = FirebaseFirestore.getInstance()

            noticeTempList.clear()
            val classQuerySnapshot = firestore?.collection("notice")?.get()?.await()
            noticeTempList.addAll(classQuerySnapshot?.toObjects(NoticeDto::class.java) ?: emptyList())

            // 데이터 처리 및 어댑터 갱신
            withContext(Dispatchers.Main) {
                rvCheckNoticeList.clear()

                //아이디 값과 학생 키값 확인 후, 해당 공지만 읽기 가능
                if(uid.equals("test_key")) {
                    for (notice in noticeTempList) {
                        for (stn_key in notice.receiver_key) {
                            //학부모의 자식으로 저장된 학생 키값 비교하기(로그인 기능 후 추가)
                            if(stn_key.equals("0G5hP16nunujNarssHwE")){
                                rvCheckNoticeList.add(
                                    RvParentNoticeDto(notice.title, notice.content,
                                        notice.notice_key, stn_key)
                                )
                            }
                        }
                    }
                }
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

                //아이디 값과 학생 키값 확인 후, 해당 공지만 읽기 가능
                if(uid.equals("test_key")) {
                    for (notice in noticeTempList) {
                        for (stn_key in notice.receiver_key) {
                            //학부모의 자식으로 저장된 학생 키값 비교하기(로그인 기능 후 추가)
                            if(stn_key.equals("0G5hP16nunujNarssHwE")){
                                rvCheckNoticeList.add(
                                    RvParentNoticeDto(notice.title, notice.content,
                                        notice.notice_key, stn_key)
                                )
                            }
                        }
                    }
                }
                notifyDataSetChanged()
            }
        }
    }

    class ParentViewHolder(
        val itemBinding: ListCheckNoticeBinding,
        listener: ParentCheckNoticeAdapter.OnItemClickListener?
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
    ): ParentCheckNoticeAdapter.ParentViewHolder {
        val itemBinding =
            ListCheckNoticeBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        // 뷰 홀더 생성과 함께 holderList에 추가
        val holder = ParentCheckNoticeAdapter.ParentViewHolder(itemBinding, listener)
        holderList.add(holder)

        return holder
    }

    override fun getItemCount(): Int {
        return rvCheckNoticeList.size
    }

    override fun onBindViewHolder(
        holder: ParentCheckNoticeAdapter.ParentViewHolder,
        position: Int
    ) {
        val isSelected = position == selectedPosition
        holder.title.text = rvCheckNoticeList[position].title

        // 선택한 홀더의 백그라운드 색 변경
        if (isSelected) {
            holder.itemView.setBackgroundColor(Color.GRAY)
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