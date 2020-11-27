package com.example.notessample.listeners;

import com.example.notessample.entities.Note;

public interface NotesListener {
    void onNoteClicked(Note note, int position);

}
