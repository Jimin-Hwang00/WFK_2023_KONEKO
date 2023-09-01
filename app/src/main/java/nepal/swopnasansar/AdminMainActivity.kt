package nepal.swopnasansar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nepal.swopnasansar.admin.*
import nepal.swopnasansar.comment.AdminCmntMainActivity
import nepal.swopnasansar.comment.ReceivedCmntListAcitivity
import nepal.swopnasansar.dao.AdminDAO
import nepal.swopnasansar.dao.AuthDAO
import nepal.swopnasansar.databinding.ActivityAdminMainBinding
import nepal.swopnasansar.dto.Administrator
import nepal.swopnasansar.login.CheckRoleActivity

class AdminMainActivity : AppCompatActivity() {
    lateinit var binding :ActivityAdminMainBinding
    lateinit var adapter : AdminCalAdapter
    val TAG = "MainActivity"

    private val authDao = AuthDAO()
    private val adminDao = AdminDAO()

    val uid = authDao.getUid()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (uid == null) {
            val intent = Intent(this, CheckRoleActivity::class.java)
            startActivity(intent)
        } else {
            Log.d("AdminMainActivity", "uid : ${uid!!}")
            lifecycleScope.launch {
                binding.pbAdminMain.visibility = View.VISIBLE

                val admin: Administrator? = withContext(Dispatchers.IO) {
                    adminDao.getAdminByKey(uid!!)
                }

                if (admin != null) {
                    binding.tvAdminName.text = admin.admin_name
                } else {
                    binding.tvAdminName.text = ""
                }

                binding.pbAdminMain.visibility = View.INVISIBLE
            }
        }

        binding.tvAdminEditList.setOnClickListener{
            val intent = Intent(this, EditListActivity::class.java)
            startActivity(intent)
        }

        binding.arrowAdminEditList.setOnClickListener {
            val intent = Intent(this, EditListActivity::class.java)
            startActivity(intent)
        }

        binding.tvAdminCreateClasses.setOnClickListener{
            val intent = Intent(this, ClassListActivity::class.java)
            startActivity(intent)
        }

        binding.arrowAdminCreateClasses.setOnClickListener {
            val intent = Intent(this, ClassListActivity::class.java)
            startActivity(intent)
        }

        binding.tvAdminCreateSubjects.setOnClickListener {
            val intent = Intent(this, SubjectListActivity::class.java)
            startActivity(intent)
        }

        binding.arrowAdminCreateSubjects.setOnClickListener {
            val intent = Intent(this, SubjectListActivity::class.java)
            startActivity(intent)
        }

        binding.tvAdminCmnt.setOnClickListener{
            val intent = Intent(this, AdminCmntMainActivity::class.java)
            startActivity(intent)
        }

        binding.arrowAdminCmnt.setOnClickListener {
            val intent = Intent(this, AdminCmntMainActivity::class.java)
            startActivity(intent)
        }

        binding.tvAdminEditCalendar.setOnClickListener{
            val intent = Intent(this, CheckEventActivity::class.java)
            startActivity(intent)
        }

        binding.arrowAdminEditCalendar.setOnClickListener {
            val intent = Intent(this, CheckEventActivity::class.java)
            startActivity(intent)
        }

        binding.tvAdminLogout.setOnClickListener {
            askForLogOut()
        }
    }

    fun askForLogOut() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.apply {
            setTitle("Log out")
            setMessage("Would you like to log out?")
            setPositiveButton("YES") { dialog, which ->
                authDao.logout()

                val intent = Intent(this@AdminMainActivity, CheckRoleActivity::class.java)
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
