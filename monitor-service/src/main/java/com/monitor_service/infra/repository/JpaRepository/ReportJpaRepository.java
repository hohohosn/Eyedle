package com.monitor_service.infra.repository.JpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.monitor_service.domain.model.Report;

public interface ReportJpaRepository extends JpaRepository<Report, Long> {
}
