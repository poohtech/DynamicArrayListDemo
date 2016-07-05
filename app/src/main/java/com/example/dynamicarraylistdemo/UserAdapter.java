package com.example.dynamicarraylistdemo;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.Holder> {

    private Context context;
    private ArrayList<UserBean> userAdapterList;

    public UserAdapter(Context context) {
        this.context = context;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder extends RecyclerView.ViewHolder {
        public TextView rowTxt;
        public ImageView rowImg;

        public Holder(View itemView) {
            super(itemView);
            rowTxt = (TextView) itemView.findViewById(R.id.rowTxt);
            rowImg = (ImageView) itemView.findViewById(R.id.rowImg);
        }
    }

    @Override
    public int getItemCount() {
        return userAdapterList.size();
    }

    public void setMoreItem(ArrayList<UserBean> usersList) {
        if (this.userAdapterList == null)
            this.userAdapterList = new ArrayList<UserBean>();

        this.userAdapterList.addAll(usersList);
    }

    public ArrayList<UserBean> getItemList() {
        return this.userAdapterList;
    }

    @Override
    public void onBindViewHolder(Holder holder, int pos) {
        System.out.println("*********Adapter : userAdapterList.get(pos).image*********" + userAdapterList.get(pos).image);
        holder.rowTxt.setText(userAdapterList.get(pos).text);
        holder.rowImg.setImageBitmap(BitmapFactory.decodeFile(userAdapterList.get(pos).image));
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup arg0, int arg1) {
        View v = LayoutInflater.from(arg0.getContext()).inflate(
                R.layout.row_text, null);
        Holder mh = new Holder(v);
        return mh;
    }
}