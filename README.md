# Convenience Store Management System

Hệ thống quản lý cửa hàng tiện lợi được xây dựng bằng Java Swing và MySQL, mô phỏng quy trình vận hành của một cửa hàng bán lẻ từ bán hàng, nhập kho, chăm sóc khách hàng đến quản trị nhân sự và báo cáo.

Dự án được phát triển với định hướng chuyển đổi yêu cầu nghiệp vụ thành các chức năng phần mềm cụ thể, phù hợp để minh họa năng lực phân tích quy trình và triển khai giải pháp trong hồ sơ ứng tuyển vị trí **IT Consultant Intern**.

<!-- Thêm ảnh tổng quan giao diện chính của hệ thống tại đây -->
<!-- Ví dụ:
![Tổng quan hệ thống](docs/images/overview.png)
-->

## Business Problem

Một cửa hàng tiện lợi cần quản lý đồng thời nhiều nghiệp vụ:

- Bán hàng nhanh tại quầy nhưng vẫn theo dõi được hóa đơn và tồn kho.
- Kiểm soát nhập hàng, nhà cung cấp, hạn sử dụng và số lượng tồn.
- Quản lý khách hàng, điểm thưởng, ưu đãi và chương trình khuyến mãi.
- Theo dõi nhân viên, hợp đồng, lương và nghỉ phép.
- Phân quyền người dùng và cung cấp báo cáo cho quản lý.

Nếu xử lý thủ công hoặc bằng nhiều file rời rạc, dữ liệu dễ trùng lặp, khó kiểm soát và khó đưa ra quyết định kinh doanh. Hệ thống này gom các nghiệp vụ đó vào một ứng dụng desktop thống nhất.

## Solution Overview

Ứng dụng hỗ trợ nhiều nhóm người dùng trong cùng một quy trình vận hành:

- **Nhân viên bán hàng** tạo đơn, thanh toán và xuất hóa đơn.
- **Nhân viên kho** quản lý sản phẩm, nhà cung cấp và phiếu nhập.
- **Quản lý** theo dõi doanh thu, hiệu quả sản phẩm, nhân sự và phân quyền tài khoản.

Mã nguồn được tổ chức theo các lớp `DTO`, `DAO`, `BUS`, `GUI` để tách biệt dữ liệu, truy cập cơ sở dữ liệu, xử lý nghiệp vụ và giao diện.

## Business Flow

<!-- Có thể thêm sơ đồ quy trình nghiệp vụ tại đây -->
<!-- Ví dụ:
![Business flow](docs/images/business-flow.png)
-->

```text
Nhập hàng -> Cập nhật tồn kho -> Bán hàng -> Lập hóa đơn
        -> Tích điểm/khuyến mãi -> Thống kê doanh thu và sản phẩm
```

Song song với luồng bán lẻ, hệ thống còn hỗ trợ:

```text
Quản lý nhân viên -> Hợp đồng -> Lương -> Nghỉ phép -> Phân quyền tài khoản
```

## Key Features

- Đăng nhập, đăng ký tài khoản và khôi phục mật khẩu.
- Bán hàng, giỏ hàng, thanh toán và in/xuất hóa đơn.
- Quản lý sản phẩm, danh mục, phân loại, nhà cung cấp và phiếu nhập.
- Theo dõi khách hàng, tích điểm, ưu đãi và khuyến mãi.
- Quản lý nhân viên, chức vụ, hợp đồng, lương và đơn xin nghỉ phép.
- Quản lý tài khoản, chức năng và phân quyền người dùng.
- Thống kê doanh thu, số lượng bán và hiệu quả sản phẩm.

## Screenshots

<!-- Thêm ảnh các màn hình nổi bật của dự án tại đây -->
<!-- Gợi ý nên có:
1. Màn hình đăng nhập
2. Màn hình bán hàng
3. Màn hình quản lý sản phẩm hoặc phiếu nhập
4. Màn hình thống kê doanh thu
5. Màn hình phân quyền người dùng
-->

