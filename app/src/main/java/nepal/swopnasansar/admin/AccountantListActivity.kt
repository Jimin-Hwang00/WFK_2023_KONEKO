package nepal.swopnasansar.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import nepal.swopnasansar.admin.data.AccountantDto
import nepal.swopnasansar.databinding.ActivityAccountantListBinding

class AccountantListActivity : AppCompatActivity() {
    lateinit var binding : ActivityAccountantListBinding
    lateinit var adapter : AccountantAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountantListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val accountantList = ArrayList<AccountantDto>()
        adapter = AccountantAdapter(this,accountantList)
        binding.rvAccountantList.adapter = adapter

        binding.rvAccountantList.layoutManager = LinearLayoutManager(this).apply {
            orientation = LinearLayoutManager.VERTICAL
        }

        binding.AccountantListEdBt.setOnClickListener{
            val intent = Intent(this@AccountantListActivity, EditAccountantActivity::class.java)
            startActivity(intent)
        }
    }
}