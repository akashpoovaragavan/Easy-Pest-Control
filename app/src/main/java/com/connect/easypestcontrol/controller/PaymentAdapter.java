package com.connect.easypestcontrol.controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.connect.easypestcontrol.R;
import com.connect.easypestcontrol.constant.Helper;
import com.connect.easypestcontrol.model.Customer;
import com.connect.easypestcontrol.view.CustomerDetailsScreen;
import com.connect.easypestcontrol.view.ServiceCompletedScreen;
import com.google.gson.Gson;

import java.util.List;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.PaymentViewHolder>{
    Context context;
    List<Customer> customerlist;

    public PaymentAdapter(Context context, List<Customer> customerlist) {
        this.context = context;
        this.customerlist = customerlist;
    }

    @NonNull
    @Override
    public PaymentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.payment,parent,false);
        return new PaymentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentViewHolder holder, int position) {
        holder.name.setText(customerlist.get(position).getName());
        holder.date.setText(customerlist.get(position).getService_sts_dt());
        holder.mobile.setText(customerlist.get(position).getMobile());
        holder.status.setText(customerlist.get(position).getService_sts());
        holder.view_more.setOnClickListener(v -> {
            Helper.sharedpreferences = context.getSharedPreferences(Helper.MyPREFERENCES, Context.MODE_PRIVATE);
            try {
                Gson gson = new Gson();
                String str = gson.toJson(customerlist.get(position));
                SharedPreferences.Editor ed = Helper.sharedpreferences.edit();
                ed.putString("Completed_Details", str);
                ed.apply();
                context.startActivity(new Intent(context, ServiceCompletedScreen.class));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    @Override
    public int getItemCount() {
        return customerlist.size();
    }

    public class PaymentViewHolder extends RecyclerView.ViewHolder {
        TextView name,date,mobile,status,view_more;
        public PaymentViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name);
            date=itemView.findViewById(R.id.date);
            mobile=itemView.findViewById(R.id.mob);
            status=itemView.findViewById(R.id.status);
            view_more=itemView.findViewById(R.id.view_more);
        }
    }
}