<!-- Ví dụ:
| Bán hàng | Thống kê |
| --- | --- |
| ![Bán hàng](docs/images/sales.png) | ![Thống kê](docs/images/reports.png) |

| Quản lý sản phẩm | Phân quyền |
| --- | --- |
| ![Sản phẩm](docs/images/products.png) | ![Phân quyền](docs/images/permissions.png) |
-->

## Consultant-Oriented Highlights

- Chuyển đổi bài toán vận hành cửa hàng bán lẻ thành các module phần mềm rõ ràng.
- Mô hình hóa luồng nghiệp vụ từ nhập hàng đến bán hàng và báo cáo quản trị.
- Thiết kế cơ sở dữ liệu MySQL để liên kết sản phẩm, hóa đơn, phiếu nhập, khách hàng và nhân sự.
- Tổ chức hệ thống theo lớp nghiệp vụ, giúp dễ bảo trì và mở rộng khi yêu cầu thay đổi.
- Hỗ trợ góc nhìn quản trị thông qua phân quyền và thống kê.

<!-- Có thể thêm sơ đồ kiến trúc hoặc ERD tại đây -->
<!-- Ví dụ:
![Sơ đồ kiến trúc](docs/images/architecture.png)
![ERD](docs/images/erd.png)
-->

## Tech Stack

- Java Swing
- MySQL
- Apache Ant / NetBeans project
- MySQL Connector/J
- FlatLaf
- JasperReports
- JFreeChart
- Apache POI

## Project Structure

```text
convenience_store/
├── database/        # Script SQL và dữ liệu mẫu
├── lib/             # Thư viện phụ thuộc
├── src/
│   ├── bus/         # Xử lý nghiệp vụ
│   ├── dao/         # Truy cập dữ liệu
│   ├── dto/         # Đối tượng truyền dữ liệu
│   ├── gui/         # Giao diện Swing
│   ├── helper/      # Tiện ích dùng chung
│   └── source/      # Ảnh sản phẩm, hóa đơn và tài nguyên khác
├── test/            # Mã kiểm thử
├── build.xml        # Cấu hình Ant
└── nbproject/       # Cấu hình NetBeans
```

## Database Setup

1. Import script:

   `convenience_store/database/qlcuahangtienloi.sql`

2. Script sẽ tạo database:

   `qlcuahangtienloi`

3. Cập nhật thông tin kết nối trong:

   `convenience_store/src/dao/connect.java`

   Cấu hình mặc định hiện tại:

   ```java
   jdbc:mysql://localhost:3306/qlcuahangtienloi
   username = "root"
   ```

   Hãy thay mật khẩu cho khớp với cấu hình MySQL trên máy của bạn trước khi chạy.

## How To Run

### Run With NetBeans

1. Mở thư mục `convenience_store` bằng NetBeans.
2. Kiểm tra kết nối database.
3. Chạy project hoặc chạy class `Convenience_store`.

### Build With Apache Ant

Từ thư mục gốc repo:

```bash
cd convenience_store
ant clean jar
```

File JAR sau khi build:

```text
convenience_store/dist/convenience_store.jar
```

Class khởi động:

```text
Convenience_store
```

Khi chạy, ứng dụng mở màn hình đăng nhập trước khi vào giao diện chính.

## Environment

- JDK phù hợp với cấu hình dự án. File hiện tại đặt `javac.source=25` và `javac.target=25`, vì vậy nên dùng JDK 25 nếu giữ nguyên cấu hình.
- MySQL 8.x
- NetBeans hoặc Apache Ant

## Notes

- Dự án hiện khai báo thông tin kết nối database trực tiếp trong mã nguồn, vì vậy mỗi máy cần tự chỉnh file `connect.java`.
- File `convenience_store/nbproject/project.properties` hiện còn chứa dấu vết merge conflict và một số đường dẫn thư viện cục bộ; nên rà soát lại trước khi chia sẻ rộng rãi hoặc build trên máy khác.
