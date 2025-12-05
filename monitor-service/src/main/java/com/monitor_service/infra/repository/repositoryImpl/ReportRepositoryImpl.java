package com.monitor_service.infra.repository.repositoryImpl;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.monitor_service.domain.model.Report;
import com.monitor_service.domain.model.ReportStatus;
import com.monitor_service.domain.repository.ReportRepository;
import com.monitor_service.infra.repository.JpaRepository.ReportJpaRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReportRepositoryImpl implements ReportRepository {
	private final ReportJpaRepository reportJpaRepository;

	@Override
	public Report save(Report report){
		return reportJpaRepository.save(report);
	}

	@Override
	public Page<Report> findAll(Pageable pageable){
		return reportJpaRepository.findAll(pageable);
	}

	@Override
	public Page<Report> findAllByStatus(ReportStatus status, Pageable pageable) {
		return reportJpaRepository.findAllByStatus(status, pageable);
	}

	@Override
	public Optional<Report> findById(Long id) {
		return reportJpaRepository.findById(id);
	}
}
