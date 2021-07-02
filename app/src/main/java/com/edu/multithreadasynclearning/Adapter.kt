package com.edu.multithreadasynclearning

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.edu.multithreadasynclearning.databinding.RecyclerListItemBinding
import java.util.*


class Adapter : RecyclerView.Adapter<Adapter.ViewHolder>() {
    private val values: LinkedList<String> = LinkedList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RecyclerListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = values.size

    fun setItems(newItems: LinkedList<String>) {
        values.clear()
        values.addAll(newItems)
        notifyDataSetChanged()
    }

    fun addItem(item: String){
        values.add(0, item)
        Log.d("Inserted", item)
        notifyDataSetChanged()
//        notifyItemInserted(0)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.itemValue.text = values[position]
    }

    inner class ViewHolder(val binding: RecyclerListItemBinding) : RecyclerView.ViewHolder(binding.root)

}