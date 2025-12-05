package com.monitor_service.domain.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.monitor_service.domain.model.Report;
import com.monitor_service.domain.model.ReportStatus;

public interface ReportRepository {
	Report save(Report report);

	Page<Report> findAll(Pageable pageable);

	Page<Report> findAllByStatus(ReportStatus status, Pageable pageable);

	Optional<Report> findById(Long id);
}
