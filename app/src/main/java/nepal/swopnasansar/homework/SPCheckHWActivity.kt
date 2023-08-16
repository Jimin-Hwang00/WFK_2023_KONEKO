package nepal.swopnasansar.homework

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nepal.swopnasansar.databinding.ActivitySpCheckHwBinding
import nepal.swopnasansar.dao.AuthDAO
import nepal.swopnasansar.dao.ClassDAO
import nepal.swopnasansar.dao.HomeworkDAO
import nepal.swopnasansar.dto.Homework
import nepal.swopnasansar.dto.SPHWSubmitItem

class SPCheckHWActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySpCheckHwBinding

    lateinit var adapter: SPSubmittedHWAdapter

    val authDao = AuthDAO()
    val classDao = ClassDAO()
    val homeworkDao = HomeworkDAO()

    val uid = authDao.getUid()

    var homeworks: ArrayList<Homework>? = ArrayList()
    var submittedStatusList = ArrayList<SPHWSubmitItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpCheckHwBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (uid == null) {
            Toast.makeText(applicationContext, "You have to login.", Toast.LENGTH_SHORT).show()
            // @TODO Login 화면으로 이동
        }

        initRecycler()

        adapter.setOnItemClickListener(object: SPSubmittedHWAdapter.OnItemClickListener{
            override fun onItemClick(position: Int) {
                adapter.selectedIdx = position
                adapter.notifyDataSetChanged()

                // change button text depending on submit status
                if (submittedStatusList[position].idx != null) {
                    binding.btnSubmitHw.text = "edit"
                } else {
                    binding.btnSubmitHw.text = "submit"
                }

                val homeworkIdx =
                    homeworks!!.indexOfFirst { it.homework_key == submittedStatusList[position].homeworkKey }

                binding.tvCheckHwTitle.text = homeworks!![homeworkIdx].title
                binding.tvCheckHwContent.text = homeworks!![homeworkIdx].content

                if (!homeworks!![homeworkIdx].image.isEmpty()) {
                    Glide.with(this@SPCheckHWActivity)
                        .load(homeworks!![homeworkIdx].image)
                        .into(binding.ivCheckHwImage)
                }

                binding.svSpCheck.visibility = View.VISIBLE
                binding.btnSubmitHw.visibility = View.VISIBLE
            }

        })

        binding.btnSubmitHw.setOnClickListener {
            val intent = Intent(this@SPCheckHWActivity, SPSubmitHWActivity::class.java)
            intent.putExtra("submittedStatus", submittedStatusList[adapter.selectedIdx])
            startActivity(intent)
        }
    }

    override fun onResume() {
        binding.pbActivitySpCheckHw.visibility = View.VISIBLE

        lifecycleScope.launch {
            val classItem = withContext(Dispatchers.IO) {
                classDao.getClassByStudentKey(uid!!)
            }

            if (classItem != null) {
                homeworks = withContext(Dispatchers.IO) {
                    homeworkDao.getHWbyClassKey(classItem[0].class_key)
                }

                if (homeworks != null) {
                    submittedStatusList.clear()

                    homeworks!!.forEach { homework ->
                        val submittedStatus = SPHWSubmitItem(homework.homework_key, homework.subject_name, homework.title, homework.date, null)
                        homework.submitted_hw.forEachIndexed { index, submittedHW ->
                            if (submittedHW.stn_key == uid) {
                                submittedStatus.idx = index
                            }
                        }

                        submittedStatusList.add(submittedStatus)
                    }

                    withContext(Main) {
                        submittedStatusList.sortByDescending { it.date }
                        adapter.notifyDataSetChanged()
                        binding.pbActivitySpCheckHw.visibility = View.INVISIBLE
                    }
                } else {
                    withContext(Main) {
                        Toast.makeText(applicationContext, "Fail to get homework info. Try again.", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            } else {
                withContext(Main) {
                    Toast.makeText(applicationContext, "Fail to get class info. Try again.", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

        super.onResume()
    }

    fun initRecycler() {
        if (submittedStatusList == null) {
            adapter = SPSubmittedHWAdapter(ArrayList())
        } else {
            adapter = SPSubmittedHWAdapter(submittedStatusList!!)
        }

        binding.rvSubmitHwList.adapter = adapter
        binding.rvSubmitHwList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }
}