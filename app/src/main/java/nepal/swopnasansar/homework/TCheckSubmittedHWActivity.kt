package nepal.swopnasansar.homework

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nepal.swopnasansar.dao.AuthDAO
import nepal.swopnasansar.dao.HomeworkDAO
import nepal.swopnasansar.dao.SubmittedHWDAO
import nepal.swopnasansar.databinding.ActivityTCheckSubmittedHwBinding
import nepal.swopnasansar.dao.*
import nepal.swopnasansar.dto.Homework
import nepal.swopnasansar.dto.Class
import nepal.swopnasansar.dto.TSubmitItem

class TCheckSubmittedHWActivity : AppCompatActivity() {
    private val TAG = "TCheckSubmittedHWActivity"

    private lateinit var binding: ActivityTCheckSubmittedHwBinding

    val authDao = AuthDAO()
    val subjectDao = SubjectDAO()
    val classDao = ClassDAO()
    val homeworkDao = HomeworkDAO()
    val studentDao = StudentDAO()
    val submittedDao = SubmittedHWDAO()

    val uid = authDao.getUid()

    var homeworks: ArrayList<Homework>? = ArrayList()
    var submittedStatusList: ArrayList<TSubmitItem> = ArrayList()

    private lateinit var hwAdapter: THWAdapter
    private lateinit var submittedHWAdapter: THWSubmittedStatusAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTCheckSubmittedHwBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (uid == null) {

        }

        initHWRecycler()
        initSubmittedHWRecycler()

        getUploadedHW()

        hwAdapter.setOnItemClickListener(object:THWAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                hwAdapter.selectedIdx = position
                hwAdapter.notifyDataSetChanged()

                submittedHWAdapter.selectedIdx = -1

                getSubmittedStatus(homeworks!![position])
            }
        })

        submittedHWAdapter.setOnItemClickListener(object:THWSubmittedStatusAdapter.OnItemClickListener{
            override fun onItemClick(position: Int) {
                submittedHWAdapter.selectedIdx = position
                submittedHWAdapter.notifyDataSetChanged()

                if (submittedStatusList!![position].idx != null) {
                    val intent = Intent(
                        this@TCheckSubmittedHWActivity,
                        TCheckSubmittedHWDetailActivity::class.java
                    )
                    intent.putExtra("submittedStatus", submittedStatusList!![position])
                    startActivity(intent)
                }
            }
        })
    }

    fun getUploadedHW() {
        binding.pbTCheckSubmittedHw.visibility = View.VISIBLE

        lifecycleScope.launch {
            homeworks = withContext(Dispatchers.IO) {
                homeworkDao.getHWbyTeacherKey(uid!!)
            }

            if(homeworks != null) {
                withContext(Main) {
                    homeworks?.sortByDescending { it.date }
                    hwAdapter.updateData(homeworks!!)
                    binding.pbTCheckSubmittedHw.visibility = View.INVISIBLE
                }
            } else {
                withContext(Main) {
                    Toast.makeText(applicationContext, "Fail to get homeworks. Try again.", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    fun getSubmittedStatus(homework: Homework) {
        binding.pbTCheckSubmittedHw.visibility = View.VISIBLE

        lifecycleScope.launch {
            val classItem: Class = withContext(Dispatchers.IO){
                classDao.getClassByClassKey(homework.class_key)
            }

            if (classItem != null) {
                submittedStatusList.clear()

                classItem.student_key.forEach { stnKey ->
                    var student = withContext(Dispatchers.IO){
                        studentDao.getStudentByKey(stnKey)
                    }

                    if (student != null) {
                        val submittedStatus = TSubmitItem(homework.homework_key, homework.class_name, homework.subject_name, stnKey, student.stn_name, null)

                        homework.submitted_hw.forEachIndexed { idx, submittedHW ->
                            if (stnKey == submittedHW.stn_key) {
                                Log.d(TAG, "EQUAL!")
                                submittedStatus.idx = idx
                            }
                        }

                        submittedStatusList?.add(submittedStatus)
                    } else {
                        withContext(Main) {
                            Toast.makeText(applicationContext, "Fail to get submitted homework. Try again.", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                }

                withContext(Main) {
                    submittedStatusList.sortBy { it.stnName }
                    submittedHWAdapter.notifyDataSetChanged()
                    binding.pbTCheckSubmittedHw.visibility = View.INVISIBLE
                }
            } else {
                withContext(Main) {
                    Toast.makeText(applicationContext, "Fail to get class info. Try again.", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    fun initHWRecycler() {
        if (homeworks == null) {
            hwAdapter = THWAdapter(ArrayList())
        } else {
            hwAdapter = THWAdapter(homeworks!!)
        }

        binding.rvHwCheckList.adapter = hwAdapter
        binding.rvHwCheckList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    fun initSubmittedHWRecycler() {
        if (submittedStatusList == null) {
            submittedHWAdapter  = THWSubmittedStatusAdapter(ArrayList())
        } else {
            submittedHWAdapter = THWSubmittedStatusAdapter(submittedStatusList!!)
        }

        binding.rvHwCheckSubmitList.adapter = submittedHWAdapter
        binding.rvHwCheckSubmitList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }
}