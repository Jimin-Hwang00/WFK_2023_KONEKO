package nepal.swopnasansar.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import nepal.swopnasansar.databinding.RvSelectClassItemBinding
import nepal.swopnasansar.dto.Class

class SelectedClassAdapter: RecyclerView.Adapter<SelectedClassAdapter.ViewHolder>() {

    var items: ArrayList<Class> = ArrayList()
    private lateinit var itemClickListner: ItemClickListener

    var mSelectedItem = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(RvSelectClassItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount(): Int = items.size


    fun setItem(item: ArrayList<Class>) {
        items = item
        mSelectedItem = -1
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: RvSelectClassItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item: Class, position: Int) {
            val checkBox = binding.cbSelectClass
            binding.tvSelectClassItem.text = item.class_name
            checkBox.isChecked = position == mSelectedItem

            checkBox.setOnClickListener {
                mSelectedItem = position
                itemClickListner.onClick(it,item)
                notifyItemRangeChanged(0, items.size)
            }

        }
    }

    interface ItemClickListener {
        fun onClick(view: View, classItem: Class)
    }

    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListner = itemClickListener
    }
}