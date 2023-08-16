package nepal.swopnasansar.attendance

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import nepal.swopnasansar.data.AttendanceDto
import nepal.swopnasansar.data.RvParentAttDto
import nepal.swopnasansar.data.StnAttDto
import nepal.swopnasansar.databinding.ListParentAttBinding
import java.text.SimpleDateFormat
import java.util.Locale

class ParentAttendanceAdapter (val rvCheckNoticeList : ArrayList<RvParentAttDto>)
    : RecyclerView.Adapter<ParentAttendanceAdapter.ParentAttViewHolder>() {
    val TAG = "ParentAttendanceAdapter"
    var firestore: FirebaseFirestore? = null
    var attTempList = ArrayList<AttendanceDto>() // 빈 ArrayList로 초기화
    var studentAttList = ArrayList<StnAttDto>()
    var dateList = ArrayList<String>()
    val uid = "test_key"

    init {
        CoroutineScope(Dispatchers.IO).launch {
            firestore = FirebaseFirestore.getInstance()

            attTempList.clear()
            val classQuerySnapshot = firestore?.collection("attendance")?.get()?.await()
            attTempList.addAll(
                classQuerySnapshot?.toObjects(AttendanceDto::class.java) ?: emptyList()
            )

            //아이디 값만 확인후, 이미 존재하는 키값을 활용해 출석 db에서 가져오기
            // 우선 부모와 일치하는 학생 키 값을 활용해 해당 학생의 att 정보만 불러오기
            if (uid.equals("test_key")) {
                for(att in attTempList){
                    for(stn in att.stn_list){
                        if(stn.stn_key.equals("Tu1obEVJEmE8GHwa6Jav")){
                            studentAttList.add(stn)
                            dateList.add(att.date)
                        }
                    }
                }
                // 데이터 처리 및 어댑터 갱신
                withContext(Dispatchers.Main) {
                    var i : Int
                    var check : String = ""
                    rvCheckNoticeList.clear()

                    i = 0
                    for(stn in studentAttList){
                        if(stn.check){
                            check = "O"
                        }else{
                            check = "X"
                        }
                        rvCheckNoticeList.add(RvParentAttDto(dateList[i], check, stn.stn_name, stn.stn_key))
                        i++
                    }

                    // date 값을 비교하여 정렬
                    rvCheckNoticeList.sortWith(Comparator { o1, o2 ->
                        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val date1 = format.parse(o1.date)
                        val date2 = format.parse(o2.date)
                        date1?.compareTo(date2) ?: 0
                    })

                    notifyDataSetChanged()
                }
            }
        }
    }
    fun onUpdateList() {
        CoroutineScope(Dispatchers.IO).launch {
            firestore = FirebaseFirestore.getInstance()

            attTempList.clear()
            val classQuerySnapshot = firestore?.collection("attendance")?.get()?.await()
            attTempList.addAll(
                classQuerySnapshot?.toObjects(AttendanceDto::class.java) ?: emptyList()
            )

            //아이디 값만 확인후, 이미 존재하는 키값을 활용해 출석 db에서 가져오기
            // 우선 부모와 일치하는 학생 키 값을 활용해 해당 학생의 att 정보만 불러오기
            if (uid.equals("test_key")) {
                for(att in attTempList){
                    for(stn in att.stn_list){
                        if(stn.stn_key.equals("Tu1obEVJEmE8GHwa6Jav")){
                            studentAttList.add(stn)
                            dateList.add(att.date)
                        }
                    }
                }
                // 데이터 처리 및 어댑터 갱신
                withContext(Dispatchers.Main) {
                    var i : Int
                    var check : String = ""
                    rvCheckNoticeList.clear()

                    i = 0
                    for(stn in studentAttList){
                        if(stn.check){
                            check = "O"
                        }else{
                            check = "X"
                        }
                        rvCheckNoticeList.add(RvParentAttDto(dateList[i], check, stn.stn_name, stn.stn_key))
                        i++
                    }
                    notifyDataSetChanged()
                }
            }
        }
    }

    class ParentAttViewHolder(
        val itemBinding: ListParentAttBinding
    ) : RecyclerView.ViewHolder(itemBinding.root) {
        val date = itemBinding.dateTv
        val check = itemBinding.attCheck
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ParentAttendanceAdapter.ParentAttViewHolder {
        val itemBinding =
            ListParentAttBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ParentAttendanceAdapter.ParentAttViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return rvCheckNoticeList.size
    }

    override fun onBindViewHolder(
        holder: ParentAttendanceAdapter.ParentAttViewHolder,
        position: Int
    ) {
        holder.date.text = rvCheckNoticeList[position].date
        holder.check.text = rvCheckNoticeList[position].check


        if (position == itemCount - 1) {
            (holder.itemView.context as? ParentAttendanceActivity)?.hideProgressBar()
        }
    }
}