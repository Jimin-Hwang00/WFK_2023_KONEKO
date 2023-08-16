package nepal.swopnasansar.accountant

import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import nepal.swopnasansar.R
import nepal.swopnasansar.accountant.dto.Student

class InputTuitionListAdapter(private var itemList: ArrayList<Student>?): RecyclerView.Adapter<InputTuitionListAdapter.ViewHolder>() {
    private val TAG = "InputTuitionListAdapter"

    var students = itemList
    var changedIdx = mutableSetOf<Int>()

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tv_name = itemView.findViewById<TextView>(R.id.tv_input_tuition_item_name)
        val ev_tuition = itemView.findViewById<EditText>(R.id.ev_input_tuition_item_tuition)
        val cb_check = itemView.findViewById<CheckBox>(R.id.cb_input_tuition_item_check)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): InputTuitionListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_input_tuition_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: InputTuitionListAdapter.ViewHolder, position: Int) {
        if (students != null) {
            val student = students!![position]

            holder.tv_name.text = student.stn_name
            holder.ev_tuition.text = Editable.Factory.getInstance().newEditable(student.fee)
            holder.cb_check.isChecked = student.is_fee_paid
        }

        holder.cb_check.setOnCheckedChangeListener { _, isChecked ->
            onCheckBoxStateChanged(position, isChecked)
        }

        holder.ev_tuition.doAfterTextChanged { evText: Editable? ->
            if (students != null) {
                students!![position].fee = evText.toString()

                changedIdx.add(position)    // Students 변수 중 변경된 내용이 있는 인덱스 저장.

                Log.d(TAG, "changed fee : ${students!![position].stn_key}, ${students!![position].fee}")
            }
        }
    }

    override fun getItemCount(): Int {
        if (students != null) {
            return students!!.size
        } else {
            return 0
        }
    }

    fun onCheckBoxStateChanged(position: Int, isChecked: Boolean) {
        if (students != null && position >= 0 && position <students!!.size) {
            val student = students!![position]
            student.is_fee_paid = isChecked

            changedIdx.add(position)        // Students 변수 중 변경된 내용이 있는 인덱스 저장.
        }
    }
}