package com.example.mobiledevproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobiledevproject.R
import com.example.mobiledevproject.model.ExerciseEntry

class ExerciseSuggestionAdapter(
    private val suggestions: MutableList<ExerciseEntry>,
    private val onAddClick: (ExerciseEntry) -> Unit
) : RecyclerView.Adapter<ExerciseSuggestionAdapter.SuggestionViewHolder>() {

    inner class SuggestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvSuggestionName)
        val tvCalories: TextView = itemView.findViewById(R.id.tvSuggestionCalories)
        val btnAdd: Button = itemView.findViewById(R.id.btnAddSuggestion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.exercise_suggestion_item, parent, false)
        return SuggestionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SuggestionViewHolder, position: Int) {
        val exercise = suggestions[position]
        holder.tvName.text = exercise.name
        holder.tvCalories.text = "Calories: ${exercise.caloriesBurned}"
        holder.btnAdd.setOnClickListener {
            onAddClick(exercise)
        }
    }

    override fun getItemCount(): Int = suggestions.size

    fun updateList(newList: List<ExerciseEntry>) {
        suggestions.clear()
        suggestions.addAll(newList)
        notifyDataSetChanged()
    }
}
