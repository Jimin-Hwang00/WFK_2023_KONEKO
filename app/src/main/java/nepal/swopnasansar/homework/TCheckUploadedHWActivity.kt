package nepal.swopnasansar.homework

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nepal.swopnasansar.dao.AuthDAO
import nepal.swopnasansar.dao.HomeworkDAO
import nepal.swopnasansar.databinding.ActivityTCheckUploadedHwBinding
import nepal.swopnasansar.dto.Homework
import nepal.swopnasansar.login.CheckRoleActivity

class TCheckUploadedHWActivity : AppCompatActivity() {
    private val tag = "TCheckUploadedHWActivity"
    private lateinit var binding: ActivityTCheckUploadedHwBinding

    val authDao = AuthDAO()
    val homeworkDao = HomeworkDAO()

    var homeworks: ArrayList<Homework>? = ArrayList()

    private lateinit var uploadedHWAdapter: THWAdapter

    val uid = authDao.getUid()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTCheckUploadedHwBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (uid == null) {
            Toast.makeText(applicationContext, "You have to login.", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, CheckRoleActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

        initRecycler()

        uploadedHWAdapter.setOnItemClickListener(object: THWAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                uploadedHWAdapter.selectedIdx = position
                uploadedHWAdapter.notifyDataSetChanged()

                binding.svUploadedHw.visibility = View.VISIBLE
                binding.btnDeleteUploadedHw.visibility = View.VISIBLE

                binding.tvTUploadedHwTitle.text = homeworks!![position].title
                binding.tvTUploadedHwContent.text = homeworks!![position].content

                if (!uploadedHWAdapter.itemList[position].image.isNullOrBlank()) {
                    Glide.with(this@TCheckUploadedHWActivity)
                        .load(uploadedHWAdapter.itemList[position].image)
                        .into(binding.ivUploadedHw)

                    binding.ivUploadedHw.visibility = View.VISIBLE
                } else {
                    binding.ivUploadedHw.visibility = View.GONE
                }
            }
        })

        binding.btnDeleteUploadedHw.setOnClickListener {
            binding.pbTCheckUploadedHw.visibility = View.VISIBLE
            getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

            lifecycleScope.launch {
                Log.d(tag, "delete : ${homeworks!![uploadedHWAdapter.selectedIdx]}")
                    val result = withContext(Dispatchers.IO) {
                        homeworkDao.removeHWByHWKey(homeworks!![uploadedHWAdapter.selectedIdx])
                    }

                    if (result) {
                        withContext(Main) {
                            binding.svUploadedHw.visibility = View.INVISIBLE
                            binding.btnDeleteUploadedHw.visibility = View.INVISIBLE
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                            uploadedHWAdapter.selectedIdx = -1

                            getUploadedHW()
                        }
                    } else {
                        withContext(Main) {
                            Toast.makeText(this@TCheckUploadedHWActivity, "Fail to delete homework. Try again.", Toast.LENGTH_SHORT).show()
                            binding.pbTCheckUploadedHw.visibility = View.INVISIBLE
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        }
                    }
                }
            }
    }

    override fun onResume() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        getUploadedHW()
        super.onResume()
    }

    fun getUploadedHW() {
        binding.pbTCheckUploadedHw.visibility = View.VISIBLE

        lifecycleScope.launch {
            homeworks = withContext(Dispatchers.IO) {
                homeworkDao.getHWbyTeacherKey(uid!!)
            }

            if (homeworks != null) {
                withContext(Main) {
                    homeworks?.sortByDescending { it.date }
                    homeworks?.let { uploadedHWAdapter.updateData(it) }
                    binding.pbTCheckUploadedHw.visibility = View.INVISIBLE
                }
            } else {
                withContext(Main) {
                    Toast.makeText(applicationContext, "Fail to get homework. Try again.", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    fun initRecycler() {
        if (homeworks == null) {
            uploadedHWAdapter = THWAdapter(ArrayList())
        } else {
            uploadedHWAdapter = THWAdapter(homeworks!!)
        }

        binding.rvTUploadedHw.adapter = uploadedHWAdapter
        binding.rvTUploadedHw.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }
}