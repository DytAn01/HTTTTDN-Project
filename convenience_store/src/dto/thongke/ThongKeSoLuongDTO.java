package dto.thongke;

public class ThongKeSoLuongDTO {
    private int thoigian;
    private long soLuong;

    public ThongKeSoLuongDTO(int thoigian, long soLuong) {
        this.thoigian = thoigian;
        this.soLuong = soLuong;
    }

    public int getThoigian() {
        return thoigian;
    }

    public void setThoigian(int thoigian) {
        this.thoigian = thoigian;
    }

    public long getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(long soLuong) {
        this.soLuong = soLuong;
    }
}