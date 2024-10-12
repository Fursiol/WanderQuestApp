package com.example.wanderquest.view.admin.quests.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.wanderquest.R
import com.example.wanderquest.databinding.ItemQuestBinding
import com.example.wanderquest.domain.model.Quest

class DiffUtilQuest : DiffUtil.ItemCallback<Quest>() {
    override fun areItemsTheSame(oldItem: Quest, newItem: Quest): Boolean {
        return oldItem.ID == newItem.ID
    }

    override fun areContentsTheSame(oldItem: Quest, newItem: Quest): Boolean {
        return oldItem == newItem
    }
}

class QuestAdapter :
    ListAdapter<Quest, QuestAdapter.QuestViewHolder>(DiffUtilQuest()) {

    var questItemClicked: ((Quest) -> Unit)? = null
    var questEditClicked: ((Quest) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestViewHolder {
        val binding : ItemQuestBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_quest,
            parent,
            false
        )
        return QuestViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuestViewHolder, position: Int) {
        val quest = getItem(position)
        holder.binding.quest = quest
        holder.binding.questNameTextView.text = quest.question

        holder.binding.root.setOnClickListener {
            questItemClicked?.invoke(quest)
        }

        holder.binding.questEditButton.setOnClickListener {
            questEditClicked?.invoke(quest)
        }
    }
    class QuestViewHolder(val binding : ItemQuestBinding) : RecyclerView.ViewHolder(binding.root)
}