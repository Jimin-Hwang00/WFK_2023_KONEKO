package nepal.swopnasansar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nepal.swopnasansar.admin.AdminCalAdapter
import nepal.swopnasansar.admin.CheckEventActivity
import nepal.swopnasansar.admin.ClassListActivity
import nepal.swopnasansar.admin.EditListActivity
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
            Toast.makeText(applicationContext, "You have to login.", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, CheckRoleActivity::class.java)
            startActivity(intent)
        }

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

        //admin이름값 가져오기
//        var firestore : FirebaseFirestore? = null
//
//        firestore = FirebaseFirestore.getInstance()
//        firestore?.collection("admin")?.get()?.addOnSuccessListener { result ->
//            val tempList = ArrayList<StudentDto>() // 새로운 리스트를 만듦
//            for (snapshot in result) {
//                tempList.add(snapshot.toObject(StudentDto::class.java))
//            }
//            // 기존의 adminCalList를 지우고 정렬된 요소들을 추가
//            studentList.clear()
//            studentList.addAll(tempList)
//
//            notifyDataSetChanged()
//        }


        binding.userCreateBt.setOnClickListener{
            val intent = Intent(this, EditListActivity::class.java)
            startActivity(intent)
        }

        binding.userCreateBt1.setOnClickListener{
            val intent = Intent(this, EditListActivity::class.java)
            startActivity(intent)
        }

        binding.createEventBt.setOnClickListener{
            val intent = Intent(this, CheckEventActivity::class.java)
            startActivity(intent)
        }

        binding.createEventBt1.setOnClickListener{
            val intent = Intent(this, CheckEventActivity::class.java)
            startActivity(intent)
        }
        binding.classCreateBt.setOnClickListener{
            val intent = Intent(this, ClassListActivity::class.java)
            startActivity(intent)
        }

        binding.classCreateBt2.setOnClickListener{
            val intent = Intent(this, ClassListActivity::class.java)
            startActivity(intent)
        }

        binding.tvAdminLogout.setOnClickListener {
            authDao.logout()

            val intent = Intent(this, CheckRoleActivity::class.java)
            startActivity(intent)
        }
    }
}
