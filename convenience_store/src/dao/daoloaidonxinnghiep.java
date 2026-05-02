package dao;

import dto.dtoloaidonxinnghiep;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class daoloaidonxinnghiep {

    public ArrayList<dtoloaidonxinnghiep> getList() {
        ArrayList<dtoloaidonxinnghiep> list = new ArrayList<>();
        String sql = "SELECT * FROM loaidonxinnghiep ORDER BY maLoaiDon DESC";

        try (Connection con = connect.connection(); PreparedStatement pst = con.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                list.add(new dtoloaidonxinnghiep(rs.getInt("maLoaiDon"), rs.getString("tenLoaiDon"), rs.getString("moTa")));
            }
        } catch (SQLException ex) {
            Logger.getLogger(daoloaidonxinnghiep.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    public dtoloaidonxinnghiep getById(int maLoaiDon) {
        String sql = "SELECT * FROM loaidonxinnghiep WHERE maLoaiDon = ?";
        try (Connection con = connect.connection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, maLoaiDon);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new dtoloaidonxinnghiep(rs.getInt("maLoaiDon"), rs.getString("tenLoaiDon"), rs.getString("moTa"));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(daoloaidonxinnghiep.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void add(dtoloaidonxinnghiep loaiDon) {
        String sql = "INSERT INTO loaidonxinnghiep (tenLoaiDon, moTa) VALUES (?, ?)";
        try (Connection con = connect.connection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, loaiDon.getTenLoaiDon());
            pst.setString(2, loaiDon.getMoTa());
            pst.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(daoloaidonxinnghiep.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void update(dtoloaidonxinnghiep loaiDon) {
        String sql = "UPDATE loaidonxinnghiep SET tenLoaiDon = ?, moTa = ? WHERE maLoaiDon = ?";
        try (Connection con = connect.connection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, loaiDon.getTenLoaiDon());
            pst.setString(2, loaiDon.getMoTa());
            pst.setInt(3, loaiDon.getMaLoaiDon());
            pst.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(daoloaidonxinnghiep.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void delete(int maLoaiDon) {
        String sql = "DELETE FROM loaidonxinnghiep WHERE maLoaiDon = ?";
        try (Connection con = connect.connection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, maLoaiDon);
            pst.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(daoloaidonxinnghiep.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}