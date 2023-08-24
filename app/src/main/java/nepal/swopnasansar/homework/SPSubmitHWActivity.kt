package nepal.swopnasansar.homework

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nepal.swopnasansar.databinding.ActivitySpSubmitHwBinding
import nepal.swopnasansar.dao.AuthDAO
import nepal.swopnasansar.dao.HomeworkDAO
import nepal.swopnasansar.dto.SPHWSubmitItem
import nepal.swopnasansar.dto.SubmittedHW
import nepal.swopnasansar.login.CheckRoleActivity

class SPSubmitHWActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySpSubmitHwBinding

    val authDao = AuthDAO()
    val homeworkDao = HomeworkDAO()

    val uid = authDao.getUid()

    var submittedStatus: SPHWSubmitItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpSubmitHwBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("SPSubmitHWActivity", "onCreate")

        if (uid == null) {
            Toast.makeText(applicationContext, "You have to login.", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, CheckRoleActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            submittedStatus = intent.getSerializableExtra("submittedStatus", SPHWSubmitItem::class.java)
        } else {
            submittedStatus = intent.getSerializableExtra("submittedStatus") as SPHWSubmitItem
        }

        if (submittedStatus == null) {
            Toast.makeText(applicationContext, "Fail to get homework info. Try again.", Toast.LENGTH_SHORT).show()
            finish()
        } else if (submittedStatus!!.idx != null) {
            lifecycleScope.launch {
                val homework = withContext(Dispatchers.IO) {
                    homeworkDao.getHWbyHWKey(submittedStatus!!.homeworkKey)
                }

                if (homework != null) {
                    binding.evUploadHwTitleSp.setText(homework!!.submitted_hw[submittedStatus!!.idx!!].title)
                    binding.evUploadHwContentSp.setText(homework!!.submitted_hw[submittedStatus!!.idx!!].content)
                } else if (homework?.homework_key == "No Document") {
                    withContext(Main) {
                        Toast.makeText(applicationContext, "This homework has been deleted.", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } else {
                    withContext(Main) {
                        Toast.makeText(applicationContext, "Fail to get homework info. Try again.", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
        }

        binding.btnSubmitHwSp.setOnClickListener {
            if (binding.evUploadHwTitleSp.text.toString().isBlank()) {
                Toast.makeText(this@SPSubmitHWActivity, "Please write down the title.", Toast.LENGTH_SHORT).show()
            } else {
                if (submittedStatus!!.idx == null) {
                    submitHomework()
                } else {
                    editHomework()
                }
            }
        }
    }

    override fun onResume() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        super.onResume()
    }

    fun submitHomework() {
        binding.pbSpSubmitHw.visibility = View.VISIBLE
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

        val title = binding.evUploadHwTitleSp.text.toString()
        val content = binding.evUploadHwContentSp.text.toString()
        val submittedHW = SubmittedHW(submittedStatus!!.homeworkKey, uid!!, title, content)

        lifecycleScope.launch {
            val homework = withContext(Dispatchers.IO) {
                homeworkDao.getHWbyHWKey(submittedStatus!!.homeworkKey)
            }

            if (homework != null) {
                if (homework.homework_key.equals("No Document")) {
                    withContext(Main) {
                        Toast.makeText(applicationContext, "This homework has been deleted.", Toast.LENGTH_SHORT).show()
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        finish()
                    }
                } else {
                    val result = withContext(Dispatchers.IO) {
                        homeworkDao.submitHW(submittedStatus!!.homeworkKey, submittedHW)
                    }

                    if (result) {
                        withContext(Main) {
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            binding.pbSpSubmitHw.visibility = View.INVISIBLE
                            Toast.makeText(applicationContext, "Upload Success", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    } else {
                        withContext(Main) {
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            binding.pbSpSubmitHw.visibility = View.INVISIBLE
                            Toast.makeText(this@SPSubmitHWActivity, "Fail to submit homework. Try again.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                withContext(Main) {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    binding.pbSpSubmitHw.visibility = View.INVISIBLE
                    Toast.makeText(applicationContext, "Fail to submit homework. Try again.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun editHomework() {
        binding.pbSpSubmitHw.visibility = View.VISIBLE
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

        val title = binding.evUploadHwTitleSp.text.toString()
        val content = binding.evUploadHwContentSp.text.toString()
        val submittedHW = SubmittedHW(submittedStatus!!.homeworkKey, uid!!, title, content)

        lifecycleScope.launch {
            val homework = withContext(Dispatchers.IO) {
                homeworkDao.getHWbyHWKey(submittedStatus!!.homeworkKey)
            }

            if (homework != null) {
                if (homework.homework_key.equals("No Document")) {
                    withContext(Main) {
                        Toast.makeText(applicationContext, "This homework has been deleted.", Toast.LENGTH_SHORT).show()
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        finish()
                    }
                } else {
                    homework.submitted_hw[submittedStatus!!.idx!!] = submittedHW
                    val result = withContext(Dispatchers.IO) {
                        homeworkDao.updateHW(submittedStatus!!.homeworkKey, mapOf("submitted_hw" to homework.submitted_hw))
                    }

                    if (result) {
                        withContext(Main) {
                            Toast.makeText(applicationContext, "Edit Success", Toast.LENGTH_SHORT).show()
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            binding.pbSpSubmitHw.visibility = View.INVISIBLE
                            finish()
                        }
                    } else {
                        withContext(Main) {
                            binding.pbSpSubmitHw.visibility = View.INVISIBLE
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            Toast.makeText(applicationContext, "Fail to submit homework. Try again.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                withContext(Main) {
                    binding.pbSpSubmitHw.visibility = View.INVISIBLE
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    Toast.makeText(applicationContext, "Fail to submit homework. Try again.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}