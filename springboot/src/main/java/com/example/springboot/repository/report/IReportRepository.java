package com.codegym.a0223i1_pharmacy_professional_be.repository.report;

import com.codegym.a0223i1_pharmacy_professional_be.dto.*;
import com.codegym.a0223i1_pharmacy_professional_be.entity.Medicine;
import com.codegym.a0223i1_pharmacy_professional_be.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IReportRepository extends JpaRepository<Medicine, String> {
    @Query("SELECT m FROM Medicine m WHERE m.quantity < 5")
    List<Medicine> getListDrugEnter();

    @Query(value = "SELECT m.medicine_id as medicineId, m.medicine_name as medicineName, DATE(w.expired_date) as expiredDate \n" +
            "FROM Medicine m\n" +
            "JOIN ware_in_detail w ON m.medicine_id = w.medicine_id \n" +
            "WHERE DATEDIFF(w.expired_date, CURRENT_DATE) <= 10" , nativeQuery = true)
    List<IExpiredMedicineDTO> findMedicinesExpiringSoon();

    @Query(nativeQuery = true, value =
            "SELECT m.medicine_id as medicineId , m.medicine_name as medicineName, SUM(id.quantity) as totalQuantity \n" +
                    "FROM medicine m \n" +
                    "JOIN invoice_detail id ON m.medicine_id = id.medicine_id\n" +
                    "LEFT JOIN invoice i ON id.invoice_id = i.invoice_id \n" +
                    "LEFT JOIN invoice_pres ip ON id.invoice_pres_id = ip.invoice_pres_id\n" +
                    "WHERE (\n" +
                    "    (DATE(i.date_create) BETWEEN :startDay AND :endDay) AND\n" +
                    "    (TIME(i.date_create) BETWEEN :startHour AND :endHour) OR\n" +
                    "    (DATE(ip.date_create) BETWEEN :startDay AND :endDay) AND\n" +
                    "    (TIME(ip.date_create) BETWEEN :startHour AND :endHour)\n" +
                    ")\n" +
                    "GROUP BY m.medicine_id, m.medicine_name \n" +
                    "ORDER BY totalQuantity DESC \n" +
                    "LIMIT 100")
    List<ITopSellingMedicineDTO> findTopSellingMedicines(@Param("startDay") String startDay
            , @Param("endDay") String endDay,@Param("startHour") String startHour,@Param("endHour") String endHour);


    @Query(nativeQuery = true, value =
            "WITH RECURSIVE DateRange AS (\n" +
                    "    SELECT DATE(:startDay) AS Date\n" +
                    "    UNION ALL\n" +
                    "    SELECT Date + INTERVAL 1 DAY\n" +
                    "    FROM DateRange\n" +
                    "    WHERE Date + INTERVAL 1 DAY <= DATE(:endDay)\n" +
                    ")\n" +
                    "SELECT d.Date AS date,COALESCE(SUM(combined.revenue), 0) AS revenue\n" +
                    "FROM DateRange d\n" +
                    "LEFT JOIN (\n" +
                    "    SELECT DATE(invoice.date_create) AS date,SUM(invoice_detail.quantity * invoice_detail.price) AS revenue\n" +
                    "    FROM invoice\n" +
                    "    JOIN invoice_detail ON invoice.invoice_id = invoice_detail.invoice_id\n" +
                    "    JOIN medicine ON invoice_detail.medicine_id = medicine.medicine_id\n" +
                    "    WHERE (\n" +
                    "        DATE(date_create) BETWEEN :startDay AND :endDay AND\n" +
                    "        TIME(date_create) BETWEEN :startHour AND :endHour\n" +
                    "    )\n" +
                    "    GROUP BY date\n" +
                    "    UNION ALL\n" +
                    "    SELECT DATE(invoice_pres.date_create) AS date,SUM(invoice_detail.quantity * invoice_detail.price) AS revenue\n" +
                    "    FROM invoice_pres\n" +
                    "    JOIN invoice_detail ON invoice_pres.invoice_pres_id = invoice_detail.invoice_pres_id\n" +
                    "    JOIN medicine ON invoice_detail.medicine_id = medicine.medicine_id\n" +
                    "    WHERE (\n" +
                    "        DATE(date_create) BETWEEN :startDay AND :endDay AND\n" +
                    "        TIME(date_create) BETWEEN :startHour AND :endHour\n" +
                    "    )\n" +
                    "    GROUP BY date\n" +
                    ") AS combined ON d.Date = combined.date\n" +
                    "GROUP BY d.Date")
    List<IRevenueDTO> revenue(@Param("startDay") String startDay, @Param("endDay") String endDay
            ,@Param("startHour") String startHour,@Param("endHour") String endHour);
//    @Query(nativeQuery = true, value =
//            "SELECT DATE(date_create) as date, SUM(total) as revenue\n" +
//                    "FROM (\n" +
//                    "    SELECT date_create, total\n" +
//                    "    FROM invoice\n" +
//                    "    WHERE (\n" +
//                    "        DATE(date_create)  BETWEEN :startDay AND :endDay AND\n" +
//                    "        TIME(date_create) BETWEEN :startHour AND :endHour\n" +
//                    "    )\n" +
//                    "    UNION ALL\n" +
//                    "    SELECT date_create, total\n" +
//                    "    FROM invoice_pres\n" +
//                    "    WHERE (\n" +
//                    "        DATE(date_create)  BETWEEN :startDay AND :endDay AND\n" +
//                    "        TIME(date_create) BETWEEN :startHour AND :endHour\n" +
//                    "    )\n" +
//                    ") AS combined\n" +
//                    "GROUP BY date\n" +
//                    "ORDER BY date")
//    List<IRevenueDTO> revenue(@Param("startDay") String startDay, @Param("endDay") String endDay
//            ,@Param("startHour") String startHour,@Param("endHour") String endHour);

        @Query(nativeQuery = true, value =
                "WITH RECURSIVE DateRange AS (\n" +
                        "    SELECT DATE(:startDay) AS Date\n" +
                        "    UNION ALL\n" +
                        "    SELECT Date + INTERVAL 1 DAY\n" +
                        "    FROM DateRange\n" +
                        "    WHERE Date + INTERVAL 1 DAY <= DATE(:endDay)\n" +
                        ")\n" +
                        "SELECT d.Date AS date, COALESCE(SUM(combined.profit), 0) AS revenue\n" +
                        "FROM DateRange d\n" +
                        "LEFT JOIN (\n" +
                        "    SELECT DATE(invoice.date_create) AS date,\n" +
                        "           SUM(invoice_detail.quantity * invoice_detail.price) - SUM(invoice_detail.quantity * (medicine.import_price/medicine.conversion_rate)) AS profit\n" +
                        "    FROM invoice\n" +
                        "    JOIN invoice_detail ON invoice.invoice_id = invoice_detail.invoice_id\n" +
                        "    JOIN medicine ON invoice_detail.medicine_id = medicine.medicine_id\n" +
                        "    WHERE (\n" +
                        "        DATE(date_create)  BETWEEN :startDay AND :endDay AND\n" +
                        "        TIME(date_create) BETWEEN :startHour AND :endHour\n" +
                        "    )\n" +
                        "    GROUP BY date\n" +
                        "    UNION ALL\n" +
                        "    SELECT DATE(invoice_pres.date_create) AS date,\n" +
                        "           SUM(invoice_detail.quantity * invoice_detail.price) - SUM(invoice_detail.quantity * (medicine.import_price/medicine.conversion_rate)) AS profit\n" +
                        "    FROM invoice_pres\n" +
                        "    JOIN invoice_detail ON invoice_pres.invoice_pres_id = invoice_detail.invoice_pres_id\n" +
                        "    JOIN medicine ON invoice_detail.medicine_id = medicine.medicine_id\n" +
                        "    WHERE (\n" +
                        "        DATE(date_create)  BETWEEN :startDay AND :endDay AND\n" +
                        "        TIME(date_create) BETWEEN :startHour AND :endHour\n" +
                        "    )\n" +
                        "    GROUP BY date\n" +
                        ") AS combined ON d.Date = combined.date\n" +
                        "GROUP BY d.Date\n")
        List<IRevenueDTO> profit(@Param("startDay") String startDay, @Param("endDay") String endDay
                ,@Param("startHour") String startHour,@Param("endHour") String endHour);
//        @Query(nativeQuery = true, value =
//                "SELECT date(date) as date,SUM(profit) AS 'revenue'\n" +
//                        "FROM\n" +
//                        "(SELECT invoice.date_create AS 'date',\n" +
//                        "        SUM(invoice_detail.quantity * invoice_detail.price) - SUM(invoice_detail.quantity * (medicine.import_price/medicine.conversion_rate)) AS 'profit'\n" +
//                        "    FROM invoice \n" +
//                        "    JOIN invoice_detail ON invoice.invoice_id = invoice_detail.invoice_id \n" +
//                        "    JOIN medicine ON invoice_detail.medicine_id = medicine.medicine_id\n" +
//                        "    WHERE (\n" +
//                        "        DATE(date_create)  BETWEEN :startDay AND :endDay AND\n" +
//                        "        TIME(date_create) BETWEEN :startHour AND :endHour\n" +
//                        "    )\n" +
//                        "    GROUP BY date\n" +
//                        "    UNION ALL\n" +
//                        "    SELECT invoice_pres.date_create AS 'date',\n" +
//                        "        SUM(invoice_detail.quantity * invoice_detail.price) - SUM(invoice_detail.quantity * (medicine.import_price/medicine.conversion_rate)) AS 'profit'\n" +
//                        "    FROM invoice_pres \n" +
//                        "    JOIN invoice_detail ON invoice_pres.invoice_pres_id = invoice_detail.invoice_pres_id \n" +
//                        "    JOIN medicine ON invoice_detail.medicine_id = medicine.medicine_id\n" +
//                        "    WHERE (\n" +
//                        "        DATE(date_create)  BETWEEN :startDay AND :endDay AND\n" +
//                        "        TIME(date_create) BETWEEN :startHour AND :endHour\n" +
//                        "    )\n" +
//                        "    GROUP BY date\n" +
//                        ") AS combined GROUP BY date\n")
//        List<IRevenueDTO> profit(@Param("startDay") String startDay, @Param("endDay") String endDay
//                ,@Param("startHour") String startHour,@Param("endHour") String endHour);


//    @Query(nativeQuery = true, value =
//            "SELECT e.employee_name as employeeName , \n" +
//                    "       DATE_FORMAT(i.date_create, '%Y-%m-%d') as dateCreate, \n" +
//                    "       i.invoice_id as invoiceId, \n" +
//                    "       i.total\n" +
//                    "FROM invoice i\n" +
//                    "JOIN employee e ON i.employee_id = e.employee_id\n" +
//                    "    WHERE (\n" +
//                    "        DATE(date_create)  BETWEEN :startDay AND :endDay AND\n" +
//                    "        TIME(date_create) BETWEEN :startHour AND :endHour\n" +
//                    "    )\n" +
//                    "UNION ALL\n" +
//                    "SELECT e.employee_name, \n" +
//                    "       DATE_FORMAT(ip.date_create, '%Y-%m-%d'), \n" +
//                    "       ip.invoice_pres_id, \n" +
//                    "       ip.total\n" +
//                    "FROM invoice_pres ip\n" +
//                    "JOIN employee e ON ip.employee_id = e.employee_id\n" +
//                    "    WHERE (\n" +
//                    "        DATE(date_create)  BETWEEN :startDay AND :endDay AND\n" +
//                    "        TIME(date_create) BETWEEN :startHour AND :endHour\n" +
//                    "    )\n" +
//                    "ORDER BY dateCreate")
//    List<ISalesDiaryDTO> salesDiary(@Param("startDay") String startDay, @Param("endDay") String endDay
//            , @Param("startHour") String startHour, @Param("endHour") String endHour);
@Query(nativeQuery = true, value =
        "SELECT e.employee_id as employeeId, e.employee_name as employeeName , i.invoice_id as invoiceId ,DATE_FORMAT(i.date_create, '%Y-%m-%d') AS saleDate, \n" +
                "    SUM(id.price * id.quantity) AS totalInvoiceAmount,\n" +
                "    COALESCE(NULL, 'N/A') AS doctorDiagnosis,\n" +
                "    COALESCE(NULL, 'N/A') AS doctorName,\n" +
                "    COALESCE(NULL, 'N/A') AS doctorPhone,\n" +
                "    COALESCE(NULL, 'N/A') AS symptom,\n" +
                "    COALESCE(i.note, 'N/A') AS note\n" +
                "FROM employee e\n" +
                "JOIN invoice i ON e.employee_id = i.employee_id\n" +
                "JOIN invoice_detail id ON i.invoice_id = id.invoice_id\n" +
                "    WHERE (\n" +
                "        DATE(date_create)  BETWEEN :startDay AND :endDay AND\n" +
                "        TIME(date_create) BETWEEN :startHour AND :endHour\n" +
                "    )\n" +
                "GROUP BY employeeId,employeeName, invoiceId,saleDate,i.note\n" +
                "UNION ALL\n" +
                "SELECT e.employee_id as employeeId, e.employee_name as employeeName, ip.invoice_pres_id as invoiceId ,DATE_FORMAT(ip.date_create, '%Y-%m-%d') AS saleDate, \n" +
                "    SUM(id.price * id.quantity) AS totalInvoiceAmount,ip.doctor_diagnosis as doctorDiagnosis,ip.doctor_name as doctorName,\n" +
                "    ip.doctor_phone,ip.symptom,COALESCE(ip.note, 'N/A') AS note\n" +
                "FROM employee e\n" +
                "JOIN invoice_pres ip ON e.employee_id = ip.employee_id\n" +
                "JOIN invoice_detail id ON ip.invoice_pres_id = id.invoice_pres_id\n" +
                "    WHERE (\n" +
                "        DATE(date_create)  BETWEEN :startDay AND :endDay AND\n" +
                "        TIME(date_create) BETWEEN :startHour AND :endHour\n" +
                "    )\n" +
                "GROUP BY employeeId,employeeName, invoiceId,saleDate,ip.doctor_diagnosis,\n" +
                "ip.doctor_name,ip.doctor_phone,ip.symptom,ip.note\n" +
                "ORDER BY employeeName")
List<ISalesDiaryDTO> salesDiary(@Param("startDay") String startDay, @Param("endDay") String endDay
        , @Param("startHour") String startHour, @Param("endHour") String endHour);

    @Query(nativeQuery = true, value =
            "SELECT s.supplier_id as supplierId, s.supplier_name as supplierName, s.address, s.email, s.phone_number as phoneNumber, \n" +
                    "    SUM((SELECT SUM(wid.price * wid.quantity) FROM ware_in_detail wid WHERE wi.warehouse_in_id = wid.warehouse_in_id) - wi.pharmacy_pay) AS toPayDebt\n" +
                    "FROM warehouse_in wi\n" +
                    "JOIN supplier s ON wi.supplier_id = s.supplier_id\n" +
                    "    WHERE (\n" +
                    "        DATE(wi.create_date)  BETWEEN :startDay AND :endDay AND\n" +
                    "        TIME(wi.create_date) BETWEEN :startHour AND :endHour\n" +
                    "    )\n" +
                    "GROUP BY s.supplier_id, s.supplier_name, s.address, s.email, s.phone_number")
    List<ISupplierDTO> getDebtSuppliers(@Param("startDay") String startDay, @Param("endDay") String endDay
            , @Param("startHour") String startHour, @Param("endHour") String endHour);

    @Query(nativeQuery = true, value =
            "WITH RECURSIVE dates(date) AS (\n" +
                    "  SELECT :startDate \n" +
                    "  UNION ALL\n" +
                    "  SELECT DATE_ADD(date, INTERVAL 1 DAY)\n" +
                    "  FROM dates\n" +
                    "  WHERE date < :endDate \n" +
                    ")\n" +
                    "SELECT dates.date AS 'date', COALESCE(SUM(revenue), 0) AS 'revenue', COALESCE(SUM(profit), 0) AS 'profit'\n" +
                    "FROM dates\n" +
                    "LEFT JOIN (\n" +
                    "  SELECT date(date) AS 'date', SUM(revenue) AS 'revenue', SUM(profit) AS 'profit'\n" +
                    "  FROM (\n" +
                    "    SELECT invoice.date_create AS 'date',\n" +
                    "      SUM(invoice_detail.quantity * invoice_detail.price) AS 'revenue',\n" +
                    "      SUM(invoice_detail.quantity * invoice_detail.price) - SUM(invoice_detail.quantity * (medicine.import_price/medicine.conversion_rate)) AS 'profit'\n" +
                    "    FROM invoice\n" +
                    "    JOIN invoice_detail ON invoice.invoice_id = invoice_detail.invoice_id\n" +
                    "    JOIN medicine ON invoice_detail.medicine_id = medicine.medicine_id\n" +
                    "    GROUP BY invoice.date_create\n" +
                    "    UNION ALL\n" +
                    "    SELECT invoice_pres.date_create AS 'date',\n" +
                    "      SUM(invoice_detail.quantity * invoice_detail.price) AS 'revenue',\n" +
                    "      SUM(invoice_detail.quantity * invoice_detail.price) - SUM(invoice_detail.quantity * (medicine.import_price/medicine.conversion_rate)) AS 'profit'\n" +
                    "    FROM invoice_pres\n" +
                    "    JOIN invoice_detail ON invoice_pres.invoice_pres_id = invoice_detail.invoice_pres_id\n" +
                    "    JOIN medicine ON invoice_detail.medicine_id = medicine.medicine_id\n" +
                    "    GROUP BY invoice_pres.date_create\n" +
                    "  ) AS combined\n" +
                    "  GROUP BY date\n" +
                    ") AS revenue_profit ON dates.date = revenue_profit.date\n" +
                    "GROUP BY dates.date")
    List<IRevenueProfitDTO> getRevenueAndProfit(@Param("startDate") String startDate
            , @Param("endDate") String endDate);


    @Query(nativeQuery = true, value =
            "WITH RECURSIVE months_in_year AS (\n" +
                    "    SELECT :startDate AS month_date\n" +
                    "    UNION\n" +
                    "    SELECT DATE_ADD(month_date, INTERVAL 1 MONTH)\n" +
                    "    FROM months_in_year\n" +
                    "    WHERE month_date < :endDate \n" +
                    ")\n" +
                    "SELECT \n" +
                    "    CONCAT('Tháng ', LPAD(MONTH(m.month_date), 2, '0')) AS 'date'," +
                    "    COALESCE(SUM(c.revenue), 0) AS 'revenue',\n" +
                    "    COALESCE(SUM(c.profit), 0) AS 'profit'\n" +
                    "FROM months_in_year m\n" +
                    "LEFT JOIN (\n" +
                    "    SELECT \n" +
                    "        DATE_FORMAT(invoice.date_create, '%Y-%m') AS 'date',\n" +
                    "        SUM(invoice_detail.quantity * invoice_detail.price) AS 'revenue',\n" +
                    "        SUM(invoice_detail.quantity * invoice_detail.price) - SUM(invoice_detail.quantity * (medicine.import_price/medicine.conversion_rate)) AS 'profit'\n" +
                    "    FROM invoice \n" +
                    "    JOIN invoice_detail ON invoice.invoice_id = invoice_detail.invoice_id \n" +
                    "    JOIN medicine ON invoice_detail.medicine_id = medicine.medicine_id\n" +
                    "    WHERE YEAR(invoice.date_create) = year(:startDate)\n" +
                    "    GROUP BY DATE_FORMAT(invoice.date_create, '%Y-%m')\n" +
                    ") AS c ON DATE_FORMAT(m.month_date, '%Y-%m') = c.date\n" +
                    "GROUP BY m.month_date")
    List<IRevenueProfitDTO> getRevenueAndProfitByYear( @Param("startDate") String startDate
            , @Param("endDate") String endDate);



}
