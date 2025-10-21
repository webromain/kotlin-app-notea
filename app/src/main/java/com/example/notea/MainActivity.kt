package com.example.notea

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.SearchView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notea.adapters.NoteAdapter
import com.example.notea.data.AppDatabase
import com.example.notea.models.Note
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NoteAdapter
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var searchView: SearchView
    private lateinit var btnSettings: ImageButton
    private lateinit var btnMenu: ImageButton

    private val db by lazy { AppDatabase.getDatabase(this) }
    private val filteredNotes = mutableListOf<Note>()

    private val noteDetailLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // La liste des notes a changé, on la recharge
            loadNotesFromDb()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupRecyclerView()
        setupListeners()
        loadNotesFromDb()
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerView)
        fabAdd = findViewById(R.id.fabAdd)
        searchView = findViewById(R.id.searchView)
        btnSettings = findViewById(R.id.btnSettings)
        btnMenu = findViewById(R.id.btnMenu)
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = NoteAdapter(
            filteredNotes,
            onNoteClick = { note -> openNoteDetail(note) },
            onNoteChecked = { note, isChecked -> toggleNoteCompletion(note, isChecked) }
        )
        recyclerView.adapter = adapter
    }

    private fun setupListeners() {
        fabAdd.setOnClickListener { openNoteDetail(null) }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                filterNotes(newText ?: "")
                return true
            }
        })

        btnSettings.setOnClickListener { showSortOptions() }
        btnMenu.setOnClickListener { /* À implémenter */ }
    }

    private fun openNoteDetail(note: Note?) {
        val intent = Intent(this, NoteDetailActivity::class.java)
        if (note != null) {
            intent.putExtra("NOTE_ID", note.id)
        }
        noteDetailLauncher.launch(intent)
    }

    private fun toggleNoteCompletion(note: Note, isCompleted: Boolean) {
        val updatedNote = note.copy(isCompleted = isCompleted)
        lifecycleScope.launch {
            db.noteDao().insertOrUpdate(updatedNote)
            loadNotesFromDb()
        }
    }

    private fun filterNotes(query: String) {
        lifecycleScope.launch {
            val allNotes = db.noteDao().getAllNotes()
            filteredNotes.clear()
            if (query.isEmpty()) {
                filteredNotes.addAll(allNotes)
            } else {
                val lowercaseQuery = query.lowercase()
                filteredNotes.addAll(
                    allNotes.filter {
                        it.title.lowercase().contains(lowercaseQuery) ||
                                it.content.lowercase().contains(lowercaseQuery)
                    }
                )
            }
            adapter.notifyDataSetChanged()
        }
    }

    private fun showSortOptions() {
        val options = arrayOf("Trier par date", "Trier par priorité")
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Trier par")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> adapter.sortByDate()
                    1 -> adapter.sortByPriority()
                }
            }
            .show()
    }

    private fun loadNotesFromDb() {
        lifecycleScope.launch {
            val notes = db.noteDao().getAllNotes()
            filteredNotes.clear()
            filteredNotes.addAll(notes)
            adapter.notifyDataSetChanged()
        }
    }
}
