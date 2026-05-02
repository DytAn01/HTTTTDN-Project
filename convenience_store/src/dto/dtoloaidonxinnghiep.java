package dto;

public class dtoloaidonxinnghiep {
    private int maLoaiDon;
    private String tenLoaiDon;
    private String moTa;

    public dtoloaidonxinnghiep() {
    }

    public dtoloaidonxinnghiep(int maLoaiDon, String tenLoaiDon, String moTa) {
        this.maLoaiDon = maLoaiDon;
        this.tenLoaiDon = tenLoaiDon;
        this.moTa = moTa;
    }

    public int getMaLoaiDon() {
        return maLoaiDon;
    }

    public void setMaLoaiDon(int maLoaiDon) {
        this.maLoaiDon = maLoaiDon;
    }

    public String getTenLoaiDon() {
        return tenLoaiDon;
    }

    public void setTenLoaiDon(String tenLoaiDon) {
        this.tenLoaiDon = tenLoaiDon;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public String getDropdownDisplay() {
        return tenLoaiDon;
    }

    @Override
    public String toString() {
        return tenLoaiDon;
    }
}