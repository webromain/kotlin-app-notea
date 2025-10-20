package com.example.notea

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notea.adapters.NoteAdapter
import com.example.notea.models.Note
import com.example.notea.models.Priority
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

/**
 * Activité principale de l'application
 * Affiche la liste des notes et permet de naviguer vers l'édition
 */
class MainActivity : AppCompatActivity() {

    // Vues de l'interface
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NoteAdapter
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var searchView: SearchView
    private lateinit var btnSettings: ImageButton
    private lateinit var btnMenu: ImageButton

    // Liste de toutes les notes
    private val allNotes = mutableListOf<Note>()

    // Liste des notes filtrées (pour la recherche)
    private val filteredNotes = mutableListOf<Note>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialiser les vues
        initViews()

        // Ajouter des notes d'exemple pour tester
        loadSampleNotes()

        // Configurer le RecyclerView
        setupRecyclerView()

        // Configurer les listeners (écouteurs d'événements)
        setupListeners()
    }

    /**
     * Initialise toutes les vues en les liant aux IDs du layout XML
     */
    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerView)
        fabAdd = findViewById(R.id.fabAdd)
        searchView = findViewById(R.id.searchView)
        btnSettings = findViewById(R.id.btnSettings)
        btnMenu = findViewById(R.id.btnMenu)
    }

    /**
     * Configure le RecyclerView avec son adaptateur et son gestionnaire de disposition
     */
    private fun setupRecyclerView() {
        // LinearLayoutManager : dispose les items en liste verticale
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Créer l'adaptateur avec les callbacks
        adapter = NoteAdapter(
            filteredNotes,
            onNoteClick = { note -> openNoteDetail(note) },
            onNoteChecked = { note, isChecked -> toggleNoteCompletion(note, isChecked) }
        )

        recyclerView.adapter = adapter

        // Copier toutes les notes dans la liste filtrée au départ
        filteredNotes.addAll(allNotes)
    }

    /**
     * Configure tous les listeners (écouteurs d'événements)
     */
    private fun setupListeners() {
        // Bouton d'ajout : ouvre l'écran de création de note
        fabAdd.setOnClickListener {
            openNoteDetail(null) // null = nouvelle note
        }

        // Barre de recherche
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            // Appelé quand l'utilisateur valide la recherche
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            // Appelé à chaque caractère tapé
            override fun onQueryTextChange(newText: String?): Boolean {
                filterNotes(newText ?: "")
                return true
            }
        })

        // Bouton paramètres (à implémenter selon vos besoins)
        btnSettings.setOnClickListener {
            // Ouvrir les paramètres ou afficher un menu pour trier
            showSortOptions()
        }

        // Bouton menu
        btnMenu.setOnClickListener {
            // Afficher le menu (à implémenter)
        }
    }

    /**
     * Ouvre l'écran de détail/édition d'une note
     * @param note La note à éditer, ou null pour créer une nouvelle note
     */
    private fun openNoteDetail(note: Note?) {
        val intent = Intent(this, NoteDetailActivity::class.java)
        if (note != null) {
            intent.putExtra("NOTE", note) // Passer la note à l'activité suivante
        }
        startActivityForResult(intent, REQUEST_NOTE_DETAIL)
    }

    /**
     * Change l'état de complétion d'une note
     */
    private fun toggleNoteCompletion(note: Note, isCompleted: Boolean) {
        note.isCompleted = isCompleted
        adapter.notifyDataSetChanged()
        // Ici, vous pourriez sauvegarder dans une base de données
    }

    /**
     * Filtre les notes selon le texte de recherche
     */
    private fun filterNotes(query: String) {
        filteredNotes.clear()

        if (query.isEmpty()) {
            // Si la recherche est vide, afficher toutes les notes
            filteredNotes.addAll(allNotes)
        } else {
            // Filtrer les notes dont le titre ou le contenu contient le texte
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

    /**
     * Affiche les options de tri
     */
    private fun showSortOptions() {
        // Créer un dialogue avec les options : Trier par date, Trier par priorité
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

    /**
     * Charge des notes d'exemple pour tester l'application
     */
    private fun loadSampleNotes() {
        // Créer des dates d'exemple
        val calendar = Calendar.getInstance()

        calendar.set(2025, 10, 10) // 10 novembre 2025
        val date1 = calendar.time

        // Ajouter des notes d'exemple
        allNotes.add(
            Note(
                title = "Aller à la salle",
                content = "Faire tous mes exercices jusqu'à l'échec",
                priority = Priority.URGENT,
                dueDate = date1
            )
        )

        allNotes.add(
            Note(
                title = "Aller à la salle",
                content = "Faire tous mes exercices jusqu'à l'échec",
                priority = Priority.HIGH
            )
        )

        allNotes.add(
            Note(
                title = "Aller à la salle",
                content = "Faire tous mes exercices jusqu'à l'échec",
                priority = Priority.LOW
            )
        )

        allNotes.add(
            Note(
                title = "Aller à la salle",
                content = "Faire tous mes exercices jusqu'à l'échec",
                priority = Priority.NORMAL,
                isCompleted = true
            )
        )
    }

    /**
     * Appelé quand on revient de l'écran de détail
     * Permet de récupérer la note créée/modifiée
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_NOTE_DETAIL && resultCode == RESULT_OK) {
            val note = data?.getSerializableExtra("NOTE") as? Note
            val isDeleted = data?.getBooleanExtra("DELETED", false) ?: false

            if (isDeleted && note != null) {
                // Supprimer la note
                allNotes.removeAll { it.id == note.id }
                filterNotes(searchView.query.toString())
            } else if (note != null) {
                // Ajouter ou mettre à jour la note
                val existingIndex = allNotes.indexOfFirst { it.id == note.id }
                if (existingIndex != -1) {
                    allNotes[existingIndex] = note
                } else {
                    allNotes.add(0, note) // Ajouter au début
                }
                filterNotes(searchView.query.toString())
            }
        }
    }

    companion object {
        private const val REQUEST_NOTE_DETAIL = 1
    }
}