package com.yurkiv.unote.adapter;

import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.yurkiv.unote.R;
import com.yurkiv.unote.model.Note;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by yurkiv on 02.02.2015.
 */
public class NotesAdapterOld extends BaseAdapter implements Filterable {

    private final List<Note> notes;
    private List<Note> filteredList;
    private NoteFilter noteFilter;


    public NotesAdapterOld(List<Note> notes) {
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
        holder.titleRow.setText(note.getTitle());
        holder.contentRow.setText(note.getContent());
        GradientDrawable bgShape = (GradientDrawable)holder.ivIcon.getBackground();
        bgShape.setColor(note.getColor());
        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (noteFilter==null){
            noteFilter=new NoteFilter();
        }
        return noteFilter;
    }



    public static Comparator<Note> titleComparator =new Comparator<Note>() {
        @Override
        public int compare(Note lhs, Note rhs) {
            String lTitle=lhs.getTitle();
            String rTitle=rhs.getTitle();
            return lTitle.compareToIgnoreCase(rTitle);
        }
    };

    public static Comparator<Note> newestFirstComparator=new Comparator<Note>() {
        @Override
        public int compare(Note lhs, Note rhs) {
            Date lDate=lhs.getUpdatedAt();
            Date rDate=rhs.getUpdatedAt();
            return rDate.compareTo(lDate);
        }
    };

    public static Comparator<Note> oldestFirstComparator=new Comparator<Note>() {
        @Override
        public int compare(Note lhs, Note rhs) {
            Date lDate=lhs.getUpdatedAt();
            Date rDate=rhs.getUpdatedAt();
            return lDate.compareTo(rDate);
        }
    };


    private static class ViewHolder{
        private TextView titleRow;
        private TextView contentRow;
        private ImageView ivIcon;

        private ViewHolder(View parent) {
            titleRow = (TextView) parent.findViewById(R.id.tvTitle);
            contentRow = (TextView) parent.findViewById(R.id.tvContent);
            ivIcon = (ImageView) parent.findViewById(R.id.ivNoteIcon);
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
