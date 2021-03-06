package com.example.foodjournal;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;


public class JournalAdapter extends FirestoreRecyclerAdapter<Journal, JournalAdapter.JournalHolder> {
    private OnItemClickListener listener;

    public JournalAdapter(FirestoreRecyclerOptions<Journal> options) {
        super(options);
    }

    // set/arrange views in main view according to retrieved values from Journal object
    @Override
    protected void onBindViewHolder(JournalHolder holder, int position, Journal model) {
        holder.textViewTitle.setText(model.getTitle());
        holder.textViewDescription.setText(model.getDescription());
        holder.textViewDate.setText(model.getDate());

        Picasso.get()
                .load(model.getImage())
                .error(R.drawable.common_full_open_on_phone)//change it to 'no image'
                .fit()
                .centerCrop()
                .into(holder.imageViewJournal);

    }

    //layout to inflate
    @NonNull
    @Override
    public JournalHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.journal_item,
                parent, false);
        return new JournalHolder(v);
    }

    // delete item in position 'position'
    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    class JournalHolder extends RecyclerView.ViewHolder {
        // define relevant views
        TextView textViewTitle;
        TextView textViewDescription;
        TextView textViewDate;
        ImageView imageViewJournal;
        LinearLayout emptyLayout;

        public JournalHolder(View itemView) {
            super(itemView);
            // connect objects to views in XML layout
            textViewTitle = itemView.findViewById(R.id.text_view_title);
            textViewDescription = itemView.findViewById(R.id.text_view_description);
            textViewDate = itemView.findViewById(R.id.text_view_date);
            imageViewJournal = itemView.findViewById(R.id.image_view_journal);
            emptyLayout = itemView.findViewById(R.id.emptyLayout);

            // set on click listener to get position of particular item
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(getSnapshots().getSnapshot(position), position);
                }
            });

        }
    }

    //method to send the document reference
    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}