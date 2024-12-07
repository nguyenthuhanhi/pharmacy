package com.example.springboot.implement.report;

import com.codegym.a0223i1_pharmacy_professional_be.dto.*;
import com.codegym.a0223i1_pharmacy_professional_be.entity.Medicine;
import com.codegym.a0223i1_pharmacy_professional_be.entity.Supplier;
import com.codegym.a0223i1_pharmacy_professional_be.repository.report.IReportRepository;
import com.example.springboot.interfaceservice.report.IReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportServiceImpl implements IReportService {
    @Autowired
    private IReportRepository iReportRepository;

    @Override
    public List<Medicine> getListDrugEnter() {
        return iReportRepository.getListDrugEnter();
    }

    @Override
    public List<IExpiredMedicineDTO> findMedicinesExpiringSoon() {
        return iReportRepository.findMedicinesExpiringSoon();
    }

    @Override
    public List<ITopSellingMedicineDTO> findTopSellingMedicines(String startDay, String endDay, String startHour, String endHour) {
        return iReportRepository.findTopSellingMedicines(startDay, endDay, startHour, endHour);
    }

    @Override
    public List<IRevenueDTO> revenue(String startDay, String endDay, String startHour, String endHour) {
        return iReportRepository.revenue(startDay, endDay, startHour, endHour);
    }

    @Override
    public List<IRevenueDTO> profit(String startDay, String endDay, String startHour, String endHour) {
        return iReportRepository.profit(startDay, endDay, startHour, endHour);
    }

    @Override
    public List<ISalesDiaryDTO> salesDiary(String startDay, String endDay, String startHour, String endHour) {
        return iReportRepository.salesDiary(startDay, endDay, startHour, endHour);
    }

    @Override
    public List<ISupplierDTO> getDebtSuppliers(String startDay, String endDay, String startHour, String endHour) {
        return iReportRepository.getDebtSuppliers(startDay,endDay,startHour,endHour);
    }

    @Override
    public List<IRevenueProfitDTO> getRevenueAndProfit(String startDate, String endDate) {
        return iReportRepository.getRevenueAndProfit(startDate, endDate);
    }

    @Override
    public List<IRevenueProfitDTO> getRevenueAndProfitByYear(String startDate, String endDate) {
        return iReportRepository.getRevenueAndProfitByYear(startDate, endDate);
    }

}
