package by.lunamretic.chat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import by.lunamretic.chat.R;
import by.lunamretic.chat.entity.Message;

public class CustomAdapter extends BaseAdapter {
    private ArrayList<Message> listData;
    private LayoutInflater layoutInflater;
    private FirebaseUser firebaseUser;

    public CustomAdapter(Context aContext, ArrayList<Message> listData) {
        this.listData = listData;
        layoutInflater = LayoutInflater.from(aContext);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void remove(int position) {
        listData.remove(position);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;


        if (convertView == null) {
            holder = new ViewHolder();

//            if (firebaseUser.getUid().matches(listData.get(position).uid)) {
//                convertView = layoutInflater.inflate(R.layout.msg_row_user, null);
//
//                holder.message = (TextView) convertView.findViewById(R.id.msg);
//            } else {
                convertView = layoutInflater.inflate(R.layout.msg_row, null);

                holder.author = (TextView) convertView.findViewById(R.id.msgUsername);
                holder.message = (TextView) convertView.findViewById(R.id.msg);
//            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

//        if (firebaseUser.getUid().matches(listData.get(position).uid)) {
//            holder.message.setText(listData.get(position).msg);
//        } else {
            holder.author.setText(listData.get(position).author);
            holder.message.setText(listData.get(position).msg);
//        }
        return convertView;
    }

    static class ViewHolder {
        TextView author;
        TextView message;
    }
}