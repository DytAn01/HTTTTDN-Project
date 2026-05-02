package bus;

import dao.daoloaidonxinnghiep;
import dto.dtoloaidonxinnghiep;
import java.util.ArrayList;

public class busloaidonxinnghiep {
    private final daoloaidonxinnghiep dao = new daoloaidonxinnghiep();

    public ArrayList<dtoloaidonxinnghiep> getList() {
        return dao.getList();
    }

    public dtoloaidonxinnghiep getById(int maLoaiDon) {
        return dao.getById(maLoaiDon);
    }

    public boolean add(dtoloaidonxinnghiep loaiDon) {
        if (loaiDon.getTenLoaiDon() == null || loaiDon.getTenLoaiDon().trim().isEmpty()) {
            return false;
        }
        dao.add(loaiDon);
        return true;
    }

    public boolean update(dtoloaidonxinnghiep loaiDon) {
        if (loaiDon.getTenLoaiDon() == null || loaiDon.getTenLoaiDon().trim().isEmpty()) {
            return false;
        }
        dao.update(loaiDon);
        return true;
    }

    public boolean delete(int maLoaiDon) {
        dao.delete(maLoaiDon);
        return true;
    }
}