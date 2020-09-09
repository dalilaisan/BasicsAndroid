package com.example.dalila.androidbasics;


import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.example.dalila.androidbasics.adapters.NotesRecyclerAdapter;
import com.example.dalila.androidbasics.models.Note;
import com.example.dalila.androidbasics.util.VerticalSpacingItemDecorator;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NotesRecyclerAdapter.OnNoteListener,
        View.OnClickListener {

    private static final String TAG = "MainActivity";

    //UI components
    private RecyclerView mRecyclerView;

    //vars
    private ArrayList<Note> mNotes = new ArrayList<>();
    private NotesRecyclerAdapter mNotesRecyclerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.recycler_view);

        findViewById(R.id.fab).setOnClickListener(this);

        initRecyclerView();
        insertFakeNotes();

        //setting the title for the actionbar
        setSupportActionBar((Toolbar) findViewById(R.id.notes_toolbar));
        setTitle("Notes");
    }


    private void insertFakeNotes() {
        for (int i =0; i<1000; i++)  {
            Note note = new Note();
            note.setTitle("title # " + i);
            note.setContent("content # " + i);
            note.setTimeStamp("Jan 2019");
            mNotes.add(note);
        }
        //if we do not call notify data set changed, no notes will show in the list
        mNotesRecyclerAdapter.notifyDataSetChanged();
    }

    //method for setting up the recycler view
    private void initRecyclerView() {
        //every recycler view always needs some type of layout manager, in this case we are building a linear recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        //add the item decorator (a utility class which we are going to use to add spacing between list items)
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(10);
        //set that decorator to the recycler view
        mRecyclerView.addItemDecoration(itemDecorator);

        //instantiate the adapter
        mNotesRecyclerAdapter = new NotesRecyclerAdapter(mNotes, this);
        //lastly, set the adapter to the recycler view
        mRecyclerView.setAdapter(mNotesRecyclerAdapter);

    }

    //we use this interface to interpret the click made on the list item
    //and then we use this method in the activity to send the position of that clicked item
    @Override
    public void onNoteClick(int position) {
        Log.d(TAG, "onNoteClick: called " + position);

        Intent intent = new Intent(this, NoteActivity.class);
        intent.putExtra("selected_note", mNotes.get(position));
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, NoteActivity.class);
        startActivity(intent);
    }
}
