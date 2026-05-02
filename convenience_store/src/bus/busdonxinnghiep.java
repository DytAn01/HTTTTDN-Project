package bus;

import dao.daodonxinnghiep;
import dto.dtodonxinnghiep;
import java.util.ArrayList;

public class busdonxinnghiep {
    private final daodonxinnghiep dao = new daodonxinnghiep();

    public ArrayList<dtodonxinnghiep> getList() {
        return dao.getList();
    }

    public ArrayList<dtodonxinnghiep> getListByEmployee(int maNhanVien) {
        return dao.getListByEmployee(maNhanVien);
    }

    public dtodonxinnghiep getById(int maDonXin) {
        return dao.getById(maDonXin);
    }

    public boolean add(dtodonxinnghiep don) {
        if (don.getMaNhanVien() <= 0 || don.getMaLoaiDon() <= 0 || don.getNgayBatDau() == null || don.getNgayKetThuc() == null) {
            return false;
        }
        dao.add(don);
        return true;
    }

    public boolean update(dtodonxinnghiep don) {
        if (don.getMaDonXin() <= 0) {
            return false;
        }
        dao.update(don);
        return true;
    }

    public boolean updateStatus(int maDonXin, String trangThai) {
        if (maDonXin <= 0 || trangThai == null || trangThai.trim().isEmpty()) {
            return false;
        }
        return dao.updateStatus(maDonXin, trangThai.trim());
    }

    public boolean delete(int maDonXin) {
        dao.delete(maDonXin);
        return true;
    }
}