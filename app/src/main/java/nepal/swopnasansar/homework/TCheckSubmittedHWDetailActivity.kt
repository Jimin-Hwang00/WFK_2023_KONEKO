package nepal.swopnasansar.homework

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nepal.swopnasansar.databinding.ActivityTCheckSubmittedHwDetailBinding
import nepal.swopnasansar.dao.HomeworkDAO
import nepal.swopnasansar.dto.TSubmitItem

class TCheckSubmittedHWDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTCheckSubmittedHwDetailBinding

    val homeworkDao = HomeworkDAO()
    var submittedStatus: TSubmitItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTCheckSubmittedHwDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            submittedStatus = intent.getSerializableExtra("submittedStatus", TSubmitItem::class.java)
        } else {
            submittedStatus = intent.getSerializableExtra("submittedStatus") as TSubmitItem
        }

        if (submittedStatus != null) {
            binding.tvCheckHwDetailClass.text = submittedStatus!!.className
            binding.tvCheckHwDetailSubject.text = submittedStatus!!.subjectName
            binding.tvCheckHwDetailStnName.text = submittedStatus!!.stnName
        } else {
            Toast.makeText(applicationContext, "Fail to get homework. Try again.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onResume() {
        binding.pbTCheckSubmittedHwDetail.visibility = View.VISIBLE

        lifecycleScope.launch {
            if (submittedStatus != null) {
                val homework = withContext(Dispatchers.IO) {
                    homeworkDao.getHWbyHWKey(submittedStatus!!.homeworkKey)
                }

                if (homework != null) {
                    if (!homework.homework_key.equals("No Document")) {
                        withContext(Main) {
                            binding.tvCheckHwDetailTitle.text = homework.submitted_hw[submittedStatus!!.idx!!].title
                            binding.tvCheckHwDetailContent.text = homework.submitted_hw[submittedStatus!!.idx!!].content

                            binding.pbTCheckSubmittedHwDetail.visibility = View.INVISIBLE
                        }
                    } else {
                        withContext(Main) {
                            Toast.makeText(applicationContext, "This homework has been deleted.", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                } else {
                    withContext(Main) {
                        Toast.makeText(applicationContext, "Fail to get homework. Try again.", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            } else {
                withContext(Main) {
                    Toast.makeText(applicationContext, "Fail to get homework. Try again.", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
        super.onResume()
    }
}