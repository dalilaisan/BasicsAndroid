package com.example.dalila.androidbasics;


import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.example.dalila.androidbasics.adapters.NotesRecyclerAdapter;
import com.example.dalila.androidbasics.models.Note;
import com.example.dalila.androidbasics.persistence.NoteRepository;
import com.example.dalila.androidbasics.util.VerticalSpacingItemDecorator;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NotesRecyclerAdapter.OnNoteListener,
        View.OnClickListener {

    private static final String TAG = "MainActivity";

    //UI components
    private RecyclerView mRecyclerView;

    //vars
    private ArrayList<Note> mNotes = new ArrayList<>();
    private NotesRecyclerAdapter mNotesRecyclerAdapter;
    private NoteRepository mNoteRepository;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.recycler_view);

        findViewById(R.id.fab).setOnClickListener(this);

        mNoteRepository = new NoteRepository(this);

        initRecyclerView();
        retrieveNotes();

        //setting the title for the actionbar
        setSupportActionBar((Toolbar) findViewById(R.id.notes_toolbar));
        setTitle("Notes");
    }

    /*any of the calls using liveDtaa are by default asynchronous, so they operate on a background thread,
    which is exactly what we need because we cannot do db transactions on the main thread
     */
    private void retrieveNotes() {
        /*the way LiveData objects work is that you call a method that returns a
        LiveDtaa object and then you observe changes to it, so any time there is a change
        done to the LiveDta object, this method will trigger*/
        mNoteRepository.retrieveNotesTask().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(@Nullable List<Note> notes) {
                /*any time a change is made to the LiveDtaa object, which is returned from the database,
                this onChanged method will trigger (it will also trigger as soon as we attach the observer,
                so that's how we're able to retrieve a list objects right away when the method retrieveNotes is called)
                 */
                /*
                basically it's like we are attaching a listener to the database, so any time
                new data is inserted/deleted/updated, this observer will call the onChange method
                and it's going to re-query that list of notes (clear them if there's already noted and
                add them all again and then it's gonna tell the adapter that the dataset has changed)
                 */
                if (mNotes.size() > 0) {
                    mNotes.clear();
                }
                if (notes != null) {
                    mNotes.addAll(notes);
                }
                mNotesRecyclerAdapter.notifyDataSetChanged();
            }
        });
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

        //attach the item touch helper to recycler view (for swiping the items)
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecyclerView);

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

    private void deleteNote(Note note) {
        mNotes.remove(note);
        mNotesRecyclerAdapter.notifyDataSetChanged();

        mNoteRepository.deleteNotesTask(note);
    }

    //we ue the itemtouchhelper to move/swipe items in the recycler view
    private ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        //the onMove method is used to rearrange the items
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
            return false;
        }

        //swiping on a note will remove the note
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            deleteNote(mNotes.get(viewHolder.getAdapterPosition()));
        }
    };
}
