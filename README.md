# Convenience Store Management

Ứng dụng quản lý cửa hàng tiện lợi viết bằng Java Swing, kết nối MySQL, theo mô hình DTO/DAO/BUS. Giao diện gồm các phân hệ bán hàng, kho, nhân viên, khách hàng, thống kê.

## Chức năng chính
- Đăng nhập/đăng ký tài khoản
- Bán hàng (Menu, giỏ hàng, thanh toán)
- Quản lý sản phẩm, phân loại, nhà cung cấp, phiếu nhập
- Quản lý nhân viên, chấm công, lương, hợp đồng
- Khách hàng, ưu đãi/khuyến mãi
- Thống kê doanh thu và thống kê sản phẩm

## Công nghệ
- Java Swing
- MySQL
- Ant (build.xml)

## Yêu cầu
- JDK 8+
- MySQL 8+

## Cấu hình cơ sở dữ liệu
1. Tạo database và dữ liệu mẫu từ file [convenience_store/database/qlcuahangtienloi.sql](convenience_store/database/qlcuahangtienloi.sql).
2. Kiểm tra thông tin kết nối trong [convenience_store/src/dao/connect.java](convenience_store/src/dao/connect.java).

## Chạy ứng dụng
Chạy class `Convenience_store` trong [convenience_store/src/Convenience_store.java](convenience_store/src/Convenience_store.java). Ứng dụng sẽ mở màn hình đăng nhập trước khi vào trang chính.

## Cấu trúc thư mục
- convenience_store/src: mã nguồn chính
- convenience_store/database: script SQL
- convenience_store/lib: thư viện phụ trợ
- convenience_store/build.xml: cấu hình Ant


