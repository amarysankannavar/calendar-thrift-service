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
public class OfficeModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String officeLoc;

    public OfficeModel(String name, String officeLoc) {
        this.name = name;
        this.officeLoc = officeLoc;
    }

}
