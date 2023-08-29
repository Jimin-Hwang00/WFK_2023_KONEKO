package nepal.swopnasansar

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.CoroutineScope
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
                val dialog = SignOutDialog(this)
                dialog.setOnClickListener(object: SignOutDialog.ButtonClickListener {
                    override fun onClicked(email: String, pw: String) {
                        if (email.isNotBlank() || pw.isNotBlank())  {
                            val user = authDao.getUser()

                            if (user != null) {
                                try {
                                    lifecycleScope.launch {
                                        binding.pbAccountantMain.visibility = View.VISIBLE

                                        val credential = EmailAuthProvider.getCredential(email, pw)
                                        user.reauthenticate(credential)
                                            .addOnSuccessListener {
                                                CoroutineScope(Dispatchers.IO).launch {
                                                    val deleteDBResult = withContext(Dispatchers.IO) {
                                                        accountantDao.removeAccountantByKey(uid!!)
                                                    }

                                                    withContext(Dispatchers.Main) {
                                                        if (deleteDBResult) {
                                                            user.delete()
                                                                .addOnSuccessListener {
                                                                    Toast.makeText(applicationContext, "Your account has been deleted.", Toast.LENGTH_LONG).show()
                                                                    binding.pbAccountantMain.visibility = View.GONE
                                                                    val intent = Intent(this@AccountantMainActivity, CheckRoleActivity::class.java)
                                                                    startActivity(intent)
                                                                }
                                                                .addOnFailureListener {
                                                                    Toast.makeText(this@AccountantMainActivity, "An error has occurred. Please contact administrator", Toast.LENGTH_LONG).show()
                                                                    binding.pbAccountantMain.visibility = View.GONE
                                                                }
                                                        } else {
                                                            Toast.makeText(this@AccountantMainActivity, "An error has occurred. Please contact administrator.", Toast.LENGTH_SHORT).show()
                                                            binding.pbAccountantMain.visibility = View.GONE
                                                        }
                                                    }
                                                }

                                            }
                                            .addOnFailureListener {
                                                Toast.makeText(this@AccountantMainActivity, "Email or password is incorrect. Please check again.", Toast.LENGTH_LONG).show()
                                                binding.pbAccountantMain.visibility = View.GONE
                                            }


                                    }
                                } catch (e: FirebaseAuthInvalidCredentialsException) {
                                    Toast.makeText(this@AccountantMainActivity, "Email or password is incorrect. Please check again.", Toast.LENGTH_LONG).show()
                                    binding.pbAccountantMain.visibility = View.GONE
                                } catch (e: FirebaseNetworkException) {
                                    Toast.makeText(this@AccountantMainActivity, "Network issue has occurred. Please try again later.", Toast.LENGTH_LONG).show()
                                    binding.pbAccountantMain.visibility = View.GONE
                                } catch (e: FirebaseException) {
                                    Toast.makeText(this@AccountantMainActivity, "A DB service error has occurred. Please try again later.", Toast.LENGTH_LONG).show()
                                    binding.pbAccountantMain.visibility = View.GONE
                                } catch (e: FirebaseAuthInvalidUserException) {
                                    Toast.makeText(this@AccountantMainActivity, "User does not exist.", Toast.LENGTH_LONG).show()
                                    binding.pbAccountantMain.visibility = View.GONE
                                } catch (e: Exception) {
                                    Toast.makeText(this@AccountantMainActivity, "An error has occurred. Please try again later.", Toast.LENGTH_LONG).show()
                                    binding.pbAccountantMain.visibility = View.GONE
                                }
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