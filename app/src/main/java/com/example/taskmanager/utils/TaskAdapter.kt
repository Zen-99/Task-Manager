package com.example.taskmanager.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
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

                binding.deleteBtn.setOnClickListener{
                    listener?.onDeleteTaskBtnClicked(this)
                }
                binding.editBtn.setOnClickListener{
                    listener?.onEditTaskBtnClicked(this)
                }

            }
        }
    }
    interface TaskAdapterClickInterface{
        fun onDeleteTaskBtnClicked(taskData:TaskData)
        fun onEditTaskBtnClicked(taskData: TaskData)

    }
}