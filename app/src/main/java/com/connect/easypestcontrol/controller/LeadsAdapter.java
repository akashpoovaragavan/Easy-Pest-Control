package com.connect.easypestcontrol.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.connect.easypestcontrol.R;
import com.connect.easypestcontrol.constant.Helper;
import com.connect.easypestcontrol.model.Customer;
import com.connect.easypestcontrol.view.CustomerDetailsScreen;
import com.google.gson.Gson;

import org.json.JSONException;

import java.util.List;

public class LeadsAdapter extends RecyclerView.Adapter<LeadsAdapter.LeadsViewHolder>{
    Context context;
    List<Customer> customerlist;

    public LeadsAdapter(Context context, List<Customer> customerlist) {
        this.context = context;
        this.customerlist = customerlist;
    }

    @NonNull
    @Override
    public LeadsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.leads,parent,false);
        return new LeadsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeadsViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.name.setText(customerlist.get(position).getName());
        holder.date.setText(customerlist.get(position).getLead_date());
        holder.mobile.setText(customerlist.get(position).getMobile());
        holder.email.setText(customerlist.get(position).getEmail());
        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s="tel:"+customerlist.get(position).getMobile();
                Intent intent=new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse(s));
                context.startActivity(intent);
            }
        });

        holder.lead_lin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Helper.sharedpreferences = context.getSharedPreferences(Helper.MyPREFERENCES, Context.MODE_PRIVATE);
                try{
                    Gson gson = new Gson();
                    String str = gson.toJson(customerlist.get(position));
                    SharedPreferences.Editor ed = Helper.sharedpreferences.edit();
                    ed.putString("Lead_Details",str);
                    ed.apply();
                    context.startActivity(new Intent(context, CustomerDetailsScreen.class));
                }catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });
    }

    @Override
    public int getItemCount() {
        return customerlist.size();
    }

    public class LeadsViewHolder extends RecyclerView.ViewHolder {
        TextView name,date,mobile,email;
        ImageView call;
        LinearLayout lead_lin;
        public LeadsViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name);
            date=itemView.findViewById(R.id.date);
            mobile=itemView.findViewById(R.id.mob);
            email=itemView.findViewById(R.id.email);
            call=itemView.findViewById(R.id.call);
            lead_lin=itemView.findViewById(R.id.lead_lin);
        }
    }
}
