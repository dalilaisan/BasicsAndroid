package com.example.dalila.androidbasics.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dalila.androidbasics.R;
import com.example.dalila.androidbasics.models.Note;

import java.util.ArrayList;

/*
When implementing a recycler view you always need some kind og an adapter class to adapt the list of objects to the recylcer view.
It/s the adapter's job to take each individual object and set the properties of each object to the individual list of the recycler view.

The first step in buildingan adapers is using a viewHolder class, which is responsible for holding that view.
 */


public class NotesRecyclerAdapter extends RecyclerView.Adapter<NotesRecyclerAdapter.ViewHolder>{

    private ArrayList<Note> mNotes = new ArrayList<>();

    public NotesRecyclerAdapter(ArrayList<Note> notes) {
        this.mNotes = notes;
    }


    //this method is responsile for instantiating the view holder object
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_notes_list_item, viewGroup, false);
        return new ViewHolder(view);
    }

    //this one is called for every single entry in the list
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        //here we set the attributes to the viewHolder object
        viewHolder.timestamp.setText(mNotes.get(i).getTimeStamp());
        viewHolder.title.setText(mNotes.get(i).getTitle());

    }

    @Override
    public int getItemCount() {
        //size of the arraylist, that is , the number of notes in the array list
        return mNotes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        TextView timestamp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //connect the layout with the class
            title = itemView.findViewById(R.id.note_title);
            timestamp = itemView.findViewById(R.id.note_timestamp);
        }
    }

    //basically all adapers classes are gonna follow the same general structure, the only thing where they differ is
    //the data type you specify, the layout and the viewholder  because the viewholder represents whatever we have in the layout
    //but the onCreateViewHolder method is goona ba exactly the same, other than the the layout (R.layour.layout_notes_list_item),
    //the onBindViewHolder method is gonna be different depending on the fileds that you have
    //all the getItemCount method ever returns is the size of the list
}