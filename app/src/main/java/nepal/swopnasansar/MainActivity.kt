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
                    val intent = Intent(this, AdminMainActivity::class.java)
                    startActivity(intent)
                }
                getString(R.string.accountant) -> {
                    val intent = Intent(this, AccountantMainActivity::class.java)
                    startActivity(intent)
                }
                getString(R.string.teacher) -> {
                    val intent = Intent(this, TeacherMainActivity::class.java)
                    startActivity(intent)
                }
                getString(R.string.student) -> {
                    val intent = Intent(this, SPMainActivity::class.java)
                    startActivity(intent)
                }
                else -> {
                    val intent = Intent(this, CheckRoleActivity::class.java)
                    startActivity(intent)
                }
            }
        } else {
            val intent = Intent(this, CheckRoleActivity::class.java)
            startActivity(intent)
        }
    }

}