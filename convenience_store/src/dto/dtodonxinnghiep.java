package dto;

import java.util.Date;

public class dtodonxinnghiep {
    private int maDonXin;
    private int maNhanVien;
    private int maLoaiDon;
    private Date ngayBatDau;
    private Date ngayKetThuc;
    private int soNgayNghi;
    private String lyDo;
    private String giayChungMinh;
    private String trangThai;
    private Integer nguoiDuyet;
    private Date ngayDuyet;
    private String ghiChu;
    private Date ngayTao;
    private Date ngayCapNhat;
    private String tenNhanVien;
    private String tenLoaiDon;
    private String tenNguoiDuyet;

    public dtodonxinnghiep() {
    }

    public dtodonxinnghiep(int maDonXin, int maNhanVien, int maLoaiDon, Date ngayBatDau, Date ngayKetThuc,
            int soNgayNghi, String lyDo, String giayChungMinh, String trangThai, Integer nguoiDuyet,
            Date ngayDuyet, String ghiChu, Date ngayTao, Date ngayCapNhat) {
        this.maDonXin = maDonXin;
        this.maNhanVien = maNhanVien;
        this.maLoaiDon = maLoaiDon;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
        this.soNgayNghi = soNgayNghi;
        this.lyDo = lyDo;
        this.giayChungMinh = giayChungMinh;
        this.trangThai = trangThai;
        this.nguoiDuyet = nguoiDuyet;
        this.ngayDuyet = ngayDuyet;
        this.ghiChu = ghiChu;
        this.ngayTao = ngayTao;
        this.ngayCapNhat = ngayCapNhat;
    }

    public int getMaDonXin() {
        return maDonXin;
    }

    public void setMaDonXin(int maDonXin) {
        this.maDonXin = maDonXin;
    }

    public int getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(int maNhanVien) {
        this.maNhanVien = maNhanVien;
    }

    public int getMaLoaiDon() {
        return maLoaiDon;
    }

    public void setMaLoaiDon(int maLoaiDon) {
        this.maLoaiDon = maLoaiDon;
    }

    public Date getNgayBatDau() {
        return ngayBatDau;
    }

    public void setNgayBatDau(Date ngayBatDau) {
        this.ngayBatDau = ngayBatDau;
    }

    public Date getNgayKetThuc() {
        return ngayKetThuc;
    }

    public void setNgayKetThuc(Date ngayKetThuc) {
        this.ngayKetThuc = ngayKetThuc;
    }

    public int getSoNgayNghi() {
        return soNgayNghi;
    }

    public void setSoNgayNghi(int soNgayNghi) {
        this.soNgayNghi = soNgayNghi;
    }

    public String getLyDo() {
        return lyDo;
    }

    public void setLyDo(String lyDo) {
        this.lyDo = lyDo;
    }

    public String getGiayChungMinh() {
        return giayChungMinh;
    }

    public void setGiayChungMinh(String giayChungMinh) {
        this.giayChungMinh = giayChungMinh;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public Integer getNguoiDuyet() {
        return nguoiDuyet;
    }

    public void setNguoiDuyet(Integer nguoiDuyet) {
        this.nguoiDuyet = nguoiDuyet;
    }

    public Date getNgayDuyet() {
        return ngayDuyet;
    }

    public void setNgayDuyet(Date ngayDuyet) {
        this.ngayDuyet = ngayDuyet;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public Date getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(Date ngayTao) {
        this.ngayTao = ngayTao;
    }

    public Date getNgayCapNhat() {
        return ngayCapNhat;
    }

    public void setNgayCapNhat(Date ngayCapNhat) {
        this.ngayCapNhat = ngayCapNhat;
    }

    public String getTenNhanVien() {
        return tenNhanVien;
    }

    public void setTenNhanVien(String tenNhanVien) {
        this.tenNhanVien = tenNhanVien;
    }

    public String getTenLoaiDon() {
        return tenLoaiDon;
    }

    public void setTenLoaiDon(String tenLoaiDon) {
        this.tenLoaiDon = tenLoaiDon;
    }

    public String getTenNguoiDuyet() {
        return tenNguoiDuyet;
    }

    public void setTenNguoiDuyet(String tenNguoiDuyet) {
        this.tenNguoiDuyet = tenNguoiDuyet;
    }
}