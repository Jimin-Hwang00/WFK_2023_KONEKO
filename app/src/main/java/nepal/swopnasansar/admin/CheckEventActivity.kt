package nepal.swopnasansar.admin

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import nepal.swopnasansar.R
import nepal.swopnasansar.data.AdminCalDao
import nepal.swopnasansar.data.AdminCalDto
import nepal.swopnasansar.data.ClassDto
import nepal.swopnasansar.data.RvClassListDto
import nepal.swopnasansar.data.SubjectDto
import nepal.swopnasansar.data.TeacherDto
import nepal.swopnasansar.databinding.ActivityCheckEventBinding
import java.util.Calendar
import java.util.Collections
import java.util.Date
import java.util.GregorianCalendar
import java.util.Locale

class CheckEventActivity : AppCompatActivity() {
    lateinit var binding :ActivityCheckEventBinding
    lateinit var adapter : AdminCalAdapter
    var progressBarVisible = true
    val TAG = "CheckEventActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = Firebase.firestore
        val adminCalList = ArrayList<AdminCalDto>()

        binding.calendarView.setSelectedDate(CalendarDay.today())
        binding.calendarView.addDecorators(TodayDecorator(), SaturdayDecorator())

        adapter = AdminCalAdapter(adminCalList, this)
        binding.rvEvent.adapter = adapter

        CoroutineScope(Dispatchers.IO).launch {
            delay(2000)
            // 데이터 로딩이 완료되었을 때의 로직
            withContext(Dispatchers.Main) {
                val tempList = onMonthChanged(
                    binding.calendarView.currentDate.year.toString(),
                    binding.calendarView.currentDate.month.toString(),
                    adminCalList
                )

                binding.rvEvent.layoutManager = LinearLayoutManager(this@CheckEventActivity).apply {
                    orientation = LinearLayoutManager.VERTICAL
                }
                binding.rvEvent.adapter = adapter

                adapter.updateList(tempList)
            }
        }

        binding.calendarView.setOnMonthChangedListener { widget, date ->
            val TempList = onMonthChanged(date.year.toString(), date.month.toString(), adminCalList)
            adapter.updateList(TempList)
        }

        binding.addBt.setOnClickListener {
            val intent = Intent(this, CreateEventActivity::class.java)
            startActivity(intent) //액티비티 띄우
        }

        val onLongClickListener = object : AdminCalAdapter.OnItemLongClickListener {
            override fun onItemLongClick(view: View, position: Int) {
                val intent = Intent(this@CheckEventActivity, CheckEventActivity::class.java)
                AlertDialog.Builder(this@CheckEventActivity).run {
                    setTitle("Delete")
                    setMessage("Delete it?")
                    setNegativeButton("No", null)
                    setCancelable(false)
                    setPositiveButton("Yes", object : DialogInterface.OnClickListener {
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            db.collection("schedule").document(adapter.adminCalList[position].schedule_key)
                                .delete()
                                .addOnSuccessListener {
                                    adapter.adminCalList.removeAt(position)
                                    adapter.notifyDataSetChanged() // 어댑터에 데이터 변경 알림

                                    for(i in adapter.adminCalList){
                                        Log.d(TAG, "지워지고 나서.. ${i.event}, 현재 position : ${position}")
                                    }
                                    Toast.makeText(
                                        this@CheckEventActivity,
                                        "delete success!!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    startActivity(intent)
                                }
                                .addOnFailureListener { e ->
                                    // 삭제 중에 발생한 오류 처리
                                    Toast.makeText(
                                        this@CheckEventActivity,
                                        "delete failed: ${e.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                    })
                    show()
                }
            }
        }
        adapter.setOnItemLongClickListener(onLongClickListener)

    }
    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }
    fun onMonthChanged(year : String, month : String, adminCalList : ArrayList<AdminCalDto>) : ArrayList<AdminCalDto>{
        val pageYearMonth = "${year}-${month}"
        val TempList = ArrayList<AdminCalDto>()
        val calendarDays = mutableSetOf<CalendarDay>()

        for(event in adminCalList){
            val eventParts = event.date.split("-")
            if (eventParts.size >= 3) {
                val eventYear = eventParts[0].toInt()
                val eventMonth = eventParts[1].toInt()
                val eventDay = eventParts[2].toInt()
                calendarDays.add(CalendarDay.from(eventYear, eventMonth, eventDay))
            }
            val eventYearMonth = event.date.substringBeforeLast("-")
            if(eventYearMonth == pageYearMonth){
                binding.calendarView.addDecorator(EventDecorator(calendarDays))
                TempList.add(event)
            }
        }
        return TempList
    }

    // 뷰 홀더가 생성되어 화면에 표시된 후에 ProgressBar를 숨기는 메서드
    fun hideProgressBar() {
        if (progressBarVisible) {
            progressBarVisible = false
            binding.progressBar.visibility = View.INVISIBLE
        }
    }
    class TodayDecorator: DayViewDecorator {
        private var date = CalendarDay.today()

        override fun shouldDecorate(day: CalendarDay?): Boolean {
            return day?.equals(date)!!
        }

        override fun decorate(view: DayViewFacade?) {
            view?.addSpan(StyleSpan(Typeface.BOLD))
            view?.addSpan(RelativeSizeSpan(1.4f))
            view?.addSpan(ForegroundColorSpan(Color.parseColor("#3F51B5")))
        }
    }
    class EventDecorator(dates: Collection<CalendarDay>): DayViewDecorator {

        var dates: HashSet<CalendarDay> = HashSet(dates)

        override fun shouldDecorate(day: CalendarDay?): Boolean {
            return dates.contains(day)
        }

        override fun decorate(view: DayViewFacade?) {
            view?.addSpan(DotSpan(8F, Color.parseColor("#FF0000")))
        }
    }

    class SaturdayDecorator: DayViewDecorator {

        private val calendar = Calendar.getInstance()

        override fun shouldDecorate(day: CalendarDay?): Boolean {
            day?.copyTo(calendar)
            val saturday = calendar.get(Calendar.DAY_OF_WEEK)
            return saturday == Calendar.SATURDAY
        }
        override fun decorate(view: DayViewFacade?) {
            view?.addSpan(object: ForegroundColorSpan(Color.RED){})
        }
    }
}
