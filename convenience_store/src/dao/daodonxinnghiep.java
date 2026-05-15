package dao;

import dto.dtodonxinnghiep;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class daodonxinnghiep {

    public ArrayList<dtodonxinnghiep> getList() {
        ArrayList<dtodonxinnghiep> list = new ArrayList<>();
        String sql = "SELECT d.*, nv.tenNhanVien AS tenNhanVien, ld.tenLoaiDon AS tenLoaiDon "
                + "FROM donxinnghiep d "
                + "LEFT JOIN nhanvien nv ON d.maNhanVien = nv.maNhanVien "
                + "LEFT JOIN loaidonxinnghiep ld ON d.maLoaiDon = ld.maLoaiDon "
                + "ORDER BY d.maDonXin DESC";

        try (Connection con = connect.connection(); PreparedStatement pst = con.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException ex) {
            Logger.getLogger(daodonxinnghiep.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    public ArrayList<dtodonxinnghiep> getListByEmployee(int maNhanVien) {
        ArrayList<dtodonxinnghiep> list = new ArrayList<>();
        String sql = "SELECT d.*, nv.tenNhanVien AS tenNhanVien, ld.tenLoaiDon AS tenLoaiDon "
                + "FROM donxinnghiep d "
                + "LEFT JOIN nhanvien nv ON d.maNhanVien = nv.maNhanVien "
                + "LEFT JOIN loaidonxinnghiep ld ON d.maLoaiDon = ld.maLoaiDon "
                + "WHERE d.maNhanVien = ? "
                + "ORDER BY d.maDonXin DESC";

        try (Connection con = connect.connection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, maNhanVien);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(daodonxinnghiep.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    public dtodonxinnghiep getById(int maDonXin) {
        String sql = "SELECT d.*, nv.tenNhanVien AS tenNhanVien, ld.tenLoaiDon AS tenLoaiDon "
                + "FROM donxinnghiep d "
                + "LEFT JOIN nhanvien nv ON d.maNhanVien = nv.maNhanVien "
                + "LEFT JOIN loaidonxinnghiep ld ON d.maLoaiDon = ld.maLoaiDon "
                + "WHERE d.maDonXin = ?";
        try (Connection con = connect.connection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, maDonXin);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(daodonxinnghiep.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void add(dtodonxinnghiep don) {
        try (Connection con = connect.connection()) {
            boolean hasNgayDuyet = tableHasColumn(con, "ngayDuyet");
            String sql;
            if (hasNgayDuyet) {
                sql = "INSERT INTO donxinnghiep (maNhanVien, maLoaiDon, ngayBatDau, ngayKetThuc, soNgayNghi, trangThai, ngayDuyet, ghiChu) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            } else {
                sql = "INSERT INTO donxinnghiep (maNhanVien, maLoaiDon, ngayBatDau, ngayKetThuc, soNgayNghi, trangThai, ghiChu) VALUES (?, ?, ?, ?, ?, ?, ?)";
            }
            try (PreparedStatement pst = con.prepareStatement(sql)) {
                fillStatement(pst, don, false, hasNgayDuyet);
                pst.executeUpdate();
            }
        } catch (SQLException ex) {
            Logger.getLogger(daodonxinnghiep.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void update(dtodonxinnghiep don) {
        try (Connection con = connect.connection()) {
            boolean hasNgayDuyet = tableHasColumn(con, "ngayDuyet");
            String sql;
            if (hasNgayDuyet) {
                sql = "UPDATE donxinnghiep SET maNhanVien = ?, maLoaiDon = ?, ngayBatDau = ?, ngayKetThuc = ?, soNgayNghi = ?, trangThai = ?, ngayDuyet = ?, ghiChu = ? WHERE maDonXin = ?";
            } else {
                sql = "UPDATE donxinnghiep SET maNhanVien = ?, maLoaiDon = ?, ngayBatDau = ?, ngayKetThuc = ?, soNgayNghi = ?, trangThai = ?, ghiChu = ? WHERE maDonXin = ?";
            }
            try (PreparedStatement pst = con.prepareStatement(sql)) {
                fillStatement(pst, don, true, hasNgayDuyet);
                pst.executeUpdate();
            }
        } catch (SQLException ex) {
            Logger.getLogger(daodonxinnghiep.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean updateStatus(int maDonXin, String trangThai) {
        try (Connection con = connect.connection()) {
            boolean hasNgayDuyet = tableHasColumn(con, "ngayDuyet");
            String sql;
            if (hasNgayDuyet) {
                sql = "UPDATE donxinnghiep SET trangThai = ?, ngayDuyet = ? WHERE maDonXin = ?";
            } else {
                sql = "UPDATE donxinnghiep SET trangThai = ? WHERE maDonXin = ?";
            }
            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setString(1, trangThai);
                if (hasNgayDuyet) {
                    pst.setDate(2, new java.sql.Date(System.currentTimeMillis()));
                    pst.setInt(3, maDonXin);
                } else {
                    pst.setInt(2, maDonXin);
                }
                pst.executeUpdate();
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(daodonxinnghiep.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public void delete(int maDonXin) {
        String sql = "DELETE FROM donxinnghiep WHERE maDonXin = ?";
        try (Connection con = connect.connection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, maDonXin);
            pst.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(daodonxinnghiep.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void fillStatement(PreparedStatement pst, dtodonxinnghiep don, boolean includeId, boolean includeNgayDuyet) throws SQLException {
        int idx = 1;
        pst.setInt(idx++, don.getMaNhanVien());
        pst.setInt(idx++, don.getMaLoaiDon());
        pst.setDate(idx++, new java.sql.Date(don.getNgayBatDau().getTime()));
        pst.setDate(idx++, new java.sql.Date(don.getNgayKetThuc().getTime()));
        pst.setInt(idx++, don.getSoNgayNghi());
        pst.setString(idx++, don.getTrangThai());
        if (includeNgayDuyet) {
            if (don.getNgayDuyet() == null) {
                pst.setNull(idx++, java.sql.Types.DATE);
            } else {
                pst.setDate(idx++, new java.sql.Date(don.getNgayDuyet().getTime()));
            }
        }
        pst.setString(idx++, don.getGhiChu());
        if (includeId) {
            pst.setInt(idx++, don.getMaDonXin());
        }
    }

    private dtodonxinnghiep mapRow(ResultSet rs) throws SQLException {
        dtodonxinnghiep don = new dtodonxinnghiep(
            rs.getInt("maDonXin"),
            rs.getInt("maNhanVien"),
            rs.getInt("maLoaiDon"),
            rs.getDate("ngayBatDau"),
            rs.getDate("ngayKetThuc"),
            rs.getInt("soNgayNghi"),
            null,
            null,
            rs.getString("trangThai"),
            null,
            (hasColumn(rs, "ngayDuyet") ? rs.getDate("ngayDuyet") : null),
            rs.getString("ghiChu"),
            (hasColumn(rs, "ngayTao") ? rs.getTimestamp("ngayTao") : null),
            (hasColumn(rs, "ngayCapNhat") ? rs.getTimestamp("ngayCapNhat") : null));
        don.setTenNhanVien(rs.getString("tenNhanVien"));
        don.setTenLoaiDon(rs.getString("tenLoaiDon"));
        return don;
    }

    private boolean hasColumn(ResultSet rs, String columnName) {
        try {
            rs.findColumn(columnName);
            return true;
        } catch (SQLException ex) {
            return false;
        }
    }

    private boolean tableHasColumn(Connection con, String columnName) {
        try {
            java.sql.DatabaseMetaData md = con.getMetaData();
            // try exact name
            try (ResultSet rs = md.getColumns(null, null, "donxinnghiep", columnName)) {
                if (rs.next()) return true;
            }
            // try uppercase
            try (ResultSet rs = md.getColumns(null, null, "donxinnghiep", columnName.toUpperCase())) {
                if (rs.next()) return true;
            }
            // try lowercase
            try (ResultSet rs = md.getColumns(null, null, "donxinnghiep", columnName.toLowerCase())) {
                if (rs.next()) return true;
            }
        } catch (SQLException ex) {
            // ignore and fall through
        }
        return false;
    }
}