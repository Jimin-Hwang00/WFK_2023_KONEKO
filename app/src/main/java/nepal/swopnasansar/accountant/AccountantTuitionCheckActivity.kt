package nepal.swopnasansar.accountant

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
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
            Log.d(TAG, "btnEditTuition 클릭 리스너 구현")
            val intent = Intent(this, AccountantTuitionInputActivity::class.java)
            startActivity(intent)
        }

        initRecycler()

        binding.progressBar.visibility = View.VISIBLE
    }

    override fun onResume() {
        lifecycleScope.launch(Dispatchers.IO) {
            students = withContext(Dispatchers.IO) {
                studentDao.getAllStudent()
            }

            withContext(Main) {
                binding.progressBar.visibility = View.INVISIBLE

                inputTuitionListAdapter.students = students
                inputTuitionListAdapter.notifyDataSetChanged()
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