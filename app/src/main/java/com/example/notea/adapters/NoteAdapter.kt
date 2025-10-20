package com.example.notea.adapters

import android.graphics.Color
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

/**
 * Adaptateur pour afficher la liste des notes dans un RecyclerView
 * Il transforme les données (liste de Notes) en vues affichables
 *
 * @param notes Liste des notes à afficher
 * @param onNoteClick Fonction appelée quand on clique sur une note
 * @param onNoteChecked Fonction appelée quand on coche/décoche une note
 */
class NoteAdapter(
    private var notes: MutableList<Note>,
    private val onNoteClick: (Note) -> Unit,
    private val onNoteChecked: (Note, Boolean) -> Unit
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    // Format de date pour l'affichage (ex: 10/11/2025)
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    /**
     * ViewHolder : contient les références aux vues d'un item de la liste
     * Cela évite de chercher les vues à chaque fois (optimisation)
     */
    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.cardView)
        val iconEdit: ImageView = itemView.findViewById(R.id.iconEdit)
        val titleText: TextView = itemView.findViewById(R.id.titleText)
        val contentText: TextView = itemView.findViewById(R.id.contentText)
        val priorityBadge: TextView = itemView.findViewById(R.id.priorityBadge)
        val dueDateText: TextView = itemView.findViewById(R.id.dueDateText)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)
    }

    /**
     * Crée une nouvelle vue pour un item de la liste
     * Appelé par le RecyclerView quand il a besoin d'une nouvelle vue
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    /**
     * Lie les données d'une note à une vue
     * Appelé pour chaque item visible dans la liste
     */
    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]

        // Définir le titre et le contenu
        holder.titleText.text = note.title
        holder.contentText.text = note.content

        // Définir la couleur de fond selon la priorité
        holder.cardView.setCardBackgroundColor(Color.parseColor(note.getBackgroundColor()))

        // Configurer le badge de priorité
        holder.priorityBadge.text = note.getPriorityText()
        holder.priorityBadge.setBackgroundColor(Color.parseColor(note.getPriorityColor()))

        // Afficher la date d'échéance si elle existe
        if (note.dueDate != null) {
            holder.dueDateText.visibility = View.VISIBLE
            holder.dueDateText.text = dateFormat.format(note.dueDate!!)
        } else {
            holder.dueDateText.visibility = View.GONE // Masquer si pas de date
        }

        // Gérer l'état de la checkbox
        holder.checkBox.isChecked = note.isCompleted

        // Si la note est complétée, changer l'icône
        if (note.isCompleted) {
            holder.checkBox.visibility = View.VISIBLE
            holder.iconEdit.visibility = View.GONE
        } else {
            holder.checkBox.visibility = View.GONE
            holder.iconEdit.visibility = View.VISIBLE
        }

        // Gérer le clic sur la note entière
        holder.cardView.setOnClickListener {
            onNoteClick(note)
        }

        // Gérer le changement d'état de la checkbox
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            onNoteChecked(note, isChecked)
        }
    }

    /**
     * Retourne le nombre total d'items dans la liste
     */
    override fun getItemCount(): Int = notes.size

    /**
     * Met à jour la liste des notes et rafraîchit l'affichage
     */
    fun updateNotes(newNotes: List<Note>) {
        notes.clear()
        notes.addAll(newNotes)
        notifyDataSetChanged() // Indique au RecyclerView de se rafraîchir
    }

    /**
     * Trie les notes par date d'échéance
     */
    fun sortByDate() {
        notes.sortBy { it.dueDate }
        notifyDataSetChanged()
    }

    /**
     * Trie les notes par priorité (Urgent -> High -> Normal -> Low)
     */
    fun sortByPriority() {
        notes.sortBy { it.priority }
        notifyDataSetChanged()
    }
}