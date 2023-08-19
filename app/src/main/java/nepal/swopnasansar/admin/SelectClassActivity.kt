package nepal.swopnasansar.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nepal.swopnasansar.dao.ClassDAO
import nepal.swopnasansar.dto.Class
import nepal.swopnasansar.databinding.ActivitySelectClassBinding

class SelectClassActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectClassBinding

    private val classDao = ClassDAO()

    private var selectedClassAdapter: SelectedClassAdapter? = null
    private var classes: ArrayList<Class>? = ArrayList()
    private var selectedClass: Class? = null

    private val REQ_CLASS = 10000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectClassBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecycler()

        binding.btnSelectClassComplete.setOnClickListener {
            if (selectedClassAdapter?.mSelectedItem != null) {
                val intent = Intent(this@SelectClassActivity, CreateSubjectActivity::class.java)
                intent.putExtra("class_key", selectedClass?.class_key)
                intent.putExtra("class_name", selectedClass?.class_name)
                setResult(RESULT_OK, intent)
                finish()
            } else {
                Toast.makeText(this@SelectClassActivity, "Please select class.", Toast.LENGTH_SHORT).show()
            }
        }

        selectedClassAdapter?.setItemClickListener(object: SelectedClassAdapter.ItemClickListener {
            override fun onClick(view: View, classItem: Class) {
                selectedClass = classItem
            }
        })
    }

    override fun onResume() {
        lifecycleScope.launch {
            binding.pbSelectClass.visibility = View.VISIBLE

            val classes = withContext(Dispatchers.IO) {
                classDao.getAllClasses()
            }

            if (classes != null) {
                selectedClassAdapter?.items = classes
                selectedClassAdapter?.notifyDataSetChanged()
                binding.pbSelectClass.visibility = View.INVISIBLE
            } else {
                Toast.makeText(this@SelectClassActivity, "Fail to get classes. Try again.", Toast.LENGTH_SHORT).show()
                binding.pbSelectClass.visibility = View.INVISIBLE
            }
        }
        super.onResume()
    }

    private fun initRecycler() {
        selectedClassAdapter = SelectedClassAdapter()

        if (classes != null) {
            selectedClassAdapter?.setItem(classes!!)
        } else {
            selectedClassAdapter?.setItem(ArrayList())
        }

        binding.rvClassList.adapter = selectedClassAdapter
        binding.rvClassList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }
}