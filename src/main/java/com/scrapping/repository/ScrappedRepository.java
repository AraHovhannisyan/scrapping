package com.scrapping.repository;

import com.scrapping.model.Scrapped;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScrappedRepository extends JpaRepository<Scrapped, Long> {
}
