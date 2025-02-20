package com.example.CalendarThriftService.model;

import javax.persistence.*;



@Entity
public class EmployeeModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String workEmail;
    private String officeLocation;
    @Column(columnDefinition = "BOOLEAN DEFAULT true")
    private boolean isActive=true;
    @ManyToOne
    @JoinColumn(name = "officeId", nullable = false)
    private OfficeModel office;

    public EmployeeModel() {
    }

    public EmployeeModel(String name, String workEmail, OfficeModel office, boolean active) {
        this.name=name;
        this.workEmail=workEmail;
        this.office=office;

    }




    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id=id;
    }

    public boolean isActive(){
        return isActive;
    }

    public void setActive(boolean active){
        this.isActive=active;
    }



    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name=name;
    }

    public String getWorkEmail(){
        return workEmail;
    }

    public void setWorkEmail(String workEmail){
        this.workEmail=workEmail;
    }

    public OfficeModel getOffice(){
        return office;
    }

    public void setOffice(OfficeModel office){
        this.office=office;
    }


}
