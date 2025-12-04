package com.monitor_service.infra.repository.repositoryImpl;

import org.springframework.stereotype.Repository;

import com.monitor_service.domain.model.Report;
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
}
