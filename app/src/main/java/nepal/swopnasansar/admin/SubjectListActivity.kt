package nepal.swopnasansar.admin

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nepal.swopnasansar.dao.ClassDAO
import nepal.swopnasansar.dao.SubjectDAO
import nepal.swopnasansar.dao.TeacherDAO
import nepal.swopnasansar.databinding.ActivitySubjectListBinding
import nepal.swopnasansar.dto.SubjectListItem

class SubjectListActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySubjectListBinding

    private var subjectItems: ArrayList<SubjectListItem>? = ArrayList()
    private var subjectAdapter: SubjectAdapter? = null

    private val subjectDao = SubjectDAO()
    private val teacherDao = TeacherDAO()
    private val classDao = ClassDAO()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubjectListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecycler()

        binding.btnEditSubject.setOnClickListener {
            val intent = Intent(this, CreateSubjectActivity::class.java)
            startActivity(intent)
        }

        subjectAdapter?.setOnItemLongClickListener(object: SubjectAdapter.OnItemLongClickListener {
            override fun onLongItemClick(position: Int, subjectListItem: SubjectListItem) {
                askForDeletingSubject(subjectListItem.subjectKey)
            }
        })
    }

    override fun onResume() {
        getAllSubjectItemLists()

        super.onResume()
    }

    private fun getAllSubjectItemLists() {
        binding.pbSubjectList.visibility = View.VISIBLE

        if (subjectItems != null) {
            subjectItems!!.clear()
        }

        lifecycleScope.launch {
            val subjects = withContext(Dispatchers.IO) {
                subjectDao.getAllSubejct()
            }

            if (subjects != null) {
                subjects.forEach { subject ->
                    val subjectItem = SubjectListItem("", "", subject.subject_name, subject.subject_key)

                    val classItem  = withContext(Dispatchers.IO) {
                        classDao.getClassByClassKey(subject.class_key)
                    }

                    if (classItem != null) {
                        subjectItem.className = classItem.class_name
                    } else {
                        Toast.makeText(this@SubjectListActivity, "Fail to get classes. Try again.", Toast.LENGTH_SHORT).show()
                        binding.pbSubjectList.visibility = View.INVISIBLE
                        return@launch
                    }

                    val teacher = withContext(Dispatchers.IO) {
                        teacherDao.getTeacherByKey(subject.teacher_key)
                    }

                    if (teacher != null) {
                        subjectItem.teacherName = teacher.teacher_name
                    } else {
                        Toast.makeText(this@SubjectListActivity, "Fail to get teachers. Try again.", Toast.LENGTH_SHORT).show()
                        binding.pbSubjectList.visibility = View.INVISIBLE
                        return@launch
                    }

                    subjectItems?.add(subjectItem)


                }
                subjectAdapter?.itemList = subjectItems!!
                subjectAdapter?.notifyDataSetChanged()

                binding.pbSubjectList.visibility = View.INVISIBLE
            } else {
                Toast.makeText(this@SubjectListActivity, "Fail to get subjects. Try again.", Toast.LENGTH_SHORT).show()
                binding.pbSubjectList.visibility = View.INVISIBLE
            }
        }
    }

    private fun askForDeletingSubject(subjectKey: String) {
        AlertDialog.Builder(this@SubjectListActivity).run {
            setTitle("Delete Subject")
            setMessage("Delete it?")
            setNegativeButton("NO", null)
            setCancelable(false)
            setPositiveButton("YES", object: DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    lifecycleScope.launch {
                        binding.pbSubjectList.visibility = View.VISIBLE

                        val deleteResult = withContext(Dispatchers.IO) {
                            subjectDao.removeSubject(subjectKey)
                        }

                        if (deleteResult) {
                            Toast.makeText(this@SubjectListActivity, "delete success!!", Toast.LENGTH_SHORT).show()
                            binding.pbSubjectList.visibility = View.INVISIBLE

                            getAllSubjectItemLists()
                        } else {
                            Toast.makeText(this@SubjectListActivity, "Failed to delete subject. Try again.", Toast.LENGTH_SHORT).show()
                            binding.pbSubjectList.visibility = View.INVISIBLE
                        }
                    }
                }
            })
            show()
        }
    }

    private fun initRecycler() {
        if (subjectItems != null) {
            subjectAdapter = SubjectAdapter(subjectItems!!)
        } else {
            subjectAdapter = SubjectAdapter(ArrayList())
        }

        binding.rvSubjectList.adapter = subjectAdapter
        binding.rvSubjectList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }
}