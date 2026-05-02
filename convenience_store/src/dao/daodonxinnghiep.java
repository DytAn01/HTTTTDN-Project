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
        String sql = "INSERT INTO donxinnghiep (maNhanVien, maLoaiDon, ngayBatDau, ngayKetThuc, soNgayNghi, trangThai, ngayDuyet, ghiChu) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = connect.connection(); PreparedStatement pst = con.prepareStatement(sql)) {
            fillStatement(pst, don, false);
            pst.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(daodonxinnghiep.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void update(dtodonxinnghiep don) {
        String sql = "UPDATE donxinnghiep SET maNhanVien = ?, maLoaiDon = ?, ngayBatDau = ?, ngayKetThuc = ?, soNgayNghi = ?, trangThai = ?, ngayDuyet = ?, ghiChu = ? WHERE maDonXin = ?";
        try (Connection con = connect.connection(); PreparedStatement pst = con.prepareStatement(sql)) {
            fillStatement(pst, don, true);
            pst.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(daodonxinnghiep.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean updateStatus(int maDonXin, String trangThai) {
        String sql = "UPDATE donxinnghiep SET trangThai = ?, ngayDuyet = ? WHERE maDonXin = ?";
        try (Connection con = connect.connection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, trangThai);
            pst.setDate(2, new java.sql.Date(System.currentTimeMillis()));
            pst.setInt(3, maDonXin);
            pst.executeUpdate();
            return true;
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

    private void fillStatement(PreparedStatement pst, dtodonxinnghiep don, boolean includeId) throws SQLException {
        pst.setInt(1, don.getMaNhanVien());
        pst.setInt(2, don.getMaLoaiDon());
        pst.setDate(3, new java.sql.Date(don.getNgayBatDau().getTime()));
        pst.setDate(4, new java.sql.Date(don.getNgayKetThuc().getTime()));
        pst.setInt(5, don.getSoNgayNghi());
        pst.setString(6, don.getTrangThai());
        if (don.getNgayDuyet() == null) {
            pst.setNull(7, java.sql.Types.DATE);
        } else {
            pst.setDate(7, new java.sql.Date(don.getNgayDuyet().getTime()));
        }
        pst.setString(8, don.getGhiChu());
        if (includeId) {
            pst.setInt(9, don.getMaDonXin());
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
                rs.getDate("ngayDuyet"),
                rs.getString("ghiChu"),
                rs.getTimestamp("ngayTao"),
                rs.getTimestamp("ngayCapNhat"));
        don.setTenNhanVien(rs.getString("tenNhanVien"));
        don.setTenLoaiDon(rs.getString("tenLoaiDon"));
        return don;
    }
}