package com.example.foodjournal;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;


public class JournalAdapter extends FirestoreRecyclerAdapter<Journal, JournalAdapter.JournalHolder> {
    public JournalAdapter(FirestoreRecyclerOptions<Journal> options) {
        super(options);
    }

    // set/arrange views in main view to info received from instance variables
    @Override
    protected void onBindViewHolder(JournalHolder holder, int position, Journal model) {
        holder.textViewTitle.setText(model.getTitle());
        holder.textViewDescription.setText(model.getDescription());
        holder.textViewPriority.setText(String.valueOf(model.getPriority()));
        //add get image for the main image
    }

    //layout to inflate
    @Override
    public JournalHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.journal_item,
                parent, false);
        return new JournalHolder(v);
    }

    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    class JournalHolder extends RecyclerView.ViewHolder {
        // relevant views
        TextView textViewTitle;
        TextView textViewDescription;
        TextView textViewPriority;


        public JournalHolder(View itemView) {
            super(itemView);
            // connect objects to views in XML layout
            textViewTitle = itemView.findViewById(R.id.text_view_title);
            textViewDescription = itemView.findViewById(R.id.text_view_description);
            textViewPriority = itemView.findViewById(R.id.text_view_priority);
        }
    }
}