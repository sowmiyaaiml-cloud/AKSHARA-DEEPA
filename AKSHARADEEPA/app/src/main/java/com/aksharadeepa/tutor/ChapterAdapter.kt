package com.aksharadeepa.tutor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChapterAdapter(
    private val chapters: List<ChapterItem>,
    private val onToggleListener: (String, Boolean) -> Unit
) : RecyclerView.Adapter<ChapterAdapter.ViewHolder>() {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chapter, parent, false)
        return ViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = chapters[position]
        holder.subjectText.text = item.subject
        holder.chapterText.text = item.chapterName
        holder.checkBox.isChecked = item.isCompleted
        
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            item.isCompleted = isChecked
            onToggleListener(item.chapterName, isChecked)
        }
    }
    
    override fun getItemCount() = chapters.size
    
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val subjectText: TextView = itemView.findViewById(R.id.subjectText)
        val chapterText: TextView = itemView.findViewById(R.id.chapterText)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)
    }
}