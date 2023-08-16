package nepal.swopnasansar.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import nepal.swopnasansar.data.AccountantDto
import nepal.swopnasansar.databinding.ActivityAccountantListBinding

class AccountantListActivity : AppCompatActivity() {
    lateinit var binding : ActivityAccountantListBinding
    lateinit var adapter : AccountantAdapter
    var progressBarVisible = true

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
    // 뷰 홀더가 생성되어 화면에 표시된 후에 ProgressBar를 숨기는 메서드
    fun hideProgressBar() {
        if (progressBarVisible) {
            progressBarVisible = false
            binding.progressBar.visibility = View.INVISIBLE
        }
    }

}