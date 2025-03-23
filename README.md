# Blockchain Library for Java

Blockchain cơ bản được viết bằng Java Spring Boot, cung cấp các thành phần cốt lõi để xây dựng ứng dụng dựa trên 
blockchain.

## Tính năng

- Cấu trúc dữ liệu cơ bản của blockchain: Block, Transaction, Wallet
- Thuật toán đồng thuận Proof of Work
- Mạng P2P đơn giản
- REST API để tương tác với blockchain
- Lưu trữ dữ liệu blockchain
- Xác thực chữ ký và bảo mật giao dịch

## Yêu cầu hệ thống

- Java 17 hoặc cao hơn
- Gradle
- Docker (tùy chọn)

## Cài đặt

### Xây dựng từ mã nguồn

```bash
# Clone repository
git clone https://github.com/yourusername/blockspring.git
cd blockspring

# Xây dựng dự án
./gradlew build
```

### Sử dụng Docker

```bash
# Xây dựng image Docker
docker build -t blockspring .

# Chạy container
docker run -p 8080:8080 -p 9090:9090 blockspring
```

## Sử dụng

### Chạy ứng dụng

```bash
java -jar build/libs/blockspring-0.1.0-SNAPSHOT.jar
```

### Tương tác với Blockchain thông qua API

#### Lấy toàn bộ blockchain

```
GET /api/blockchain/chain
```

#### Thêm mới một giao dịch

```
POST /api/blockchain/transaction/new
Content-Type: application/json

{
  "sender": "address1",
  "recipient": "address2", 
  "amount": 10
}
```

#### Đào khối mới

```
GET /api/blockchain/mine
```

#### Quản lý các node (peers)

```
# Thêm node mới
POST /api/blockchain/peers/add
Content-Type: application/json

{
  "address": "127.0.0.1:8081"
}

# Lấy danh sách các node
GET /api/blockchain/peers
```

## Cấu trúc dự án

```
blockspring/
├── src/main/java/com/blockchain/
│   ├── core/              # Thành phần cốt lõi (Block, Transaction, etc.)
│   ├── crypto/            # Các hàm mã hóa và bảo mật
│   ├── consensus/         # Thuật toán đồng thuận
│   ├── network/           # Giao tiếp P2P
│   ├── storage/           # Lưu trữ dữ liệu
│   ├── config/            # Cấu hình ứng dụng
│   ├── exception/         # Xử lý ngoại lệ
│   └── BlockchainApplication.java  # Lớp chính
```

## Tích hợp vào dự án của bạn

Để sử dụng blockspring trong dự án Java Spring Boot của bạn, hãy thêm dependency sau vào file build.gradle:

```groovy
implementation 'com.blockchain:blockspring:0.1.0-SNAPSHOT'
```

Sau đó, bạn có thể sử dụng các lớp cốt lõi như Blockchain, Block, Transaction, và Wallet trong mã nguồn của mình.

## Phát triển

### Mở rộng thuật toán đồng thuận

Bạn có thể triển khai các thuật toán đồng thuận khác như Proof of Stake bằng cách thêm lớp mới trong package `com.blockchain.consensus`.

### Thêm chức năng mới

blockspring được thiết kế để dễ dàng mở rộng. Bạn có thể thêm tính năng mới bằng cách:

1. Thêm các phương thức vào các lớp hiện có
2. Tạo lớp mới trong package phù hợp
3. Mở rộng API trong NodeController

## Đóng góp

Đóng góp luôn được chào đón! Vui lòng tạo pull request hoặc issue để đóng góp cho dự án.

## Giấy phép

MIT License