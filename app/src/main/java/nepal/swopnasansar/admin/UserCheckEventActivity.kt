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
import nepal.swopnasansar.admin.data.AdminCalDao
import nepal.swopnasansar.admin.data.AdminCalDto
import nepal.swopnasansar.admin.data.ClassDto
import nepal.swopnasansar.admin.data.RvClassListDto
import nepal.swopnasansar.admin.data.SubjectDto
import nepal.swopnasansar.admin.data.TeacherDto
import nepal.swopnasansar.databinding.ActivityCheckEventBinding
import nepal.swopnasansar.databinding.ActivityUserCheckEventBinding
import java.util.Collections
import java.util.Date
import java.util.GregorianCalendar
import java.util.Locale

class UserCheckEventActivity : AppCompatActivity() {
    lateinit var binding :ActivityUserCheckEventBinding
    lateinit var adapter : AdminCalAdapter
    var progressBarVisible = true
    val TAG = "UserCheckEventActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserCheckEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = Firebase.firestore
        val adminCalList = ArrayList<AdminCalDto>()

        binding.calendarView.setSelectedDate(CalendarDay.today())
        binding.calendarView.addDecorator(TodayDecorator())

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

                binding.rvEvent.layoutManager = LinearLayoutManager(this@UserCheckEventActivity).apply {
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
        Log.d(TAG, "${TempList.size}")
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
}
