package nepal.swopnasansar

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.firebase.auth.FirebaseAuth
import nepal.swopnasansar.databinding.ActivityMainBinding
import nepal.swopnasansar.login.CheckRoleActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            if (auth.currentUser != null) {
                changeActivityToUserMain()
            } else {
                val intent = Intent(this@MainActivity, CheckRoleActivity::class.java)
                startActivity(intent)
            }
        }, 3000)
    }

    fun changeActivityToUserMain() {
        val pref: SharedPreferences = getSharedPreferences("save_state", Context.MODE_PRIVATE)
        val role = pref.getString("role", null)

        if (role != null) {
            when (role) {
                getString(R.string.administrator) -> {
                    // @TODO 자동 로그인 구현 - administrator 화면으로 이동
                }
                getString(R.string.accountant) -> {
                    // @TODO 자동 로그인 구현 - student 화면으로 이동
                }
                getString(R.string.teacher) -> {
                    // @TODO 자동 로그인 구현 - teacher 화면으로 이동
                }
                getString(R.string.student) -> {
                    // @TODO 자동 로그인 구현 - parent 화면으로 이동
                }
                else -> {
                    val intent = Intent(this@MainActivity, CheckRoleActivity::class.java)
                    startActivity(intent)
                }
            }
        } else {
            val intent = Intent(this@MainActivity, CheckRoleActivity::class.java)
            startActivity(intent)
        }
    }

}