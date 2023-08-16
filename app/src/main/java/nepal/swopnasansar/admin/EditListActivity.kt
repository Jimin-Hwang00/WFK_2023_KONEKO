package nepal.swopnasansar.admin

import android.content.Intent
import nepal.swopnasansar.databinding.ActivityEditListBinding
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class EditListActivity : AppCompatActivity() {
    lateinit var binding : ActivityEditListBinding
    val TAG = "EditListActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.EtStnBt.setOnClickListener{
            val intent = Intent(this@EditListActivity, StudentListActivity::class.java)
            startActivity(intent)
        }

        binding.EtTeacherBt.setOnClickListener{
            val intent = Intent(this@EditListActivity, TeacherListActivity::class.java)
            startActivity(intent)
        }

        binding.EtAccountBt.setOnClickListener{
            val intent = Intent(this@EditListActivity, AccountantListActivity::class.java)
            startActivity(intent)
        }
    }
}
