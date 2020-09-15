package com.example.dalila.androidbasics.persistence;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import com.example.dalila.androidbasics.async.DeleteAsyncTask;
import com.example.dalila.androidbasics.async.InsertAsyncTask;
import com.example.dalila.androidbasics.async.UpdateAsyncTask;
import com.example.dalila.androidbasics.models.Note;

import java.util.List;

public class NoteRepository {

    private NoteDatabase mNoteDatabase;

    public NoteRepository(Context context) {
        mNoteDatabase = NoteDatabase.getInstance(context);
    }

    public void insertNoteTask(Note note){
        new InsertAsyncTask(mNoteDatabase.getNoteDao()).execute(note);
    }

    public void updateNoteTask(Note note) {
        new UpdateAsyncTask(mNoteDatabase.getNoteDao()).execute(note);
    }

    public LiveData<List<Note>> retrieveNotesTask() {
        return mNoteDatabase.getNoteDao().getNotes();
    }

    public void deleteNotesTask(Note note){
        new DeleteAsyncTask(mNoteDatabase.getNoteDao()).execute(note);
    }

}
