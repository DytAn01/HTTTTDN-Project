package helper;

import dto.dtoluong;
import dto.dtonhanvien;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.awt.Font;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.print.PrinterException;

/**
 * Utility class for printing salary statements
 */
public class SalaryPrinter implements Printable {
    private dtonhanvien nhanvien;
    private ArrayList<dtoluong> salaryList;
    private int printType; // 1 = monthly, 2 = yearly
    private int month;
    private int year;
    private double luongCoban;

    public SalaryPrinter(dtonhanvien nhanvien, ArrayList<dtoluong> salaryList, 
                        int printType, int month, int year, double luongCoban) {
        this.nhanvien = nhanvien;
        this.salaryList = salaryList;
        this.printType = printType;
        this.month = month;
        this.year = year;
        this.luongCoban = luongCoban;
    }

    @Override
    public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
        if (pageIndex > 0) {
            return NO_SUCH_PAGE;
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.translate((int) pf.getImageableX(), (int) pf.getImageableY());
        g2d.setFont(new Font("Arial", Font.PLAIN, 11));

        int y = 30;
        int lineHeight = 20;

        // Header
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        if (printType == 1) {
            g2d.drawString("BẢNG LƯƠNG THÁNG " + month + "/" + year, 150, y);
        } else {
            g2d.drawString("BẢNG LƯƠNG NĂM " + year, 150, y);
        }

        y += 40;

        // Employee info
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString("THÔNG TIN NHÂN VIÊN", 30, y);
        y += 20;

        g2d.setFont(new Font("Arial", Font.PLAIN, 11));
        g2d.drawString("Tên: " + nhanvien.getTennhanvien(), 30, y);
        y += 18;
        g2d.drawString("Mã NV: " + nhanvien.getManhanvien(), 30, y);
        y += 18;
        g2d.drawString("Lương Cơ Bản: " + String.format("%.2f", luongCoban), 30, y);
        y += 18;
        g2d.drawString("Lương/Ngày: " + String.format("%.2f", luongCoban / 26), 30, y);
        y += 18;
        g2d.drawString("Lương/Giờ: " + String.format("%.2f", luongCoban / 26 / 8), 30, y);

        y += 30;

        // Salary details
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString("CHI TIẾT LƯƠNG", 30, y);
        y += 25;

        // Table header
        g2d.setFont(new Font("Arial", Font.BOLD, 10));
        g2d.drawString("Khoản Lương", 30, y);
        g2d.drawString("Số Tiền", 250, y);

        y += 15;
        g2d.drawLine(30, y, 500, y);
        y += 18;

        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        double totalSalary = 0;

        for (dtoluong luong : salaryList) {
            g2d.drawString("Lương Thực Tế", 30, y);
            g2d.drawString(String.format("%.2f", luong.getLuongThucTe()), 250, y);
            totalSalary += luong.getLuongThucTe();
            y += 18;

            if (luong.getLuongThuong() > 0) {
                g2d.drawString("Lương Thưởng", 30, y);
                g2d.drawString(String.format("%.2f", luong.getLuongThuong()), 250, y);
                totalSalary += luong.getLuongThuong();
                y += 18;
            }

            if (luong.getLuongLamThem() > 0) {
                g2d.drawString("Lương Làm Thêm", 30, y);
                g2d.drawString(String.format("%.2f", luong.getLuongLamThem()), 250, y);
                totalSalary += luong.getLuongLamThem();
                y += 18;
            }

            if (luong.getKhoanBaoHiem() > 0) {
                g2d.drawString("Khấu Bảo Hiểm", 30, y);
                g2d.drawString("-" + String.format("%.2f", luong.getKhoanBaoHiem()), 250, y);
                y += 18;
            }

            if (luong.getKhoanThue() > 0) {
                g2d.drawString("Khấu Thuế", 30, y);
                g2d.drawString("-" + String.format("%.2f", luong.getKhoanThue()), 250, y);
                y += 18;
            }
        }

        y += 10;
        g2d.drawLine(30, y, 500, y);
        y += 18;

        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.setColor(Color.BLUE);
        if (!salaryList.isEmpty()) {
            dtoluong lastSalary = salaryList.get(salaryList.size() - 1);
            g2d.drawString("Tổng Lương Thực Nhận", 30, y);
            g2d.drawString(String.format("%.2f", lastSalary.getThuclanh()), 250, y);
        }

        return PAGE_EXISTS;
    }

    public void showPrintDialog() {
        PrinterJob printerJob = PrinterJob.getPrinterJob();
        printerJob.setPrintable(this);

        if (printerJob.printDialog()) {
            try {
                printerJob.print();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void printToFile(String filename) {
        try {
            PrinterJob printerJob = PrinterJob.getPrinterJob();
            printerJob.setPrintable(this);
            printerJob.print();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
