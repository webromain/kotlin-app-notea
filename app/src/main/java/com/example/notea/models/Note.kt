package com.example.notea.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.notea.data.Converters
import java.util.*

/**
 * Énumération pour les niveaux de priorité des notes
 * Chaque priorité a une couleur associée pour l'affichage
 */
enum class Priority {
    URGENT,   // Rouge - Priorité maximale
    HIGH,     // Orange - Haute priorité
    NORMAL,   // Bleu - Priorité normale
    LOW       // Vert - Basse priorité
}

/**
 * Classe de données représentant une Note
 * C'est une entité Room, ce qui signifie qu'elle sera stockée dans une table SQL.
 *
 * @param id Identifiant unique de la note
 * @param title Titre de la note
 * @param content Contenu/description de la note
 * @param priority Niveau de priorité (URGENT, HIGH, NORMAL, LOW)
 * @param dueDate Date d'échéance optionnelle
 * @param isCompleted État de complétion de la note
 * @param createdAt Date de création de la note
 */
@Entity(tableName = "notes")
@TypeConverters(Converters::class)
data class Note(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(), // Génère un ID unique automatiquement
    var title: String,
    var content: String,
    var priority: Priority = Priority.NORMAL,
    var dueDate: Date? = null, // Null si pas de date d'échéance
    var isCompleted: Boolean = false,
    val createdAt: Date = Date() // Date de création automatique
) {

    /**
     * Retourne la couleur de fond selon la priorité
     * Utilisé pour l'affichage dans la liste
     */
    fun getBackgroundColor(): String {
        return when (priority) {
            Priority.URGENT -> "#FFE5E5"  // Rose clair pour urgent
            Priority.HIGH -> "#FFF3E0"    // Orange clair pour high
            Priority.NORMAL -> "#E3F2FD"  // Bleu clair pour normal
            Priority.LOW -> "#E8F5E9"     // Vert clair pour low
        }
    }

    /**
     * Retourne la couleur du badge de priorité
     */
    fun getPriorityColor(): String {
        return when (priority) {
            Priority.URGENT -> "#FFCDD2"  // Rose pour urgent
            Priority.HIGH -> "#FFE0B2"    // Orange pour high
            Priority.NORMAL -> "#BBDEFB"  // Bleu pour normal
            Priority.LOW -> "#C8E6C9"     // Vert pour low
        }
    }

    /**
     * Retourne le texte de la priorité en français
     */
    fun getPriorityText(): String {
        return when (priority) {
            Priority.URGENT -> "Urgent !"
            Priority.HIGH -> "High"
            Priority.NORMAL -> "Normal"
            Priority.LOW -> "Low"
        }
    }
}