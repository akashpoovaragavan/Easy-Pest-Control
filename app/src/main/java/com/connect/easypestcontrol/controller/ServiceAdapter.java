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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.connect.easypestcontrol.R;
import com.connect.easypestcontrol.constant.Helper;
import com.connect.easypestcontrol.model.Customer;
import com.connect.easypestcontrol.view.CustomerDetailsScreen;
import com.connect.easypestcontrol.view.ServiceDetailsScreen;
import com.google.gson.Gson;

import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder>{
    Context context;
    List<Customer> customerlist;

    public ServiceAdapter(Context context, List<Customer> customerlist) {
        this.context = context;
        this.customerlist = customerlist;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.customer,parent,false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.name.setText(customerlist.get(position).getName());
        holder.date.setText(customerlist.get(position).getService_sts_dt());
        holder.mobile.setText(customerlist.get(position).getMobile());
        holder.status.setText(customerlist.get(position).getService_sts());

        holder.view_more.setOnClickListener(v -> {
            Helper.sharedpreferences = context.getSharedPreferences(Helper.MyPREFERENCES, Context.MODE_PRIVATE);
            try{
                Gson gson = new Gson();
                String str = gson.toJson(customerlist.get(position));
                SharedPreferences.Editor ed = Helper.sharedpreferences.edit();
                ed.putString("Service_Details",str);
                ed.apply();
                context.startActivity(new Intent(context, ServiceDetailsScreen.class));
            }catch (Exception e) {
                e.printStackTrace();
            }
        });
        holder.call.setOnClickListener(v -> {
            String s="tel:"+customerlist.get(position).getMobile();
            Intent intent=new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse(s));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return customerlist.size();
    }

    public class ServiceViewHolder extends RecyclerView.ViewHolder {
        TextView name,date,mobile,status,view_more;
        ImageView call;
        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name);
            date=itemView.findViewById(R.id.date);
            mobile=itemView.findViewById(R.id.mob);
            status=itemView.findViewById(R.id.status);
            view_more=itemView.findViewById(R.id.view_more);
            call=itemView.findViewById(R.id.call);
        }
    }
}
