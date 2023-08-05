package nepal.swopnasansar.admin

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import nepal.swopnasansar.R
import nepal.swopnasansar.admin.data.AccountantDto
import nepal.swopnasansar.admin.data.TeacherDto
import nepal.swopnasansar.databinding.ActivityAccountantListBinding
import nepal.swopnasansar.databinding.ActivityEditAccountantBinding
import nepal.swopnasansar.databinding.ActivityEditTeacherBinding
import nepal.swopnasansar.databinding.ListTeacherAndAccountBinding

class EditAccountantActivity : AppCompatActivity() {
    lateinit var binding: ActivityEditAccountantBinding
    lateinit var adapter: AccountantAdapter
    lateinit var itemBinding: ListTeacherAndAccountBinding
    val TAG = "EditAccountantActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAccountantBinding.inflate(layoutInflater)
        itemBinding = ListTeacherAndAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = Firebase.firestore
        val accountantList = ArrayList<AccountantDto>()
        var checkedList = ArrayList<AccountantDto>()
        var checkedPos = ArrayList<Int>()

        adapter = AccountantAdapter(this, accountantList)
        binding.rvAccountantList.adapter = adapter

        binding.rvAccountantList.layoutManager = LinearLayoutManager(this).apply {
            orientation = LinearLayoutManager.VERTICAL
        }


        binding.addAccountantBt.setOnClickListener {
            val name = binding.nameEt.text.toString()
            val email = binding.emailEt.text.toString()

            AlertDialog.Builder(this@EditAccountantActivity).run {
                val intent = Intent(this@EditAccountantActivity, EditAccountantActivity::class.java)
                setTitle("Add Accountant")
                setMessage("Are you sure to add Accountant \"${name}\"?")
                setNegativeButton("No", null)
                setCancelable(false)
                setPositiveButton("Yes", object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        // 문서를 추가하고 자동으로 생성된 키 값을 받아옵니다.
                        db.collection("accountant").add(AccountantDto("", name, email))
                            .addOnSuccessListener { documentReference ->
                                val documentId = documentReference.id
                                // 문서 ID를 저장한 뒤 문서에 데이터를 업데이트합니다.
                                db.collection("accountant").document(documentId)
                                    .set(AccountantDto(documentId, name, email))
                                    .addOnSuccessListener {
                                        Log.d(TAG, "add item: ${documentId}, ${name}")
                                        Toast.makeText(
                                            this@EditAccountantActivity,
                                            "Save completed",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()

                                        startActivity(intent)
                                    }
                                    .addOnFailureListener { exception ->
                                        println("Error creating document: $exception")
                                    }
                            }
                            .addOnFailureListener { exception ->
                                println("Error creating document: $exception")
                            }

                        binding.nameEt.setText("")
                        binding.emailEt.setText("")
                        adapter.onUpdateList()
                        adapter.notifyDataSetChanged()
                    }
                })
                show()
            }
        }

        val onCheckBoxClickListener = object : AccountantAdapter.onCheckBoxClickListener {
            override fun onClickCheckBox(flag: Int, position: Int) {
                Log.d(TAG, "${flag}값과 현재 position : ${position}")
                if(flag == 1){
                    checkedList.add(accountantList[position])
                    checkedPos.add(position)
                    for (item in checkedList) {
                        Log.d(TAG, "checkedList item: ${item.accountant_name}")
                    }
                }else{
                    // flag 값이 0인 경우, 해당 객체를 checkedList에서 제거
                    val targetObject = accountantList[position]
                    checkedList.remove(targetObject)
                    checkedPos.remove(position)
                    for (item in checkedList) {
                        Log.d(TAG, "checkedList item: ${item.accountant_name}")
                    }
                }
            }
        }
        adapter.setOnCheckBoxClickListener(onCheckBoxClickListener)

        binding.deleteAccountantBt.setOnClickListener {
            AlertDialog.Builder(this@EditAccountantActivity).run {
                setTitle("delete Accountant(s)")
                setMessage("Are you sure to delete?")
                setNegativeButton("No", null)
                setCancelable(false)
                for (item in accountantList) {
                    Log.d(TAG, "adapter accountant item: ${item.accountant_key}, ${item.accountant_name}")
                }
                setPositiveButton("Yes", object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        val tempCheckedList = ArrayList<AccountantDto>() // 임시 리스트 생성
                        for (i in checkedPos.indices) {
                            val pos = checkedPos[i]
                            if (pos >= 0 && pos < accountantList.size) { // 유효한 위치인지 확인
                                val ac = accountantList[pos]
                                Log.d(TAG, "${ac.accountant_key}")
                                db.collection("accountant").document(ac.accountant_key).delete()
                                    .addOnSuccessListener {
                                        adapter.onUpdateList()
                                        adapter.notifyDataSetChanged()
                                        Toast.makeText(this@EditAccountantActivity, "delete success!!", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("DeleteError", "Delete failed for accountant key: ${ac.accountant_key}, Error: ${e.message}")
                                        Toast.makeText(this@EditAccountantActivity, "delete failed: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                tempCheckedList.add(ac) // 삭제할 데이터를 임시 리스트에 추가
                            }
                        }
                        accountantList.removeIf { ac -> tempCheckedList.any { it.accountant_key == ac.accountant_key } } // 삭제할 데이터를 실제 리스트에서 제거
                        adapter.onUpdateList()
                        adapter.notifyDataSetChanged()
                        checkedPos.clear()
                        checkedList.clear()
                    }
                })
                show()
            }
        }
    }
    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }
}