package nepal.swopnasansar.accountant

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
import nepal.swopnasansar.R
import nepal.swopnasansar.dao.StudentDAO
import nepal.swopnasansar.dto.Student
import nepal.swopnasansar.databinding.ActivityAccountantTuitionCheckBinding

class AccountantTuitionCheckActivity : AppCompatActivity() {
    private val TAG = "AccoutantTuitionCheckActivity"

    private lateinit var binding: ActivityAccountantTuitionCheckBinding

    private var students: ArrayList<Student>? = ArrayList()

    var studentDao = StudentDAO()

    private lateinit var inputTuitionListAdapter: CheckTuitionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountantTuitionCheckBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnEditTuition.setOnClickListener {
            val intent = Intent(this, AccountantTuitionInputActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

        initRecycler()
    }

    override fun onResume() {
        lifecycleScope.launch() {
            binding.pbAccountantTuitionCheck.visibility = View.VISIBLE

            val result = withContext(Dispatchers.IO) {
                studentDao.getAllStudents()
            }

            withContext(Main) {
                if (students == null) {
                    Toast.makeText(this@AccountantTuitionCheckActivity, "Fail to get students info. Try again.", Toast.LENGTH_SHORT).show()
                } else {
                    result?.sortBy { it.stn_name }

                    students = result
                    inputTuitionListAdapter.students = students
                    inputTuitionListAdapter.notifyDataSetChanged()
                }

                binding.pbAccountantTuitionCheck.visibility = View.INVISIBLE
            }
        }

        super.onResume()
    }

    fun initRecycler() {
        if (students == null) {
            inputTuitionListAdapter = CheckTuitionAdapter(ArrayList())
        } else {
            inputTuitionListAdapter = CheckTuitionAdapter(students!!)
        }

        binding.rvCheckTuitionList.adapter = inputTuitionListAdapter
        binding.rvCheckTuitionList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        inputTuitionListAdapter.notifyDataSetChanged()
    }
}