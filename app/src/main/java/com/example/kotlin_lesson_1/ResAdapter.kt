package com.example.kotlin_lesson_1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlin_lesson_1.databinding.ResItemBinding

class ResAdapter: RecyclerView.Adapter<ResAdapter.ResHolder>(){
    val resList = ArrayList<Result>()
    class ResHolder(item:View):RecyclerView.ViewHolder(item) {
        val binding = ResItemBinding.bind(item)
        fun bind(res: Result) = with(binding){
            tvTitle.text = res.disease
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.res_item, parent, false)
        return ResHolder(view)
    }

    override fun getItemCount(): Int {
        return resList.size
    }

    override fun onBindViewHolder(holder: ResHolder, position: Int) {
        holder.bind(resList[position])
    }
    fun addResults(res:Result)
    {
        resList.add(res)
        notifyDataSetChanged()
    }
}