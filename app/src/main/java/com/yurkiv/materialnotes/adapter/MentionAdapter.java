package com.yurkiv.materialnotes.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yurkiv.materialnotes.R;
import com.yurkiv.materialnotes.model.Mention;

import java.util.List;

/**
 * Created by yurkiv on 19.02.2015.
 */
public class MentionAdapter extends BaseAdapter {

    private List<Mention> mentions;
    public MentionAdapter(List<Mention> mentions) {
        this.mentions = mentions;
    }

    @Override
    public int getCount() {
        return mentions != null ? mentions.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return mentions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView==null){
            convertView=LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_row, parent, false);
            holder=new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder= (ViewHolder) convertView.getTag();
        }
        holder.textView.setText(mentions.get(position).getName());

        return convertView;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.item_name);
        }
    }
}
