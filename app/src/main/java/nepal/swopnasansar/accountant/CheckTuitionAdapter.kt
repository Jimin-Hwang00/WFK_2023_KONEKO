package nepal.swopnasansar.accountant

import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import nepal.swopnasansar.R
import nepal.swopnasansar.dto.Student

class CheckTuitionAdapter(private var itemList: ArrayList<Student>?): RecyclerView.Adapter<CheckTuitionAdapter.ViewHolder>() {
    var students = itemList

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tv_name = itemView.findViewById<TextView>(R.id.tv_check_tuition_item_name)
        val tv_tuition = itemView.findViewById<TextView>(R.id.tv_check_tuition_item_tuition)
        val tv_check = itemView.findViewById<TextView>(R.id.tv_check_tuition_item_check)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CheckTuitionAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_check_tuition_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: CheckTuitionAdapter.ViewHolder, position: Int) {
        if (students != null) {
            val student = students!![position]

            holder.tv_name.text = student.stn_name
            holder.tv_tuition.text = student.fee

            if (student.is_fee_paid) {
                holder.tv_check.text = "O"
            } else {
                holder.tv_check.text = "X"
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
}