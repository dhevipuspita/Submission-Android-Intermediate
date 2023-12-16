package com.dicoding.MyStoryApp

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.MyStoryApp.data.response.ListStoryItem
import com.dicoding.MyStoryApp.databinding.ListStoriesBinding
import com.dicoding.MyStoryApp.view.DateFormatter
import java.util.TimeZone

class MyAdapter(private val onItemClickCallback: OnItemClickCallBack) :
    ListAdapter<ListStoryItem, MyAdapter.MyViewHolder>(
        DIFF_CALLBACK
    ) {

    inner class MyViewHolder(private val binding: ListStoriesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(result: ListStoryItem) {
            Glide.with(itemView.context).load(result.photoUrl).skipMemoryCache(true)
                .into(binding.imgUser)
            binding.tvName.text = result.name
            binding.tvDesc.text = result.description
            binding.tvDate.text = result.createdAt?.let { DateFormatter.formatDate(it, TimeZone.getDefault().id) }

            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val story = result
                    onItemClickCallback.onItemClicked(story)
                }
            }
        }
    }

    interface OnItemClickCallBack {
        fun onItemClicked(data: ListStoryItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ListStoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val result = getItem(position)
        holder.bind(result)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}