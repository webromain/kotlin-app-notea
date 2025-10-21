package com.example.notea.adapters

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.notea.R
import com.example.notea.models.Note
import java.text.SimpleDateFormat
import java.util.*

class NoteAdapter(
    private val notes: MutableList<Note>,
    private val onNoteClick: (Note) -> Unit,
    private val onNoteChecked: (Note, Boolean) -> Unit
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.cardView)
        val titleText: TextView = itemView.findViewById(R.id.titleText)
        val contentText: TextView = itemView.findViewById(R.id.contentText)
        val priorityBadge: TextView = itemView.findViewById(R.id.priorityBadge)
        val dueDateText: TextView = itemView.findViewById(R.id.dueDateText)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)
        val iconEdit: ImageView = itemView.findViewById(R.id.iconEdit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]

        // Lier les données
        holder.titleText.text = note.title
        holder.contentText.text = note.content
        holder.cardView.setCardBackgroundColor(Color.parseColor(note.getBackgroundColor()))
        holder.priorityBadge.text = note.getPriorityText()
        holder.priorityBadge.setBackgroundColor(Color.parseColor(note.getPriorityColor()))

        // Afficher la date
        if (note.dueDate != null) {
            holder.dueDateText.visibility = View.VISIBLE
            holder.dueDateText.text = dateFormat.format(note.dueDate!!)
        } else {
            holder.dueDateText.visibility = View.GONE
        }

        // Gérer l'apparence de la tâche (barrée ou non)
        updateTaskAppearance(holder, note.isCompleted)

        // Gérer l'état de la checkbox
        holder.checkBox.setOnCheckedChangeListener(null) // Très important pour éviter les bugs de recyclage
        holder.checkBox.isChecked = note.isCompleted

        // Gérer les clics
        holder.cardView.setOnClickListener {
            onNoteClick(note)
        }

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            onNoteChecked(note, isChecked)
            updateTaskAppearance(holder, isChecked)
        }
    }

    /**
     * Met à jour l'apparence d'une tâche (barrée ou non)
     */
    private fun updateTaskAppearance(holder: NoteViewHolder, isCompleted: Boolean) {
        holder.checkBox.visibility = View.VISIBLE // Toujours afficher la checkbox
        holder.iconEdit.visibility = View.GONE    // Cacher l'icône de crayon

        if (isCompleted) {
            holder.titleText.paintFlags = holder.titleText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.contentText.paintFlags = holder.contentText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            holder.titleText.paintFlags = holder.titleText.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.contentText.paintFlags = holder.contentText.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
    }

    override fun getItemCount(): Int = notes.size

    fun sortByDate() {
        notes.sortBy { it.dueDate }
        notifyDataSetChanged()
    }

    fun sortByPriority() {
        notes.sortBy { it.priority }
        notifyDataSetChanged()
    }
}
