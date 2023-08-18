package nepal.swopnasansar

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
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
    }

    override fun onResume() {
        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            if (auth.currentUser != null) {
                Log.d("MainActivity", "auth.currentUser : ${auth.currentUser}")
                changeActivityToUserMain()
            } else {
                val intent = Intent(this@MainActivity, CheckRoleActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                startActivity(intent)
            }
        }, 3000)
        super.onResume()
    }

    fun changeActivityToUserMain() {
        val pref: SharedPreferences = getSharedPreferences("save_state", Context.MODE_PRIVATE)
        val role = pref.getString("role", null)

        if (role != null) {
            when (role) {
                getString(R.string.administrator) -> {
                    val intent = Intent(this, AdminMainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                    startActivity(intent)
                }
                getString(R.string.accountant) -> {
                    val intent = Intent(this, AccountantMainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                    startActivity(intent)
                }
                getString(R.string.teacher) -> {
                    val intent = Intent(this, TeacherMainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                    startActivity(intent)
                }
                getString(R.string.student) -> {
                    val intent = Intent(this, SPMainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                    startActivity(intent)
                }
                else -> {
                    val intent = Intent(this, CheckRoleActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                    startActivity(intent)
                }
            }
        } else {
            val intent = Intent(this, CheckRoleActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            startActivity(intent)
        }
    }

}