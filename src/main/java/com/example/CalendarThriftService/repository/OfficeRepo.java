package com.example.CalendarThriftService.repository;

import com.example.CalendarThriftService.model.OfficeModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OfficeRepo extends JpaRepository<OfficeModel, Integer> {

    Optional<OfficeModel> findById(int officeId);

    Optional<OfficeModel> findByName(String name);

}
