package nepal.swopnasansar.notice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import nepal.swopnasansar.R
import nepal.swopnasansar.databinding.ActivityTeacherNoticeMainBinding

class TeacherNoticeMainActivity : AppCompatActivity() {
    lateinit var binding : ActivityTeacherNoticeMainBinding
    val TAG = "TeacheNoticeMainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeacherNoticeMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.entireNoticeBt.setOnClickListener{
            val intent = Intent(this@TeacherNoticeMainActivity, TeacherEntireNoticeActivity::class.java)
            startActivity(intent)
        }

        binding.selectNoticeBt.setOnClickListener{
            val intent = Intent(this@TeacherNoticeMainActivity, TeacherSelectNoticeActivity::class.java)
            startActivity(intent)
        }

        binding.checkNoticeBt.setOnClickListener{
            val intent = Intent(this@TeacherNoticeMainActivity, TeacherCheckNoticeActivity::class.java)
            startActivity(intent)
        }
    }
}