package com.codegym.a0223i1_pharmacy_professional_be.service.interfaceservice.report;

import com.codegym.a0223i1_pharmacy_professional_be.dto.*;
import com.codegym.a0223i1_pharmacy_professional_be.entity.Medicine;
import com.codegym.a0223i1_pharmacy_professional_be.entity.Supplier;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IReportService {
    List<Medicine> getListDrugEnter();

    List<IExpiredMedicineDTO> findMedicinesExpiringSoon();

    List<ITopSellingMedicineDTO> findTopSellingMedicines(String startDay, String endDay
            , String startHour, String endHour);

    List<ISalesDiaryDTO> salesDiary(String startDay, String endDay, String startHour, String endHour);

    List<ISupplierDTO> getDebtSuppliers(String startDay, String endDay, String startHour, String endHour);

    List<IRevenueDTO> revenue(String startDay, String endDay, String startHour, String endHour);

    List<IRevenueDTO> profit(String startDay, String endDay, String startHour, String endHour);

    List<IRevenueProfitDTO> getRevenueAndProfit(String startDate, String endDate);

    List<IRevenueProfitDTO> getRevenueAndProfitByYear(String startDate, String endDate);


}
