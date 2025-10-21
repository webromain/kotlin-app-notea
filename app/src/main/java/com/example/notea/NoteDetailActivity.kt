package com.example.notea

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.notea.data.AppDatabase
import com.example.notea.models.Note
import com.example.notea.models.Priority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class NoteDetailActivity : AppCompatActivity() {

    // Vues
    private lateinit var btnBack: ImageButton
    private lateinit var btnMenu: ImageButton
    private lateinit var btnPriority: TextView
    private lateinit var editTitle: EditText
    private lateinit var editContent: EditText
    private lateinit var btnDate: TextView

    // Base de données
    private val db by lazy { AppDatabase.getDatabase(this) }

    // Note en cours d'édition
    private var currentNote: Note? = null

    // Données temporaires
    private var selectedPriority: Priority = Priority.NORMAL
    private var selectedDate: Date? = null
    private val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale.FRANCE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_detail)

        initViews()
        loadNoteFromDb()
        setupListeners()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        btnMenu = findViewById(R.id.btnMenu)
        btnPriority = findViewById(R.id.btnPriority)
        editTitle = findViewById(R.id.editTitle)
        editContent = findViewById(R.id.editContent)
        btnDate = findViewById(R.id.btnDate)
    }

    private fun loadNoteFromDb() {
        val noteId = intent.getStringExtra("NOTE_ID")
        if (noteId != null) {
            lifecycleScope.launch(Dispatchers.IO) { // Lancer en arrière-plan
                val note = db.noteDao().getNoteById(noteId)

                withContext(Dispatchers.Main) { // Revenir sur le thread principal pour l'UI
                    currentNote = note
                    if (note != null) {
                        // Mode édition : remplir les champs
                        editTitle.setText(note.title)
                        editContent.setText(note.content)
                        selectedPriority = note.priority
                        selectedDate = note.dueDate
                        updatePriorityButton()
                        updateDateButton()
                    } else {
                        // La note n'a pas été trouvée, on passe en mode création
                        updatePriorityButton()
                    }
                }
            }
        } else {
            // Mode création
            updatePriorityButton()
        }
    }

    private fun setupListeners() {
        btnBack.setOnClickListener { saveAndFinish() }
        btnMenu.setOnClickListener { showOptionsMenu() }
        btnPriority.setOnClickListener { showPriorityDialog() }
        btnDate.setOnClickListener { showDatePicker() }
    }

    private fun updatePriorityButton() {
        btnPriority.text = "Priority > ${getPriorityText(selectedPriority)}"
    }

    private fun updateDateButton() {
        // Rendu plus sûr pour éviter les crashs
        val date = selectedDate
        btnDate.text = if (date != null) {
            "Date > ${dateFormat.format(date)}"
        } else {
            "Date > Non définie"
        }
    }

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

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val date = selectedDate
        if (date != null) {
            calendar.time = date
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

    private fun showOptionsMenu() {
        if (currentNote != null) {
            AlertDialog.Builder(this)
                .setTitle("Options")
                .setItems(arrayOf("Supprimer la note")) { _, _ -> deleteNote() }
                .setNegativeButton("Annuler", null)
                .show()
        }
    }

    private fun deleteNote() {
        val noteToDelete = currentNote ?: return

        AlertDialog.Builder(this)
            .setTitle("Supprimer la note")
            .setMessage("Êtes-vous sûr de vouloir supprimer cette note ?")
            .setPositiveButton("Supprimer") { _, _ ->
                lifecycleScope.launch(Dispatchers.IO) {
                    db.noteDao().delete(noteToDelete)
                    withContext(Dispatchers.Main) {
                        val resultIntent = Intent()
                        resultIntent.putExtra("DELETED", true)
                        resultIntent.putExtra("NOTE_ID", noteToDelete.id)
                        setResult(Activity.RESULT_OK, resultIntent)
                        finish()
                    }
                }
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    private fun saveAndFinish() {
        val title = editTitle.text.toString().trim()
        val content = editContent.text.toString().trim()

        if (title.isEmpty()) {
            Toast.makeText(this, "Le titre ne peut pas être vide", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val noteToSave = if (currentNote == null) {
                Note(
                    title = title,
                    content = content,
                    priority = selectedPriority,
                    dueDate = selectedDate
                )
            } else {
                currentNote!!.copy(
                    title = title,
                    content = content,
                    priority = selectedPriority,
                    dueDate = selectedDate
                )
            }

            db.noteDao().insertOrUpdate(noteToSave)

            withContext(Dispatchers.Main) {
                val resultIntent = Intent()
                resultIntent.putExtra("NOTE_ID", noteToSave.id)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }
    }

    private fun getPriorityText(priority: Priority): String {
        return when (priority) {
            Priority.URGENT -> "Urgent !"
            Priority.HIGH -> "High"
            Priority.NORMAL -> "Normal"
            Priority.LOW -> "Low"
        }
    }

    override fun onBackPressed() {
        saveAndFinish()
        super.onBackPressed()
    }
}
