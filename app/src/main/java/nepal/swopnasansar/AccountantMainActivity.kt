package nepal.swopnasansar

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nepal.swopnasansar.accountant.AccountantTuitionCheckActivity
import nepal.swopnasansar.dao.AccountantDAO
import nepal.swopnasansar.dao.AuthDAO
import nepal.swopnasansar.databinding.ActivityAccountantMainBinding
import nepal.swopnasansar.dto.Accountant
import nepal.swopnasansar.login.CheckRoleActivity

class AccountantMainActivity: AppCompatActivity() {
    private lateinit var binding: ActivityAccountantMainBinding

    private val authDao = AuthDAO()
    private val accountantDao = AccountantDAO()

    val uid = authDao.getUid()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountantMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (uid == null) {
            val intent = Intent(this, CheckRoleActivity::class.java)
            startActivity(intent)
        } else {
            lifecycleScope.launch {
                binding.pbAccountantMain.visibility = View.VISIBLE

                val accountant: Accountant? = withContext(Dispatchers.IO) {
                    accountantDao.getAccountantByKey(uid!!)
                }

                if (accountant != null) {
                    binding.tvAccountantName.text = accountant.accountant_name
                } else {
                    binding.tvAccountantName.text = ""
                }

                binding.pbAccountantMain.visibility = View.INVISIBLE
            }
        }

        binding.tvTuitionCheck.setOnClickListener {
            val intent = Intent(this, AccountantTuitionCheckActivity::class.java)
            startActivity(intent)
        }

        binding.arrowAccountantEditTuiton.setOnClickListener {
            val intent = Intent(this, AccountantTuitionCheckActivity::class.java)
            startActivity(intent)
        }

        binding.tvAccountantLogout.setOnClickListener {
            authDao.logout()

            val intent = Intent(this, CheckRoleActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        // 뒤로 가기 버튼 동작 없음
    }

}