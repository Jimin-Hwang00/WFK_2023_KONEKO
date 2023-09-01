package nepal.swopnasansar.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import nepal.swopnasansar.databinding.ListTeacherAndAccountBinding
import nepal.swopnasansar.dto.Teacher

class SelectedTeacherForSubjectAdapter: RecyclerView.Adapter<SelectedTeacherForSubjectAdapter.ViewHolder>() {

    var items: ArrayList<Teacher> = ArrayList()
    private lateinit var itemClickListner: ItemClickListener

    var mSelectedItem = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ListTeacherAndAccountBinding.inflate(LayoutInflater.from(parent.context),parent,false))


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount(): Int = items.size


    fun setItem(item: ArrayList<Teacher>) {
        items = item
        mSelectedItem = -1
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ListTeacherAndAccountBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item: Teacher, position: Int) {
            val checkBox = binding.deleteCheckBox
            binding.nameTv.text = item.teacher_name
            checkBox.isChecked = position == mSelectedItem

            binding.emailTv.visibility = View.GONE

            checkBox.setOnClickListener {
                mSelectedItem = position
                itemClickListner.onClick(it,item)
                notifyItemRangeChanged(0, items.size)
            }

        }
    }

    interface ItemClickListener {
        fun onClick(view: View, classItem: Teacher)
    }

    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListner = itemClickListener
    }
}