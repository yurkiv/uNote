package com.yurkiv.materialnotes.data;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.yurkiv.materialnotes.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yurkiv on 02.02.2015.
 */
public class NotesAdapter extends BaseAdapter implements Filterable {

    private final List<Note> notes;
    private List<Note> filteredList;
    private NoteFilter noteFilter;


    public NotesAdapter(List<Note> notes) {
        this.notes = notes;
        this.filteredList=notes;
        getFilter();
    }


    @Override
    public int getCount() {
        return filteredList.size();
    }

    @Override
    public Note getItem(int position) {
        return filteredList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView==null){
            convertView=LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_row, parent, false);
            holder=new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder= (ViewHolder) convertView.getTag();
        }
        Note note=getItem(position);
        holder.textRow.setText(note.getTitle());
        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (noteFilter==null){
            noteFilter=new NoteFilter();
        }
        return noteFilter;
    }

    private static class ViewHolder{
        private TextView textRow;
        private View parent;

        private ViewHolder(View parent) {
            this.parent = parent;
            textRow = (TextView) parent.findViewById(R.id.textRow);
        }
    }

    private class NoteFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults=new FilterResults();
            if (constraint!=null && constraint.length()>0){
                ArrayList<Note> tempList=new ArrayList<>();
                for (Note note:notes){
                    if (note.getTitle().toLowerCase().contains(constraint.toString().toLowerCase())){
                        tempList.add(note);
                    }
                }
                filterResults.count=tempList.size();
                filterResults.values=tempList;
            } else {
                filterResults.count=notes.size();
                filterResults.values=notes;
            }

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredList= (ArrayList<Note>) results.values;

            Log.d("NA", filteredList.toString());
            notifyDataSetChanged();
        }
    }
}
