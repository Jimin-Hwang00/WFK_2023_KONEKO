package nepal.swopnasansar.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import nepal.swopnasansar.databinding.ActivityAdminMainBinding

class AdminMainActivity : AppCompatActivity() {
    lateinit var binding :ActivityAdminMainBinding
    lateinit var adapter : AdminCalAdapter
    val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
    }
}
