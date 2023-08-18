package nepal.swopnasansar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.EmailAuthProvider
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
            askForLogOut()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_signout, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.item_sign_out -> {
                val dialog = SignoutDialog(this)
                dialog.setOnClickListener(object: SignoutDialog.ButtonClickListener {
                    override fun onClicked(email: String, pw: String) {
                        val user = authDao.getUser()

                        if (user != null) {
                            val credential = EmailAuthProvider.getCredential(email, pw)
                            user.reauthenticate(credential)
                                .addOnSuccessListener {
                                    user.delete()

                                    lifecycleScope.launch {
                                        val deleteDBResult = withContext(Dispatchers.IO) {
                                            accountantDao.removeAccountantByKey(uid!!)
                                        }

                                        if (deleteDBResult) {
                                            Toast.makeText(applicationContext, "Your account has been deleted.", Toast.LENGTH_LONG).show()
                                            val intent = Intent(this@AccountantMainActivity, CheckRoleActivity::class.java)
                                            startActivity(intent)
                                        }
                                    }
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this@AccountantMainActivity, "Fail to delete your account. Try again.", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this@AccountantMainActivity, "Authentication has failed.", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                })
                dialog.showDialog()

                true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun askForLogOut() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.apply {
            setTitle("Log out")
            setMessage("Would you like to log out?")
            setPositiveButton("YES") { dialog, which ->
                authDao.logout()

                val intent = Intent(this@AccountantMainActivity, CheckRoleActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
            setNegativeButton("NO") { dialog, which ->
                dialog.dismiss()
            }
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    override fun onBackPressed() {
        // 뒤로 가기 버튼 동작 없음
    }

}