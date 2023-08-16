package nepal.swopnasansar.attendance

import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
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
import nepal.swopnasansar.data.AttendanceDto
import nepal.swopnasansar.data.ClassDto
import nepal.swopnasansar.data.RvTeacherAttDto
import nepal.swopnasansar.data.StudentDto
import nepal.swopnasansar.databinding.ListTeacherAttBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TeacherAttendanceAdapter (val rvCheckNoticeList : ArrayList<RvTeacherAttDto>, val attKey : String)
    : RecyclerView.Adapter<TeacherAttendanceAdapter.TeacherAttViewHolder>() {
    val TAG = "TeacherAttendanceAdapter"
    var firestore: FirebaseFirestore? = null
    var attTemp = ArrayList<AttendanceDto>() // 빈 ArrayList로 초기화
    var classTempList = ArrayList<ClassDto>() // 빈 ArrayList로 초기화
    var studentTempList = ArrayList<StudentDto>()
    var selectedPosition = -1
    val uid = "test_key"

    init {
        if (attKey.isEmpty()) {
            Log.d(TAG, "attKey is empty")
            CoroutineScope(Dispatchers.IO).launch {
                firestore = FirebaseFirestore.getInstance()

                classTempList.clear()
                val classQuerySnapshot = firestore?.collection("class")?.get()?.await()
                classTempList.addAll(classQuerySnapshot?.toObjects(ClassDto::class.java) ?: emptyList())

                //아이디 값과 해당 클래스의 teacher_key가 일치하면 학생 리스트 출력
                if(uid.equals("test_key")) {
                    //classTempList에서 담당 teacher_key 일치하는 부분의 데이터만 담기
                    for( c in classTempList){
                        //현재 로그인 된 선생 key 일치하는 것만
                        if(c.teacher_key.equals("qxLHhh9StYOfogNqLN9G")){
                            var todayDate = getTodayDate()

                            studentTempList.clear()
                            for(stn_key in c.student_key){
                                val studentQuerySnapshot = firestore?.collection("student")?.whereEqualTo("stn_key", stn_key)?.get()?.await()
                                studentTempList.addAll(studentQuerySnapshot?.toObjects(StudentDto::class.java) ?: emptyList())
                            }

                            // 데이터 처리 및 어댑터 갱신
                            withContext(Dispatchers.Main) {
                                rvCheckNoticeList.clear()

                                for(stn in studentTempList){
                                    rvCheckNoticeList.add(
                                        RvTeacherAttDto(c.class_name, todayDate, stn.stn_name, stn.stn_key,  "O")
                                    )
                                    Log.d(TAG, "${rvCheckNoticeList.size}")
                                }
                                notifyDataSetChanged()
                            }
                        }
                    }
                }
            }
        }else{
            CoroutineScope(Dispatchers.IO).launch {
                firestore = FirebaseFirestore.getInstance()

                classTempList.clear()
                val classQuerySnapshot = firestore?.collection("class")?.get()?.await()
                classTempList.addAll(
                    classQuerySnapshot?.toObjects(ClassDto::class.java) ?: emptyList()
                )

                //아이디 값만 확인후, 이미 존재하는 키값을 활용해 출석 db에서 가져오기
                if (uid.equals("test_key")) {
                    for (c in classTempList) {
                        if (c.teacher_key.equals("qxLHhh9StYOfogNqLN9G")) {
                            attTemp.clear()
                            val attQuerySnapshot = firestore?.collection("attendance")
                                ?.whereEqualTo("attendance_key", attKey)?.get()?.await()
                            attTemp.addAll(
                                attQuerySnapshot?.toObjects(AttendanceDto::class.java)
                                    ?: emptyList()
                            )

                            // 데이터 처리 및 어댑터 갱신
                            withContext(Dispatchers.Main) {
                                var check: String
                                rvCheckNoticeList.clear()

                                for (stn in attTemp[0].stn_list) {
                                    if (stn.check) {
                                        check = "O"
                                    } else {
                                        check = "X"
                                    }
                                    rvCheckNoticeList.add(
                                        RvTeacherAttDto(
                                            c.class_name,
                                            attTemp[0].date,
                                            stn.stn_name,
                                            stn.stn_key,
                                            check
                                        )
                                    )
                                }
                                notifyDataSetChanged()
                            }
                        }
                    }
                }
            }
        }
    }
    fun getTodayDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = Date()
        return dateFormat.format(today)
    }

    fun onUpdateList() {
        if (attKey.isEmpty()) {
            Log.d(TAG, "attKey is empty")
            CoroutineScope(Dispatchers.IO).launch {
                firestore = FirebaseFirestore.getInstance()

                classTempList.clear()
                val classQuerySnapshot = firestore?.collection("class")?.get()?.await()
                classTempList.addAll(classQuerySnapshot?.toObjects(ClassDto::class.java) ?: emptyList())

                //아이디 값과 해당 클래스의 teacher_key가 일치하면 학생 리스트 출력
                if(uid.equals("test_key")) {
                    //classTempList에서 담당 teacher_key 일치하는 부분의 데이터만 담기
                    for( c in classTempList){
                        //현재 로그인 된 선생 key 일치하는 것만
                        if(c.teacher_key.equals("qxLHhh9StYOfogNqLN9G")){
                            var todayDate = getTodayDate()

                            studentTempList.clear()
                            for(stn_key in c.student_key){
                                val studentQuerySnapshot = firestore?.collection("student")?.whereEqualTo("stn_key", stn_key)?.get()?.await()
                                studentTempList.addAll(studentQuerySnapshot?.toObjects(StudentDto::class.java) ?: emptyList())
                            }

                            // 데이터 처리 및 어댑터 갱신
                            withContext(Dispatchers.Main) {
                                rvCheckNoticeList.clear()

                                for(stn in studentTempList){
                                    rvCheckNoticeList.add(
                                        RvTeacherAttDto(c.class_name, todayDate, stn.stn_name, stn.stn_key,  "O")
                                    )
                                    Log.d(TAG, "${rvCheckNoticeList.size}")
                                }
                                notifyDataSetChanged()
                            }
                        }
                    }
                }
            }
        }else{
            CoroutineScope(Dispatchers.IO).launch {
                firestore = FirebaseFirestore.getInstance()

                classTempList.clear()
                val classQuerySnapshot = firestore?.collection("class")?.get()?.await()
                classTempList.addAll(
                    classQuerySnapshot?.toObjects(ClassDto::class.java) ?: emptyList()
                )

                //아이디 값만 확인후, 이미 존재하는 키값을 활용해 출석 db에서 가져오기
                if (uid.equals("test_key")) {
                    for (c in classTempList) {
                        if (c.teacher_key.equals("qxLHhh9StYOfogNqLN9G")) {
                            attTemp.clear()
                            val attQuerySnapshot = firestore?.collection("attendance")
                                ?.whereEqualTo("attendance_key", attKey)?.get()?.await()
                            attTemp.addAll(
                                attQuerySnapshot?.toObjects(AttendanceDto::class.java)
                                    ?: emptyList()
                            )

                            // 데이터 처리 및 어댑터 갱신
                            withContext(Dispatchers.Main) {
                                var check: String
                                rvCheckNoticeList.clear()

                                for (stn in attTemp[0].stn_list) {
                                    if (stn.check) {
                                        check = "O"
                                    } else {
                                        check = "X"
                                    }
                                    rvCheckNoticeList.add(
                                        RvTeacherAttDto(
                                            c.class_name,
                                            attTemp[0].date,
                                            stn.stn_name,
                                            stn.stn_key,
                                            check
                                        )
                                    )
                                }
                                notifyDataSetChanged()
                            }
                        }
                    }
                }
            }
        }
    }

    class TeacherAttViewHolder(
        val itemBinding: ListTeacherAttBinding,
        listener: TeacherAttendanceAdapter.OnItemClickListener?
    ) : RecyclerView.ViewHolder(itemBinding.root) {
        val className = itemBinding.classNameTv
        val date = itemBinding.dateTv
        val stnName = itemBinding.stnNameTv
        val checkButton = itemBinding.attCheckBt
        init {
            itemBinding.attCheckBt.setOnClickListener {
                listener?.onClick(it, adapterPosition)
                true
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TeacherAttendanceAdapter.TeacherAttViewHolder {
        val itemBinding =
            ListTeacherAttBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return TeacherAttendanceAdapter.TeacherAttViewHolder(itemBinding, listener)
    }

    override fun getItemCount(): Int {
        return rvCheckNoticeList.size
    }

    override fun onBindViewHolder(
        holder: TeacherAttendanceAdapter.TeacherAttViewHolder,
        position: Int
    ) {
        holder.className.text = rvCheckNoticeList[position].class_name
        holder.date.text = rvCheckNoticeList[position].date
        holder.stnName.text = rvCheckNoticeList[position].stn_name
        holder.checkButton.text = rvCheckNoticeList[position].check

        if (holder.checkButton.text == "X") {
            holder.checkButton.backgroundTintList = ColorStateList.valueOf(Color.RED) // 배경색 변경
        }


        holder.checkButton.setOnClickListener {
            if (holder.checkButton.text == "O") {
                holder.checkButton.setText("X")
                holder.checkButton.backgroundTintList = ColorStateList.valueOf(Color.RED) // 배경색 변경
                rvCheckNoticeList[position].check = "X"
            } else if (holder.checkButton.text == "X") {
                holder.checkButton.setText("O")
                holder.checkButton.backgroundTintList = ColorStateList.valueOf(Color.BLUE) // 배경색 변경
                rvCheckNoticeList[position].check = "O"
            }
            Log.d(TAG, "${rvCheckNoticeList[position].check}")
        }

        if (position == itemCount - 1) {
            (holder.itemView.context as? TeacherAttendanceActivity)?.hideProgressBar()
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