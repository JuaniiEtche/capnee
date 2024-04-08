package com.gidas.capneebe.api.models.repositories;

import com.gidas.capneebe.api.models.entities.CaracteristicasFaciales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CaracteristicasFacialesRepository extends JpaRepository<CaracteristicasFaciales, Long> {

}