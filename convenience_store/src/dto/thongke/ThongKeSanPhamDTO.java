package dto.thongke;

public class ThongKeSanPhamDTO {
    private int maSanPham;
    private String tenSanPham;
    private long soLuongBan;
    private long doanhThu;
    private long chiPhi;
    private long loiNhuan;

    public ThongKeSanPhamDTO(int maSanPham, String tenSanPham, long soLuongBan, long doanhThu, long chiPhi, long loiNhuan) {
        this.maSanPham = maSanPham;
        this.tenSanPham = tenSanPham;
        this.soLuongBan = soLuongBan;
        this.doanhThu = doanhThu;
        this.chiPhi = chiPhi;
        this.loiNhuan = loiNhuan;
    }

    public int getMaSanPham() {
        return maSanPham;
    }

    public void setMaSanPham(int maSanPham) {
        this.maSanPham = maSanPham;
    }

    public String getTenSanPham() {
        return tenSanPham;
    }

    public void setTenSanPham(String tenSanPham) {
        this.tenSanPham = tenSanPham;
    }

    public long getSoLuongBan() {
        return soLuongBan;
    }

    public void setSoLuongBan(long soLuongBan) {
        this.soLuongBan = soLuongBan;
    }

    public long getDoanhThu() {
        return doanhThu;
    }

    public void setDoanhThu(long doanhThu) {
        this.doanhThu = doanhThu;
    }

    public long getChiPhi() {
        return chiPhi;
    }

    public void setChiPhi(long chiPhi) {
        this.chiPhi = chiPhi;
    }

    public long getLoiNhuan() {
        return loiNhuan;
    }

    public void setLoiNhuan(long loiNhuan) {
        this.loiNhuan = loiNhuan;
    }
}