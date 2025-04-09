package com.example.CalendarThriftService.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;



@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    public EmployeeModel(String name, String workEmail, OfficeModel office, boolean active) {
        this.name=name;
        this.workEmail=workEmail;
        this.office=office;

    }
}
