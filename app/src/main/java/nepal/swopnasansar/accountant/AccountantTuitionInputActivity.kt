package nepal.swopnasansar.accountant

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nepal.swopnasansar.dao.StudentDAO
import nepal.swopnasansar.dto.Student
import nepal.swopnasansar.databinding.ActivityAccountantTuitionInputBinding

class AccountantTuitionInputActivity : AppCompatActivity() {
    private val TAG = "AccountantTuitionInputActivity"

    private lateinit var binding: ActivityAccountantTuitionInputBinding

    private var students: ArrayList<Student>? = ArrayList()
    var studentDao = StudentDAO()

    private lateinit var inputTuitionListAdapter: InputTuitionListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountantTuitionInputBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecycler()

        binding.btnTuitionSubmit.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                binding.pbAccountantTuitionInput.visibility = View.VISIBLE
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                for (idx in inputTuitionListAdapter.changedIdx) {
                    val result = withContext(Dispatchers.IO) {
                        studentDao.updateStudentByKey(students!![idx])
                    }

                    withContext(Main) {
                        Log.d(TAG, "idx: ${idx}, result : ${result}")
                        if (!result) {
                            Toast.makeText(this@AccountantTuitionInputActivity, "Fail to submit changes. Try again", Toast.LENGTH_SHORT).show()
                            binding.pbAccountantTuitionInput.visibility = View.VISIBLE
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        }
                    }
                }

                withContext(Main) {
                    val intent = Intent(this@AccountantTuitionInputActivity, AccountantTuitionCheckActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    override fun onResume() {
        lifecycleScope.launch() {
            binding.pbAccountantTuitionInput.visibility = View.VISIBLE

            students = withContext(Dispatchers.IO) {
                studentDao.getAllStudents()
            }

            withContext(Main) {
                binding.pbAccountantTuitionInput.visibility = View.INVISIBLE

                inputTuitionListAdapter.students = students
                inputTuitionListAdapter.notifyDataSetChanged()
            }
        }
        super.onResume()
    }

    fun initRecycler() {
        if (students == null) {
            inputTuitionListAdapter = InputTuitionListAdapter(ArrayList())
        } else {
            inputTuitionListAdapter = InputTuitionListAdapter(students!!)
        }

        binding.rvInputTuition.adapter = inputTuitionListAdapter
        binding.rvInputTuition.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        inputTuitionListAdapter.notifyDataSetChanged()
    }
}