package nepal.swopnasansar.accountant

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import nepal.swopnasansar.databinding.ActivityAccountantMainBinding

class AccountantMainActivity: AppCompatActivity() {
    private lateinit var binding: ActivityAccountantMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountantMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.tvTuitionCheck.setOnClickListener {
            val intent = Intent(this, AccountantTuitionCheckActivity::class.java)
            startActivity(intent)
        }

        binding.tvAccountantLogout.setOnClickListener {
            // @TODO 로그아웃 구현
        }
    }
}