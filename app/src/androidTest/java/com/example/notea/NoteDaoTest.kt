package com.example.notea

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.notea.data.AppDatabase
import com.example.notea.data.NoteDao
import com.example.notea.models.Note
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**Ce test vérifie que les opérations de base de données (lecture, écriture, suppression) fonctionnent comme prévu.*/
@RunWith(AndroidJUnit4::class)
class NoteDaoTest {

    private lateinit var noteDao: NoteDao
    private lateinit var db: AppDatabase

    /**Créer une base de données en mémoire avant chaque test. Une base de données en mémoire est rapide et est détruite après le test, ce qui garantit que les tests sont bien isolés.*/
    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        noteDao = db.noteDao()
    }

    /**Fermer la base de données après chaque test pour libérer les ressources.*/
    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    /**Test complet : insertion, lecture et vérification. On utilise runBlocking car les fonctions du DAO sont des `suspend` functions.*/
    @Test
    @Throws(Exception::class)
    fun insertAndReadNote() = runBlocking {
        // Étape 2 : Créer une note et l'insérer dans la base de données.
        val note = Note(id = "test-id-1", title = "Test Note", content = "Contenu de test")
        noteDao.insertOrUpdate(note)

        // Étape 3 : Lire la note depuis la base de données en utilisant son ID.
        val retrievedNote = noteDao.getNoteById("test-id-1")

        // Vérifier que la note lue est bien la même que celle qu'on a insérée.
        assertEquals(note.title, retrievedNote?.title)
    }

    /**Test de la suppression*/
    @Test
    @Throws(Exception::class)
    fun deleteNote() = runBlocking {
        // Insérer une note d'abord.
        val note = Note(id = "test-id-2", title = "Note à supprimer", content = "Contenu")
        noteDao.insertOrUpdate(note)

        // La supprimer.
        noteDao.delete(note)

        // Essayer de la relire.
        val retrievedNote = noteDao.getNoteById("test-id-2")

        // Vérifier qu'elle n'existe plus (le résultat doit être null).
        assertNull(retrievedNote)
    }
}