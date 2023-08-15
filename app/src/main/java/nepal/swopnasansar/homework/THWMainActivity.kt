package nepal.swopnasansar.homework

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import nepal.swopnasansar.databinding.ActivityTHwMainBinding

class THWMainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTHwMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTHwMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvUploadHw.setOnClickListener {
            val intent = Intent(this@THWMainActivity, TUploadHWActivity::class.java)
            startActivity(intent)
        }

        binding.tvMyUploadedHw.setOnClickListener {
            val intent = Intent(this@THWMainActivity, TCheckUploadedHWActivity::class.java)
            startActivity(intent)
        }

        binding.tvCheckHw.setOnClickListener {
            val intent = Intent(this@THWMainActivity, TCheckSubmittedHWActivity::class.java)
            startActivity(intent)
        }
    }
}