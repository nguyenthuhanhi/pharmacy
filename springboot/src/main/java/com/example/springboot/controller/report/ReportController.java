package com.example.springboot.controller.report;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.codegym.a0223i1_pharmacy_professional_be.dto.IExpiredMedicineDTO;
import com.codegym.a0223i1_pharmacy_professional_be.dto.IRevenueDTO;
import com.codegym.a0223i1_pharmacy_professional_be.dto.IRevenueProfitDTO;
import com.codegym.a0223i1_pharmacy_professional_be.dto.ISalesDiaryDTO;
import com.codegym.a0223i1_pharmacy_professional_be.dto.ISupplierDTO;
import com.codegym.a0223i1_pharmacy_professional_be.dto.ITopSellingMedicineDTO;
import com.codegym.a0223i1_pharmacy_professional_be.entity.Medicine;
import com.example.springboot.interfaceservice.report.IReportService;

@RestController
@CrossOrigin("*")
public class ReportController {

    @Autowired
    IReportService iReportService;

    @RequestMapping(value = "/revenue-profit", method = RequestMethod.GET)
    public ResponseEntity<?> getRevenueAndProfit
            (@RequestParam String chartType, @RequestParam String startDate, @RequestParam String endDate) {
                
        if (chartType.equalsIgnoreCase("year")) {
            List<IRevenueProfitDTO> iRevenueProfitDTOS = iReportService.getRevenueAndProfitByYear(startDate, endDate);
            return new ResponseEntity<>(iRevenueProfitDTOS, HttpStatus.OK);
        } else {
            List<IRevenueProfitDTO> iRevenueProfitDTOS = iReportService.getRevenueAndProfit(startDate, endDate);
            return new ResponseEntity<>(iRevenueProfitDTOS, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/generate-report", method = RequestMethod.GET)
    public ResponseEntity<?> generateReport
            (@RequestParam String reportType, @RequestParam String startDate, @RequestParam String endDate,
             @RequestParam String startTime, @RequestParam String endTime) throws IOException {
        switch (reportType) {
            case "drug-enter":
                return getAllDrugEnterReport();
            case "medicines-expiring-soon":
                return getAllMedicinesExpiringSoon();
            case "top-selling-medicine":
                return findTopSellingMedicines(startDate, endDate, startTime, endTime);
            case "sales-diary":
                return salesDiary(startDate, endDate, startTime, endTime);
            case "debt":
                return getDebtSuppliers(startDate, endDate, startTime, endTime);
            case "revenue":
                return revenue(startDate, endDate, startTime, endTime);
            case "profit":
                return profit(startDate, endDate, startTime, endTime);
            default:
                return new ResponseEntity<>("Loại báo cáo không hợp lệ", HttpStatus.BAD_REQUEST);
        }
    }

    private ResponseEntity<?> profit
            (String startDate, String endDate, String startTime, String endTime) throws IOException {
        List<IRevenueDTO> profit = iReportService.profit(startDate, endDate, startTime, endTime);
        if (profit.isEmpty()) {
            String message = "Không có dữ liệu lợi nhuận trong khoảng thời gian đã chọn";
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
        String[] columnNames = {"Thời gian", "Lợi nhuận"};
        writeWorkbook(profit, columnNames, "Lợi nhuận", "D:\\loinhuan.xlsx");
        String successMessage = "Báo cáo lợi nhuận đã được xuất thành công trong khoảng thời gian đã chọn";
        return new ResponseEntity<>(successMessage, HttpStatus.OK);
    }

    private ResponseEntity<?> revenue
            (String startDate, String endDate, String startTime, String endTime) throws IOException {
        List<IRevenueDTO> revenue = iReportService.revenue(startDate, endDate, startTime, endTime);
        if (revenue.isEmpty()) {
            return new ResponseEntity<>("Cửa hàng chưa bán đưược trong khoảng thời gian đã chọn", HttpStatus.NOT_FOUND);
        }
        String[] columnNames = {"Thời gian", "Doanh thu"};
        writeWorkbook(revenue, columnNames, "Doanh thu", "D:\\doanhthu.xlsx");
        return new ResponseEntity<>("Báo cáo doanh thu đã được tạo thành công", HttpStatus.OK);
    }

    private ResponseEntity<?> getDebtSuppliers
            (String startDate, String endDate, String startTime, String endTime) throws IOException {
        List<ISupplierDTO> suppliers = iReportService.getDebtSuppliers(startDate, endDate, startTime, endTime);
        if (suppliers.isEmpty()) {
            return new ResponseEntity<>("Hiện không có nhà cung cấp nào có công nợ trong khoảng thời gian đã chọn", HttpStatus.NOT_FOUND);
        }
        String[] columnNames = {"Mã nhà cung cấp", "Tên nhà cung cấp", "Địa chỉ", "Email", "Số điện thoại", "Công nợ"};
        writeWorkbook(suppliers, columnNames, "Nhật ký bán hàng", "D:\\congno.xlsx");
        return new ResponseEntity<>("Báo cáo công nợ của các nhà cung cấp đã được tạo thành công", HttpStatus.OK);
    }
    private ResponseEntity<?> salesDiary
    (String startDate, String endDate, String startTime, String endTime) throws IOException {
        List<ISalesDiaryDTO> iSalesDiaryDTOS = iReportService.salesDiary(startDate, endDate, startTime, endTime);
        if (iSalesDiaryDTOS.isEmpty()) {
            return new ResponseEntity<>("Không có dữ liệu nhật ký bán hàng trong khoảng thời gian đã chọn", HttpStatus.NOT_FOUND);
        }
        String[] columnNames = {"Mã nhân viên", "Tên nhân viên", "Mã hóa đơn", "Ngày tạo", "Tên bác sĩ", "Số điện thoại bác sĩ", "Triệu chứng", "Chuẩn đoán", "Ghi chú", "Tổng tiền"};
        writeWorkbook(iSalesDiaryDTOS, columnNames, "Nhật ký bán hàng", "D:\\nhatkybanhang.xlsx");
        return new ResponseEntity<>("Báo cáo nhật ký bán hàng đã được xuất thành công trong khoảng thời gian đã chọn", HttpStatus.OK);
    }

    private ResponseEntity<?> getAllDrugEnterReport() throws IOException {
        List<Medicine> medicines = iReportService.getListDrugEnter();
        if (medicines.isEmpty()) {
            return new ResponseEntity<>("Hiện không có thuốc nào cần nhập thêm", HttpStatus.NOT_FOUND);
        }
        String[] columnNames = {"Mã thuốc", "Tên thuốc", "Số lượng"};
        writeWorkbook(medicines, columnNames, "Thuốc cần nhập thêm", "D:\\thuoccannhapthem.xlsx");
        return new ResponseEntity<>("Báo cáo thuốc cần nhập thêm đã được tạo thành công", HttpStatus.OK);
    }

    private ResponseEntity<?> getAllMedicinesExpiringSoon() throws IOException {
        List<IExpiredMedicineDTO> expiredMedicines = iReportService.findMedicinesExpiringSoon();
        if (expiredMedicines.isEmpty()) {
            return new ResponseEntity<>("Hiện không có thuốc nào sắp hết hạn", HttpStatus.NOT_FOUND);
        }
        String[] columnNames = {"Mã thuốc", "Tên thuốc", "Hạn sử dụng"};
        writeWorkbook(expiredMedicines, columnNames, "Thuốc sắp hết hạn", "D:\\thuocsaphethan.xlsx");
        return new ResponseEntity<>("Báo cáo thuốc sắp hết hạn đã được tạo thành công", HttpStatus.OK);
    }

    private ResponseEntity<?> findTopSellingMedicines
            (String startDate, String endDate, String startTime, String endTime) throws IOException {
        List<ITopSellingMedicineDTO> iTopSellingMedicineDTOS = iReportService.findTopSellingMedicines
                (startDate, endDate, startTime, endTime);
        if (iTopSellingMedicineDTOS.isEmpty()) {
            return new ResponseEntity<>("Không có thuốc nào được bán trong khoảng thời gian này", HttpStatus.NOT_FOUND);
        }
        String[] columnNames = {"Mã thuốc", "Tên thuốc", "Số lượng bán ra"};
        writeWorkbook(iTopSellingMedicineDTOS, columnNames, "Thuốc bán chạy", "D:\\thuocbanchay.xlsx");
        return new ResponseEntity<>("Báo cáo thuốc bán chạy đã được tạo thành công", HttpStatus.OK);
    }

    //    private void writeWorkbook(List<?> data, String[] columnNames, String sheetName, String fileName)
//            throws IOException, FileNotFoundException {
//        try (
//                Workbook workbook = new XSSFWorkbook();
//                FileOutputStream fos = new FileOutputStream(fileName);
//        ) {
//            Sheet sheet = workbook.createSheet(sheetName);
//            Row headerRow = sheet.createRow(0);
//            for (int i = 0; i < columnNames.length; i++) {
//                headerRow.createCell(i).setCellValue(columnNames[i]);
//            }
//            for (int i = 0; i < data.size(); i++) {
//                Row row = sheet.createRow(i + 1);
//                if (data.get(i) instanceof Medicine) {
//                    Medicine medicine = (Medicine) data.get(i);
//                    row.createCell(0).setCellValue(medicine.getMedicineId());
//                    row.createCell(1).setCellValue(medicine.getMedicineName());
//                    row.createCell(2).setCellValue(medicine.getQuantity());
//                } else if (data.get(i) instanceof IExpiredMedicineDTO) {
//                    IExpiredMedicineDTO medicine = (IExpiredMedicineDTO) data.get(i);
//                    row.createCell(0).setCellValue(medicine.getMedicineId());
//                    row.createCell(1).setCellValue(medicine.getMedicineName());
//                    row.createCell(2).setCellValue(medicine.getExpiredDate().toString());
//                } else if (data.get(i) instanceof ITopSellingMedicineDTO) {
//                    ITopSellingMedicineDTO medicine = (ITopSellingMedicineDTO) data.get(i);
//                    row.createCell(0).setCellValue(medicine.getMedicineId());
//                    row.createCell(1).setCellValue(medicine.getMedicineName());
//                    row.createCell(2).setCellValue(medicine.getTotalQuantity());
//                } else if (data.get(i) instanceof IRevenueDTO) {
//                    IRevenueDTO revenueDTO = (IRevenueDTO) data.get(i);
//                    row.createCell(0).setCellValue(revenueDTO.getDate());
//                    double revenueValue;
//                    if(revenueDTO.getRevenue()==null){
//                        revenueValue=0;
//                    }else {
//                        revenueValue = Double.parseDouble(revenueDTO.getRevenue());
//                    }
//
//                    String revenue = String.format("%,.0f", revenueValue);
//                    revenue = revenue.replace(',', '.');
//                    revenue += " ₫";
//                    Cell revenueCell = row.createCell(1);
//                    revenueCell.setCellValue(revenue);
//                } else if (data.get(i) instanceof ISalesDiaryDTO) {
//                    ISalesDiaryDTO salesDiaryDTO = (ISalesDiaryDTO) data.get(i);
//                    row.createCell(0).setCellValue(salesDiaryDTO.getEmployeeName());
//                    row.createCell(1).setCellValue(salesDiaryDTO.getDateCreate());
//                    row.createCell(2).setCellValue(salesDiaryDTO.getInvoiceId());
//                    String total = String.format("%,.0f", salesDiaryDTO.getTotal());
//                    total = total.replace(',', '.');
//                    total += " ₫";
//                    Cell totalCell = row.createCell(3);
//                    totalCell.setCellValue(total);
//                } else if (data.get(i) instanceof ISupplierDTO) {
//                    ISupplierDTO supplier = (ISupplierDTO) data.get(i);
//                    row.createCell(0).setCellValue(supplier.getSupplierId());
//                    row.createCell(1).setCellValue(supplier.getSupplierName());
//                    row.createCell(2).setCellValue(supplier.getAddress());
//                    row.createCell(3).setCellValue(supplier.getEmail());
//                    row.createCell(4).setCellValue(supplier.getPhoneNumber());
//                    String debt = String.format("%,.0f", supplier.getToPayDebt());
//                    debt = debt.replace(',', '.');
//                    debt += " ₫";
//                    Cell debtCell = row.createCell(5);
//                    debtCell.setCellValue(debt);
//                }
//            }
//            workbook.write(fos);
//        } catch (FileNotFoundException e) {
//            String errorMessage = "Không thể ghi vào tệp vì nó đang được sử dụng bởi một quá trình khác. Vui lòng đảm bảo rằng tệp không đang được mở và thử lại.";
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
    private void createCell(Row row, int columnIndex, String value, CellStyle cellStyle) {
        Cell cell = row.createCell(columnIndex);
        cell.setCellValue(value);
        cell.setCellStyle(cellStyle);
    }

    private void createCell(Row row, int columnIndex, double value, CellStyle cellStyle) {
        Cell cell = row.createCell(columnIndex);
        cell.setCellValue(value);
        cell.setCellStyle(cellStyle);
    }

    private void writeWorkbook(List<?> data, String[] columnNames, String sheetName, String fileName)
            throws IOException, FileNotFoundException {
        try (Workbook workbook = new XSSFWorkbook(); FileOutputStream fos = new FileOutputStream(fileName);) {
            Sheet sheet = workbook.createSheet(sheetName);
            Row headerRow = sheet.createRow(0);

            // Tạo CellStyle cho các ô dữ liệu
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);

            // Tạo CellStyle cho tiêu đề
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setFontHeightInPoints((short) 12);

            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);
            headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
            headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            // Tạo một màu xanh dương nhạt
            XSSFColor lightBlue = new XSSFColor(new java.awt.Color(173, 216, 230));
            // Đặt màu nền cho tiêu đề
            headerCellStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerCellStyle.setBorderTop(BorderStyle.THIN);
            headerCellStyle.setBorderBottom(BorderStyle.THIN);
            headerCellStyle.setBorderLeft(BorderStyle.THIN);
            headerCellStyle.setBorderRight(BorderStyle.THIN);

            for (int i = 0; i < columnNames.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columnNames[i]);
                cell.setCellStyle(headerCellStyle);
            }

            for (int i = 0; i < data.size(); i++) {
                Row row = sheet.createRow(i + 1);
                if (data.get(i) instanceof Medicine) {
                    Medicine medicine = (Medicine) data.get(i);
                    createCell(row, 0, medicine.getMedicineId(), cellStyle);
                    createCell(row, 1, medicine.getMedicineName(), cellStyle);
                    createCell(row, 2, medicine.getQuantity(), cellStyle);
                } else if (data.get(i) instanceof IExpiredMedicineDTO) {
                    IExpiredMedicineDTO medicine = (IExpiredMedicineDTO) data.get(i);
                    createCell(row, 0, medicine.getMedicineId(), cellStyle);
                    createCell(row, 1, medicine.getMedicineName(), cellStyle);
                    createCell(row, 2, medicine.getExpiredDate().toString(), cellStyle);
                } else if (data.get(i) instanceof ITopSellingMedicineDTO) {
                    ITopSellingMedicineDTO medicine = (ITopSellingMedicineDTO) data.get(i);
                    createCell(row, 0, medicine.getMedicineId(), cellStyle);
                    createCell(row, 1, medicine.getMedicineName(), cellStyle);
                    createCell(row, 2, medicine.getTotalQuantity(), cellStyle);
                } else if (data.get(i) instanceof IRevenueDTO) {
                    IRevenueDTO revenueDTO = (IRevenueDTO) data.get(i);
                    createCell(row, 0, revenueDTO.getDate(), cellStyle);
                    double revenueValue = revenueDTO.getRevenue() != null ? Double.parseDouble(revenueDTO.getRevenue()) : 0;
                    String revenue = String.format("%,.0f", revenueValue).replace(',', '.') + " ₫";
                    createCell(row, 1, revenue, cellStyle);
                } else if (data.get(i) instanceof ISalesDiaryDTO) {
                    ISalesDiaryDTO salesDiaryDTO = (ISalesDiaryDTO) data.get(i);
                    createCell(row, 0, salesDiaryDTO.getEmployeeId(), cellStyle);
                    createCell(row, 1, salesDiaryDTO.getEmployeeName(), cellStyle);
                    createCell(row, 2, salesDiaryDTO.getInvoiceId(), cellStyle);
                    createCell(row, 3, salesDiaryDTO.getSaleDate(), cellStyle);
                    createCell(row, 4, salesDiaryDTO.getDoctorName(), cellStyle);
                    createCell(row, 5, salesDiaryDTO.getDoctorPhone(), cellStyle);
                    createCell(row, 6, salesDiaryDTO.getSymptom(), cellStyle);
                    createCell(row, 7, salesDiaryDTO.getDoctorDiagnosis(), cellStyle);
                    createCell(row, 8, salesDiaryDTO.getNote(), cellStyle);
                    if (salesDiaryDTO.getTotalInvoiceAmount() == null) {
                        createCell(row, 9, 0, cellStyle);
                    } else {
                        String total = String.format("%,.0f ₫", Double.parseDouble(salesDiaryDTO.getTotalInvoiceAmount())).replace(',', '.');
                        createCell(row, 9, total, cellStyle);
                    }
                } else if (data.get(i) instanceof ISupplierDTO) {
                    ISupplierDTO supplier = (ISupplierDTO) data.get(i);
                    createCell(row, 0, supplier.getSupplierId(), cellStyle);
                    createCell(row, 1, supplier.getSupplierName(), cellStyle);
                    createCell(row, 2, supplier.getAddress(), cellStyle);
                    createCell(row, 3, supplier.getEmail(), cellStyle);
                    createCell(row, 4, supplier.getPhoneNumber(), cellStyle);
                    String debt = String.format("%,.0f ₫", supplier.getToPayDebt()).replace(',', '.');
                    createCell(row, 5, debt, cellStyle);
                }
            }
            for (int i = 0; i < columnNames.length; i++) {
                sheet.autoSizeColumn(i);
            }
            workbook.write(fos);
        } catch (FileNotFoundException e) {
            String errorMessage = "Không thể ghi vào tệp vì nó đang được sử dụng bởi một quá trình khác. Vui lòng đảm bảo rằng tệp không đang được mở và thử lại.";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

