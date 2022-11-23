package com.uri.contact.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.uri.contact.databinding.ItemContactBinding
import com.uri.contact.model.Contact

class ContactAdapter : RecyclerView.Adapter<ContactAdapter.ViewHolder>() {

    private lateinit var itemClickListener: ItemClickListener
    val items = arrayListOf<Contact>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemContactBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(private val binding: ItemContactBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(contact: Contact) {
            binding.contactName.text = contact.name
            binding.contactEmail.text = contact.email
            binding.contactNumber.text = contact.number

            binding.contactEdit.setOnClickListener {
                itemClickListener.onEditClick(absoluteAdapterPosition)
            }

            binding.contactDelete.setOnClickListener {
                itemClickListener.onDeleteClick(absoluteAdapterPosition)
            }

            binding.root.setOnClickListener {
                itemClickListener.onCallClick(absoluteAdapterPosition)
            }
        }
    }

    fun insertItem(item: Contact) {
        items.add(item)
        notifyItemInserted(items.size)
    }

    fun insertItems(newItems: List<Contact>) {
        items.clear()
        items.addAll(newItems)
        notifyItemRangeInserted(items.size, newItems.size)
    }

    fun removeItem(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    fun updateItems(item: Contact) {
        items.forEachIndexed { index, contact ->
            if (contact.id == item.id) {

                contact.name = item.name
                contact.email = item.email
                contact.number = item.number

                notifyItemChanged(index)
                return@forEachIndexed
            }
        }
    }

    fun setOnClickListener(listener: ItemClickListener) {
        itemClickListener = listener
    }

    interface ItemClickListener {
        fun onEditClick(position: Int)
        fun onDeleteClick(position: Int)
        fun onCallClick(position: Int)
    }
}