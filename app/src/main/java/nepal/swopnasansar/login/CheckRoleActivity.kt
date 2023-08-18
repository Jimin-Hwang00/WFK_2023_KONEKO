package nepal.swopnasansar.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import nepal.swopnasansar.R
import nepal.swopnasansar.databinding.ActivityCheckRoleBinding

class CheckRoleActivity: AppCompatActivity() {

    private lateinit var binding: ActivityCheckRoleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckRoleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = Intent(this@CheckRoleActivity, LoginActivity::class.java)

        binding.tvCheckRoleAdmin.setOnClickListener {
            intent.putExtra("role", getString(R.string.administrator))
            startActivity(intent)
        }

        binding.tvCheckRoleAccountant.setOnClickListener {
            intent.putExtra("role", getString(R.string.accountant))
            startActivity(intent)
        }

        binding.tvCheckRoleTeacher.setOnClickListener {
            intent.putExtra("role", getString(R.string.teacher))
            startActivity(intent)
        }

        binding.tvCheckRoleSp.setOnClickListener {
            intent.putExtra("role", getString(R.string.student))
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        // 뒤로 가기 버튼 동작 없음
    }

}