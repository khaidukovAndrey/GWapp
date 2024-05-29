package com.example.kotlin_lesson_1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlin_lesson_1.databinding.ResItemBinding
import com.example.kotlin_lesson_1.domain.models.Result


class ResAdapter(private val onClickListener: OnClickListener): RecyclerView.Adapter<ResAdapter.ResHolder>(){
    private val resList = ArrayList<Result>()

    class ResHolder(item:View ):RecyclerView.ViewHolder(item) {
        val binding = ResItemBinding.bind(item)
        var details = binding.details
        fun bind(res: Result) = with(binding){
            tvTitle.text = res.disease
            tvDate.text = res.date
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
        holder.details.setOnClickListener{
            onClickListener.onClicked(resList[position])
        }
    }
    fun addResults(res: Result)
    {
        resList.add(res)
        notifyDataSetChanged()
    }
}