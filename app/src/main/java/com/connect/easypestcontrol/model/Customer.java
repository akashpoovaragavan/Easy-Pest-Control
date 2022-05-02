package com.connect.easypestcontrol.model;

public class Customer {
    private String id,name,date,mobile,status,email,lead_source,lead_date,lead_branch,lead_status,lead_status_dt,service_type,service_sts,service_sts_dt,sales_man,service_man,notes;

    public Customer(String name, String date, String mobile, String status,String email) {
        this.name = name;
        this.date = date;
        this.mobile = mobile;
        this.status = status;
        this.email=email;
    }
    public Customer(){

    }
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getMobile() {
        return mobile;
    }

    public String getStatus() {
        return status;
    }

    public String getEmail() {
        return email;
    }


    public String getLead_source() {
        return lead_source;
    }

    public String getLead_date() {
        return lead_date;
    }

    public String getLead_branch() {
        return lead_branch;
    }

    public String getLead_status() {
        return lead_status;
    }

    public String getLead_status_dt() {
        return lead_status_dt;
    }

    public String getService_type() {
        return service_type;
    }

    public String getService_sts() {
        return service_sts;
    }

    public String getService_sts_dt() {
        return service_sts_dt;
    }

    public String getSales_man() {
        return sales_man;
    }

    public String getService_man() {
        return service_man;
    }

    public String getNotes() {
        return notes;
    }

    public void setId(String id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setLead_source(String lead_source) {
        this.lead_source = lead_source;
    }

    public void setLead_date(String lead_date) {
        this.lead_date = lead_date;
    }

    public void setLead_branch(String lead_branch) {
        this.lead_branch = lead_branch;
    }

    public void setLead_status(String lead_status) {
        this.lead_status = lead_status;
    }

    public void setLead_status_dt(String lead_status_dt) {
        this.lead_status_dt = lead_status_dt;
    }

    public void setService_type(String service_type) {
        this.service_type = service_type;
    }

    public void setService_sts(String service_sts) {
        this.service_sts = service_sts;
    }

    public void setService_sts_dt(String service_sts_dt) {
        this.service_sts_dt = service_sts_dt;
    }

    public void setSales_man(String sales_man) {
        this.sales_man = sales_man;
    }

    public void setService_man(String service_man) {
        this.service_man = service_man;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
