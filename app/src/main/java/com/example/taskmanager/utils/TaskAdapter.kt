package com.example.taskmanager.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanager.R
import com.example.taskmanager.databinding.EachTaskItemBinding
import com.example.taskmanager.databinding.FragmentHomeBinding

class TaskAdapter(private val type:String,private val list:MutableList<TaskData>) :RecyclerView.Adapter<TaskAdapter.TaskViewHolder>(){
    private var listener:TaskAdapterClickInterface?=null
    fun setListener(listener:TaskAdapterClickInterface){
        this.listener=listener
    }
    inner class TaskViewHolder(val binding:EachTaskItemBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding=EachTaskItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return TaskViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        with(holder){
            with(list[position]){
                binding.titleVal.text = this.taskObj.title
                binding.categoryVal.text=this.taskObj.category
                binding.priortyVal.text=this.taskObj.priority
                binding.descriptionVal.text=this.taskObj.description
                binding.dateVal.text=this.taskObj.date

                // Set the background color based on the priority value
                when (this.taskObj.priority) {
                    "High" -> binding.priortyVal.setBackgroundResource(R.drawable.high_priority_background)
                    "Medium" -> binding.priortyVal.setBackgroundResource(R.drawable.medium_priority_background)
                    "Low" -> binding.priortyVal.setBackgroundResource(R.drawable.low_priority_background)
                    else -> binding.priortyVal.setBackgroundResource(R.drawable.default_priority_background)
                }

                when(this.taskObj.status) {
                    "Pending" ->{
                        binding.cardPaddingView1.visibility = View.GONE
                        binding.completedText.visibility = View.GONE
                        binding.completeBtn.visibility = View.VISIBLE
                        binding.cardPaddingView2.visibility = View.VISIBLE
//                        binding.cardView.strokeColor = ContextCompat.getColor(binding.root.context, R.color.border_default)
//                        binding.cardView.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.card_default))
                    }
                    "Completed" -> {
                        binding.cardPaddingView1.visibility = View.VISIBLE
                        binding.completedText.visibility = View.VISIBLE
                        binding.completeBtn.visibility = View.GONE
                        binding.cardPaddingView2.visibility = View.GONE
//                        binding.cardView.strokeColor = ContextCompat.getColor(binding.root.context, R.color.border_completed)
//                        binding.cardView.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.card_completed))
                    }

                }

                binding.deleteBtn.setOnClickListener{
                    listener?.onDeleteTaskBtnClicked(this)
                }
                binding.completeBtn.setOnClickListener{
                    listener?.onCompleteTaskBtnClicked(this)
                }

            }
        }
    }
    interface TaskAdapterClickInterface{
        fun onDeleteTaskBtnClicked(taskData:TaskData)
        fun onEditTaskBtnClicked(taskData: TaskData)
        fun onCompleteTaskBtnClicked(taskData: TaskData)

    }
}