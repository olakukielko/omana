package com.aambekar.tadhack.Adapters;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aambekar.tadhack.Activities.ActiveList;
import com.aambekar.tadhack.Data.Database;
import com.aambekar.tadhack.Model.Contact;
import com.aambekar.tadhack.R;

import java.util.List;


public class ActiveListAdapter extends  RecyclerView.Adapter<ActiveListViewHolder> {


    private static final String TAG = "ActiveListAdapter";
    private List<Contact> contacts;
    private ActiveList mainAct;

    public ActiveListAdapter(List<Contact> empList, ActiveList ma) {
        this.contacts = empList;
        mainAct = ma;
    }

    @NonNull
    @Override
    public ActiveListViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: MAKING NEW");
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_activelist, parent, false);

        return new ActiveListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ActiveListViewHolder holder, final int position) {
        final Contact contact = contacts.get(position);
        holder.name.setText(contact.name);
        holder.number.setText(contact.contact_no);
        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Database database = new Database(mainAct.getApplicationContext());
                database.deleteBook(contact);
                contacts.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    public void updateList(List<Contact> list){
        contacts = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }
}
