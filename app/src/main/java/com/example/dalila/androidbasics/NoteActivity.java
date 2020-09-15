package com.example.dalila.androidbasics;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.dalila.androidbasics.models.Note;
import com.example.dalila.androidbasics.persistence.NoteRepository;
import com.example.dalila.androidbasics.util.LinedEditText;
import com.example.dalila.androidbasics.util.Utility;

public class NoteActivity extends AppCompatActivity
        implements View.OnTouchListener,
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener,
        View.OnClickListener,
        TextWatcher {
    private static final String TAG = "NoteActivity";

    private static final int EDIT_MODE_ENABLED = 1;
    private static final int EDIT_MODE_DISABLED = 0;

    private LinedEditText mLinedEditText;
    private EditText mEditTitle;
    private TextView mViewTitle;
    private RelativeLayout mCheckContainer, mBackArrowContainer;
    private ImageButton mCheck, mBackArrow;


    private boolean mIsNewNote;
    private Note mInitialNote;
    private GestureDetector mGestureDetector;
    private int mMode;

    private NoteRepository mNoteRepository;
    //the only time we save a change ona note is when the title is changed or when the edit text is changed
    private Note mFinalNote;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        mLinedEditText = findViewById(R.id.note_text);
        mEditTitle = findViewById(R.id.note_edit_title);
        mViewTitle = findViewById(R.id.note_text_title);
        mCheckContainer = findViewById(R.id.check_container);
        mBackArrowContainer = findViewById(R.id.back_arrow_container);
        mCheck = findViewById(R.id.toolbar_check);
        mBackArrow = findViewById(R.id.toolbar_back_arrow);

        mNoteRepository = new NoteRepository(this);

        if (getIncomingIntent()) {
            //it is a new note; EDIT MODE
            setNewNoteProperties();
            enableEditMode();
        }
        else{
            //it is not a new note; VIEW MODE
            setNoteProperties();
            disableContentInteraction();
        }

        setListeners();
    }

    //when we enter the activity, we check whether it is a new note we are viewing
    public boolean getIncomingIntent() {
        if (getIntent().hasExtra("selected_note")){
            mInitialNote = getIntent().getParcelableExtra("selected_note");

            /*
            we have to make a new Note object for mFinalNote, otherwise mInitiaNote
            and mFinalNote are going to be assigned the same object in memory and thus
            always be equal when we compare them in the saveChanges method
             */
            mFinalNote = new Note();
            mFinalNote.setTitle(mInitialNote.getTitle());
            mFinalNote.setContent(mInitialNote.getContent());
            mFinalNote.setTimeStamp(mInitialNote.getTimeStamp());
            mFinalNote.setId(mInitialNote.getId());

            mMode = EDIT_MODE_DISABLED;
            mIsNewNote = false;
            return false;
        }
        mMode = EDIT_MODE_ENABLED;
        mIsNewNote = true;
        return true;
    }

    //we want to disable all interactions on the lined edit text, unless edit mode
    private void disableContentInteraction() {
        mLinedEditText.setKeyListener(null);
        mLinedEditText.setFocusable(false);
        mLinedEditText.setFocusableInTouchMode(false);
        mLinedEditText.setCursorVisible(false);
        mLinedEditText.clearFocus();
    }
    private void enableContentInteraction() {
        mLinedEditText.setKeyListener(new EditText(this).getKeyListener());
        mLinedEditText.setFocusable(true);
        mLinedEditText.setFocusableInTouchMode(true);
        mLinedEditText.setCursorVisible(true);
        mLinedEditText.requestFocus();
    }

    private void enableEditMode() {
        mBackArrowContainer.setVisibility(View.GONE);
        mCheckContainer.setVisibility(View.VISIBLE);

        mViewTitle.setVisibility(View.GONE);
        mEditTitle.setVisibility(View.VISIBLE);

        mMode = EDIT_MODE_ENABLED;

        enableContentInteraction();
    }

    private void disableEditMode() {
        mBackArrowContainer.setVisibility(View.VISIBLE);
        mCheckContainer.setVisibility(View.GONE);

        mViewTitle.setVisibility(View.VISIBLE);
        mEditTitle.setVisibility(View.GONE);

        mMode = EDIT_MODE_DISABLED;

        disableContentInteraction();

        //compare if the final note is equal to the initial note
        String temp = mLinedEditText.getText().toString();
        //remove the new lined and blank spaces
        temp = temp.replace("\n", "");
        temp = temp.replace(" ", "");
        if(temp.length() > 0) {
            mFinalNote.setTitle(mEditTitle.getText().toString());
            mFinalNote.setContent(mLinedEditText.getText().toString());
            String timestamp = Utility.getCurrentTimestamp();
            mFinalNote.setTimeStamp(timestamp);

            if (mFinalNote.getContent().equals(mInitialNote.getContent())
                    || mFinalNote.getTitle().equals(mInitialNote.getTitle())){
                saveChanges();
            }
        }
    }

    private void saveChanges() {
        if (mIsNewNote) {
            saveNewNote();
        }
        else {
            updateNote();
        }
    }

    private void updateNote() {
        mNoteRepository.updateNoteTask(mFinalNote);
    }

    private void saveNewNote() {
        mNoteRepository.insertNoteTask(mFinalNote);
    }

    private void setNoteProperties() {
        mViewTitle.setText(mInitialNote.getTitle());
        mEditTitle.setText(mInitialNote.getTitle());
        mLinedEditText.setText(mInitialNote.getContent());
    }

    private void setNewNoteProperties() {
        mViewTitle.setText("Note title");
        mEditTitle.setText("Note title");

        mInitialNote = new Note();
        mFinalNote = new Note();
        mInitialNote.setTitle("Note title");
        mFinalNote.setTitle("Note title");
    }

    private void setListeners() {
        //setting a listener on the lined edit text; double tap on that widget for entering edit mode
        /*generally, setting up gesture and double tap listeners and such is to you have to use an onTouchListener and then
        you have to pass that touch event to the gesture listener you wanna use*/
        mLinedEditText.setOnTouchListener(this);
        mGestureDetector = new GestureDetector(this, this);

        mCheck.setOnClickListener(this);
        mViewTitle.setOnClickListener(this);

        mBackArrow.setOnClickListener(this);

        mEditTitle.addTextChangedListener(this);

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        enableEditMode();
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.toolbar_check:
                hideSoftKeyboard();
                disableEditMode();
                break;
            case R.id.note_text_title:
                enableEditMode();
                //make the cursor go into the edit text, after it is clicked:
                mEditTitle.requestFocus();
                //make the cursor be at the end of the string inside the edit text:
                mEditTitle.setSelection(mEditTitle.length());
                break;
            case R.id.toolbar_back_arrow:
                //destroy the activity
                finish();
                break;
        }
    }

    //intercepting the click events on the phone's back button
    @Override
    public void onBackPressed() {
        //simulating a click to the check mark
        if (mMode == EDIT_MODE_ENABLED)
            onClick(mCheck);
        else {
            super.onBackPressed();
        }
    }

    private void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //get whatever view is currently in focus (where the cursor is basically)
        View view = this.getCurrentFocus();
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    //the method that's called when the activity is paused
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("mode", mMode);

    }

    //the method that's called when the activity is recreated
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //if we rotate the phone in edit mode, we want to stay in edit mode
        mMode = savedInstanceState.getInt("mode");
        if (mMode == EDIT_MODE_ENABLED) {
            enableEditMode();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mViewTitle.setText(s.toString());
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
