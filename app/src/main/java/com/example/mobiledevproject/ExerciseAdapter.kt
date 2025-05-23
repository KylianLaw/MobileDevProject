package com.example.mobiledevproject

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.mobiledevproject.R
import com.example.mobiledevproject.model.ExerciseEntry

class ExerciseAdapter(
    private val exerciseList: MutableList<ExerciseEntry>,
    private val onCheckChanged: (ExerciseEntry) -> Unit
) : RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    inner class ExerciseViewHolder(val checkBox: CheckBox) :
        RecyclerView.ViewHolder(checkBox)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val checkBox = LayoutInflater.from(parent.context)
            .inflate(R.layout.exercise_item, parent, false) as CheckBox
        return ExerciseViewHolder(checkBox)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exerciseList[position]
        holder.checkBox.text = exercise.name
        holder.checkBox.isChecked = exercise.isChecked

        updateStyle(holder.checkBox, exercise.isChecked)

        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            exercise.isChecked = isChecked
            updateStyle(holder.checkBox, isChecked)
            onCheckChanged(exercise)
        }
    }

    override fun getItemCount(): Int = exerciseList.size

    private fun updateStyle(cb: CheckBox, isChecked: Boolean) {
        if (isChecked) {
            cb.paintFlags = cb.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            cb.alpha = 0.5f
        } else {
            cb.paintFlags = cb.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            cb.alpha = 1f
        }
    }
}
