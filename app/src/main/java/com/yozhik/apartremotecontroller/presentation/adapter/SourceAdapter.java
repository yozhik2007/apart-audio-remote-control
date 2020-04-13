package com.yozhik.apartremotecontroller.presentation.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import com.yozhik.R;

public class SourceAdapter extends ArrayAdapter<String> {

    private OnEditClickListener onEditClickListener;

    public SourceAdapter(Activity context, int resourceId, int textViewId, List<String> list) {
        super(context, resourceId, textViewId, list);
    }

    public void setOnEditClickListener(OnEditClickListener onEditClickListener) {
        this.onEditClickListener = onEditClickListener;
    }

    @NotNull
    @Override
    public View getView(int position, View convertView, @NotNull ViewGroup parent) {
        return getRowView(convertView, position);
    }

    @Override
    public View getDropDownView(int position, View convertView, @NotNull ViewGroup parent) {
        return getRowView(convertView, position);
    }

    private View getRowView(View convertView, int position) {

        String name = getItem(position);

        ViewHolder holder;
        View rowview = convertView;
        if (rowview == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (inflater != null) {
                rowview = inflater.inflate(R.layout.item_dropdown_source, holder.rootRelativeLayout, false);
                holder.rootRelativeLayout = rowview.findViewById(R.id.dropdown_root_rl);
                holder.nameTextView = rowview.findViewById(R.id.title_tv);
                holder.editImageView = rowview.findViewById(R.id.edit_iv);
                rowview.setTag(holder);
            }
        } else {
            holder = (ViewHolder) rowview.getTag();
        }

        holder.nameTextView.setText(name);
        holder.editImageView.setOnClickListener(
                view -> {
                    if (onEditClickListener != null) {
                        onEditClickListener.onEditClicked(position);
                    }
                }
        );

        return rowview;
    }

    private static class ViewHolder {
        RelativeLayout rootRelativeLayout;
        TextView nameTextView;
        ImageView editImageView;
    }

    public interface OnEditClickListener {
        void onEditClicked(Integer index);
    }
}