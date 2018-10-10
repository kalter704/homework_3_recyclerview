package com.tinkoff.androidcourse

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import java.util.*

class WorkerRecyclerAdapter : RecyclerView.Adapter<WorkerRecyclerAdapter.WorkerViewHolder>(), MainActivity.ItemTouchHelperAdapter {

    var workers = mutableListOf<Worker>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkerViewHolder =
            WorkerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_worker_list, parent, false))

    override fun getItemCount(): Int = workers.size

    override fun onBindViewHolder(holder: WorkerViewHolder, position: Int) {
        holder.bind(workers[position])
    }

    override fun onItemMove(from: Int, to: Int) {
        Collections.swap(workers, from, to)
        notifyItemMoved(from, to)
    }

    override fun onItemDismiss(position: Int) {
        workers.removeAt(position)
        notifyItemRemoved(position)
    }


    class WorkerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val photoImg = itemView.findViewById<ImageView>(R.id.photo)
        private val nameTv = itemView.findViewById<TextView>(R.id.name)
        private val ageTv = itemView.findViewById<TextView>(R.id.age)
        private val positionTv = itemView.findViewById<TextView>(R.id.position)

        fun bind(worker: Worker) {
            with(worker) {
                photoImg.setImageResource(photo)
                nameTv.text = name
                ageTv.text = age
                positionTv.text = position
            }
        }
    }

}