package com.pdm.contact.presentation.contact

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pdm.contact.databinding.ItemContactBinding
import com.pdm.contact.feature.domain.model.Contact

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
            binding.contactNumber.text = buildString {
                append(contact.country + " " + contact.number)
            }

            binding.contactEdit.setOnClickListener {
                itemClickListener.onEditClick(absoluteAdapterPosition)
            }

            binding.contactDelete.setOnClickListener {
                itemClickListener.onDeleteClick(absoluteAdapterPosition)
            }

            binding.root.setOnClickListener {
                itemClickListener.onCallClick(absoluteAdapterPosition)
            }

            binding.root.setOnLongClickListener {
                itemClickListener.onShareClick(absoluteAdapterPosition)
                false
            }
        }
    }

    fun insertItems(newItems: List<Contact>) {
        val oldAdapterSize = items.size
        val newAdapterSize = newItems.size

        if (items.isEmpty()) {
            items.addAll(newItems)
            notifyItemRangeInserted(oldAdapterSize, newAdapterSize)
        }
    }

    fun updateItem(contact: Contact) = run loop@ {
        items.forEachIndexed { index, item ->
            if (item.id == contact.id) {
                items.removeAt(index)
                items.add(index, contact)
                notifyItemChanged(index)
                return@loop
            }
        }
    }

    fun filterItems(query: String) {
        val newFilteredList = items.filter { it.name.lowercase().contains(query.lowercase()) }
        val newAdapterSize = newFilteredList.size
        val oldAdapterSize = items.size

        items.clear()
        notifyItemRangeRemoved(0, oldAdapterSize)

        items.addAll(newFilteredList)
        notifyItemRangeInserted(0, newAdapterSize)
    }

    fun insertItem(contact: Contact) {
        items.add(contact)
        notifyItemInserted(items.size)
    }

    fun removeItem(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    fun cleanItems() {
        val oldAdapterSize = items.size
        items.clear()
        notifyItemRangeRemoved(0, oldAdapterSize)
    }

    fun setOnClickListener(listener: ItemClickListener) {
        itemClickListener = listener
    }

    interface ItemClickListener {
        fun onEditClick(position: Int)
        fun onDeleteClick(position: Int)
        fun onCallClick(position: Int)
        fun onShareClick(position: Int)
    }
}