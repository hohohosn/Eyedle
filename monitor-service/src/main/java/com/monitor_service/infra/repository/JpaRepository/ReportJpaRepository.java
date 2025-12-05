package com.monitor_service.infra.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.monitor_service.domain.model.Report;
import com.monitor_service.domain.model.ReportStatus;

public interface ReportJpaRepository extends JpaRepository<Report, Long> {
	Page<Report> findAllByStatus(ReportStatus status, Pageable pageable);
}
