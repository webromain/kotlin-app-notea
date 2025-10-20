package com.example.notea

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.notea.models.Note
import com.example.notea.models.Priority
import java.text.SimpleDateFormat
import java.util.*

/**
 * Activité pour créer ou éditer une note
 */
class NoteDetailActivity : AppCompatActivity() {

    // Vues de l'interface
    private lateinit var btnBack: ImageButton
    private lateinit var btnMenu: ImageButton
    private lateinit var btnPriority: TextView
    private lateinit var editTitle: EditText
    private lateinit var editContent: EditText
    private lateinit var btnDate: TextView

    // Note en cours d'édition (null si création)
    private var currentNote: Note? = null

    // Priorité sélectionnée
    private var selectedPriority: Priority = Priority.NORMAL

    // Date d'échéance sélectionnée
    private var selectedDate: Date? = null

    // Format de date
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_detail)

        // Initialiser les vues
        initViews()

        // Charger la note si elle existe
        loadNote()

        // Configurer les listeners
        setupListeners()
    }

    /**
     * Initialise toutes les vues
     */
    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        btnMenu = findViewById(R.id.btnMenu)
        btnPriority = findViewById(R.id.btnPriority)
        editTitle = findViewById(R.id.editTitle)
        editContent = findViewById(R.id.editContent)
        btnDate = findViewById(R.id.btnDate)
    }

    /**
     * Charge la note passée en paramètre (si elle existe)
     */
    private fun loadNote() {
        currentNote = intent.getSerializableExtra("NOTE") as? Note

        if (currentNote != null) {
            // Mode édition : remplir les champs avec les données existantes
            editTitle.setText(currentNote!!.title)
            editContent.setText(currentNote!!.content)
            selectedPriority = currentNote!!.priority
            selectedDate = currentNote!!.dueDate

            updatePriorityButton()
            updateDateButton()
        } else {
            // Mode création : champs vides
            updatePriorityButton()
        }
    }

    /**
     * Configure tous les listeners
     */
    private fun setupListeners() {
        // Bouton retour : sauvegarder et retourner
        btnBack.setOnClickListener {
            saveAndFinish()
        }

        // Bouton menu : afficher les options
        btnMenu.setOnClickListener {
            showOptionsMenu()
        }

        // Bouton priorité : changer la priorité
        btnPriority.setOnClickListener {
            showPriorityDialog()
        }

        // Bouton date : sélectionner une date
        btnDate.setOnClickListener {
            showDatePicker()
        }
    }

    /**
     * Met à jour l'affichage du bouton de priorité
     */
    private fun updatePriorityButton() {
        btnPriority.text = "Priority > ${getPriorityText(selectedPriority)}"
    }

    /**
     * Met à jour l'affichage du bouton de date
     */
    private fun updateDateButton() {
        if (selectedDate != null) {
            btnDate.text = "Date > ${dateFormat.format(selectedDate!!)}"
        } else {
            btnDate.text = "Date > Non définie"
        }
    }

    /**
     * Affiche un dialogue pour choisir la priorité
     */
    private fun showPriorityDialog() {
        val priorities = arrayOf("Urgent !", "High", "Normal", "Low")
        val priorityValues = arrayOf(Priority.URGENT, Priority.HIGH, Priority.NORMAL, Priority.LOW)

        AlertDialog.Builder(this)
            .setTitle("Choisir une priorité")
            .setItems(priorities) { _, which ->
                selectedPriority = priorityValues[which]
                updatePriorityButton()
            }
            .show()
    }

    /**
     * Affiche un sélecteur de date
     */
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        if (selectedDate != null) {
            calendar.time = selectedDate!!
        }

        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                selectedDate = calendar.time
                updateDateButton()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    /**
     * Affiche le menu d'options (supprimer, etc.)
     */
    private fun showOptionsMenu() {
        if (currentNote != null) {
            AlertDialog.Builder(this)
                .setTitle("Options")
                .setItems(arrayOf("Supprimer la note")) { _, _ ->
                    deleteNote()
                }
                .setNegativeButton("Annuler", null)
                .show()
        }
    }

    /**
     * Supprime la note actuelle
     */
    private fun deleteNote() {
        AlertDialog.Builder(this)
            .setTitle("Supprimer la note")
            .setMessage("Êtes-vous sûr de vouloir supprimer cette note ?")
            .setPositiveButton("Supprimer") { _, _ ->
                val resultIntent = Intent()
                resultIntent.putExtra("NOTE", currentNote)
                resultIntent.putExtra("DELETED", true)
                setResult(RESULT_OK, resultIntent)
                finish()
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    /**
     * Sauvegarde la note et retourne à l'écran principal
     */
    private fun saveAndFinish() {
        val title = editTitle.text.toString().trim()
        val content = editContent.text.toString().trim()

        // Vérifier que le titre n'est pas vide
        if (title.isEmpty()) {
            Toast.makeText(this, "Le titre ne peut pas être vide", Toast.LENGTH_SHORT).show()
            return
        }

        // Créer ou mettre à jour la note
        val note = currentNote ?: Note(
            title = title,
            content = content,
            priority = selectedPriority,
            dueDate = selectedDate
        )

        // Si c'est une mise à jour, modifier les propriétés
        if (currentNote != null) {
            note.title = title
            note.content = content
            note.priority = selectedPriority
            note.dueDate = selectedDate
        }

        // Retourner la note à l'activité principale
        val resultIntent = Intent()
        resultIntent.putExtra("NOTE", note)
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    /**
     * Retourne le texte de la priorité en français
     */
    private fun getPriorityText(priority: Priority): String {
        return when (priority) {
            Priority.URGENT -> "Urgent !"
            Priority.HIGH -> "High"
            Priority.NORMAL -> "Normal"
            Priority.LOW -> "Low"
        }
    }

    /**
     * Gérer le bouton retour système
     */
    override fun onBackPressed() {
        saveAndFinish()
    }
}