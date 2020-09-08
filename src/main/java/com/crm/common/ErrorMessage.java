package com.crm.common;

public class ErrorMessage {
  // Bidding
  public static final String BIDDINGDOCUMENT_NOT_FOUND = "Không tìm thấy hồ sơ mời thầu";
  public static final String BIDDINGDOCUMENT_INVALID_CLOSING_TIME = "Thời gian đóng thầu phải sau thời gian hiện tại và trước thời gian đóng hàng";
  public static final String BIDDINGDOCUMENT_INVALID_OPENING_TIME = "Thời gian mở thầu phải sau thời gian đóng hàng";
  public static final String BIDDINGDOCUMENT_STATUS_NOT_FOUND = "Không tìm thấy trạng thái của hồ sơ mời thầu";
  public static final String BIDDINGDOCUMENT_IS_IN_TRANSACTION = "Hồ sơ mời thầu đã được ghép hoặc đang được đấu thầu";
  public static final String BIDDINGDOCUMENT_TIME_OUT = "Hồ sơ mời thầu đã hết hạn";
  public static final String BIDDINGDOCUMENT_ACCEPT_INVALID_BID = "Hồ sơ dự thầu đã hết hạn";
  public static final String BIDDINGDOCUMENT_CANNOT_CREATE_INVOICE = "Bạn không thể tạo hồ sơ mời thầu bởi bạn có hóa đơn chưa thanh toán";

  // Bid
  public static final String BID_NOT_FOUND = "Không tìm thấy hồ sơ dự thầu";
  public static final String BID_STATUS_NOT_FOUND = "Không tìm thấy trạng thái của hồ sơ dự thầu";
  public static final String BID_INVALID_EDIT = "Không thể sửa hồ sơ mời thầu sau khi đã được ghép hoặc hủy bỏ";
  public static final String BID_INVALID_CREATE = "Chỉ được tạo 1 hồ sơ dự thầu cho 1 hồ sơ đấu thầu";
  public static final String BID_INVALID_BID_PRICE = "Giá thầu phải bằng hoặc cao hơn giá sàn";
  public static final String BID_INVALID_PENDING_EDIT = "Hồ sơ dự thầu chỉ được sửa khi ở trạng thái chờ";
  public static final String BID_EDIT_BEFORE_FREEZE_TIME = "Vui lòng chỉnh sửa sau thời gian đóng băng";
  public static final String BID_INVALID_VALIDITY_PERIOD = "Thời gian hiệu lực của hồ sơ dự thầu phải sau thời gian hiện tại";
  public static final String BID_CANNOT_CREATE_INVOICE = "Bạn không thể tạo hồ sơ dự thầu bởi bạn có hóa đơn chưa thanh toán";

  // Combined
  public static final String COMBINED_NOT_FOUND = "Không tìm thấy hàng ghép";
  public static final String COMBINED_STATUS_NOT_FOUND = "Không tìm thấy trạng thái của hàng ghép";

  // ShippingInfo
  public static final String SHIPPING_INFO_NOT_FOUND = "Không tìm thấy chi tiết đơn vận chuyển";
  public static final String SHIPPING_INFO_INVALID_EDIT = "Không thể cập nhật trạng thái chi tiết đơn vận chuyển";
  public static final String SHIPPING_INFO_STATUS_NOT_FOUND = "Không tìm thấy trạng thái của việc vận chuyển hàng";

  // Contract
  public static final String CONTRACT_NOT_FOUND = "Không tìm thấy hợp đồng";
  public static final String CONTRACT_INVALID_FINES = "Phần trăm phạt hợp đồng không hợp lệ";
  public static final String CONTRACT_INVALID_EDIT = "Không thể sửa hợp đồng đã khai báo hóa đơn hợp lệ";

  // Evidence
  public static final String EVIDENCE_NOT_FOUND = "Không tìm thấy chứng cứ";
  public static final String EVIDENCE_INVALID = "Chứng cứ không hợp lệ";

  // Report
  public static final String REPORT_NOT_FOUND = "Không tìm thấy báo cáo";
  public static final String REPORT_STATUS_NOT_FOUND = "Không tìm thấy trạng thái của báo cáo";
  public static final String REPORT_INVALID_TIME = "Không thể tạo hoặc sửa báo cáo lúc này";
  public static final String REPORT_TITLE_INVALID_TIME = "Tiêu đề của báo cáo không hợp lệ";
  public static final String REPORT_DETAIL_INVALID_TIME = "Chi tiết báo cáo không hợp lệ";

  // Feedback
  public static final String FEEDBACK_NOT_FOUND = "Không tìm thấy phản hồi";
  public static final String FEEDBACK_INVALID_SATISFACTION_POINTS = "Điểm đánh giá không hợp lệ";
  public static final String FEEDBACK_INVALID_TIME = "Không thể tạo hoặc sửa phản hồi lúc này";

  // Rating
  public static final String RATING_NOT_FOUND = "Không tìm thấy đánh giá";
  public static final String RATING_ONE_PER_CONTRACT = "Bạn chỉ có thể đánh giá hợp đồng này một lần";

  // Container
  public static final String CONTAINER_LESS_THAN_NEEDED = "Số lượng container chưa đủ yêu cầu";
  public static final String CONTAINER_MORE_THAN_NEEDED = "Số lượng container vượt quá yêu cầu";
  public static final String CONTAINER_NOT_FOUND = "Không tìm thấy container";
  public static final String CONTAINER_NOT_FOUND_IN_BID = "Không tìm thấy container này trong hồ sơ dự thầu";
  public static final String CONTAINER_NOT_SUITABLE = "Container không phù hợp";
  public static final String CONTAINER_BUSY = "Container đã được dùng trong hồ sơ đấu thầu khác";
  public static final String CONTAINER_ALREADY_EXISTS = "Container đã tồn tại";
  public static final String CONTAINER_CAN_NOT_BE_ZERO = "Số lượng container không được nhỏ hơn 0";
  public static final String CONTAINER_CAN_NOT_ADD_OR_REMOVE = "Chỉ có thể thêm mới hoặc xóa container với Hồ sơ mời thầu có nhiều nhà thầu cùng thắng";

  // Supply
  public static final String SUPPLY_CODE_DUPLICATE = "Mã hàng đã tồn tại";

  // Supplier
  public static final String TIN_DUPLICATE = "Mã số thuế đã được sử dụng";
  public static final String FAX_DUPLICATE = "Địa chỉ thư điện tử đã được sử dụng";

  // Outbound
  public static final String OUTBOUND_NOT_FOUND = "Không tìm thấy hàng xuất khẩu";
  public static final String OUTBOUND_IS_IN_TRANSACTION = "Hàng nhập đã được đấu thầu";
  public static final String OUTBOUND_IS_NOT_YOUR = "Vui lòng chọn hàng xuất khẩu thuộc sở hữu của bạn";
  public static final String OUTBOUND_INVALID_DELIVERY_TIME = "Thời gian giao hàng phải sau thời gian đóng hàng";

  // Booking
  public static final String BOOKING_NOT_FOUND = "Không tìm thấy đơn đặt container";
  public static final String BOOKING_ALREADY_EXISTS = "Đơn vận đã tồn tại";
  public static final String BOOKING_BUSY = "Đơn đặt container này đang bận";

  // Inbound
  public static final String INBOUND_NOT_FOUND = "Không tìm thấy hàng nhập khẩu";
  public static final String INBOUND_IS_IN_TRANSACTION = "Hàng nhập khẩu đã được đấu thầu";
  public static final String INBOUND_INVALID_FREETIME = "Thời hạn dùng container phải sau thời gian nhận hàng";

  // Bill of lading
  public static final String BILLOFLADING_NOT_FOUND = "Không tìm thấy đơn vận";
  public static final String BILLOFLADING_INVALID_FREE_TIME = "Thời gian được sử dụng container không phù hợp";
  public static final String BILLOFLADING_ALREADY_EXISTS = "Đơn vận đã tồn tại";

  // Vehicle
  public static final String VEHICLE_LICENSE_PLATE_ALREADY_EXISTS = "Biển số xe đã tồn tại";

  // ContainerType

  public static final String CONTAINER_TYPE_NOT_FOUND = "Không tìm thấy loại container";
  public static final String CONTAINER_TYPE_ALREADY_EXISTS = "Loại container đã tồn tại";

  // Trailer
  public static final String TRAILER_NOT_FOUND = "Không tìm rơ móc";
  public static final String TRAILER_BUSY = "Rơ móc container đã được sử dụng";
  public static final String UNIT_OF_MEASUREMENT_NOT_FOUND = "Không tìm thấy đơn vị đo lường";
  public static final String TRAILER_TYPE_NOT_FOUND = "Không tìm thấy loại rơ móc";

  // Tractor
  public static final String TRACTOR_NOT_FOUND = "Không tìm đầu kéo";
  public static final String TRACTOR_BUSY = "Đầu kéo container đã được sử dụng";

  // Port
  public static final String PORT_NOT_FOUND = "Không tìm thấy cảng";
  public static final String PORT_ALREADY_EXISTS = "Cảng đã tồn tại";

  // User
  public static final String USER_NOT_FOUND = "Người dùng không tìm thấy";
  public static final String USERNAME_OR_PASSWORD_NOT_CORRECT = "Tài khoản hoặc mật khẩu không đúng";
  public static final String USER_STATUS_NOT_FOUND = "Không tìm thấy vai trò";
  public static final String USER_ADDRESS_NOT_FOUND = "Không tìm thấy địa chỉ trên";
  public static final String USER_ACCESS_DENIED = "Truy cập bị từ chối";
  public static final String RECIPIENT_NOT_FOUND = "Không tìm thấy người nhận";
  public static final String SENDER_NOT_FOUND = "Không tìm thấy người gửi";
  public static final String USER_EMAIL_ALREADY_EXISTS = "Email đã được sử dụng";
  public static final String USER_PHONE_ALREADY_EXISTS = "Số điện thoại đã được sử dụng";
  public static final String COMPANY_CODE_ALREADY_EXISTS = "Mã công ty đã được sử dụng";
  public static final String USERNAME_ALREADY_EXISTS = "Tên đăng nhập đã được sử dụng";
  public static final String PASSWORD_NOT_CORRECT = "Mật khẩu không đúng";
  public static final String NEW_PASSWORD_NOT_VALID = "Mật khẩu mới không hợp lệ";
  public static final String USER_ALREADY_EXISTS = "Tên đăng nhập, số điện thoại, email hoặc mã công ty đã tồn tại";
  public static final String INVALID_RESET_PASSWORD_TOKEN = "Không tim thấy tài khoản hoặc đường dẫn đã hết hạn";
  public static final String BANNED_ACCOUNT = "Tài khoản của bạn đã bị khóa vui lòng liên hệ tới quản trị viên để được trợ giúp";

  // Operator
  public static final String OPERATOR_NOT_FOUND = "Không tìm thấy quản trị viên";

  // ShippingLine
  public static final String SHIPPINGLINE_NOT_FOUND = "Không tìm thấy hãng tàu";
  public static final String SHIPPINGLINE_ALREADY_EXISTS = "Hãng tàu đã tồn tại";

  // Merchant
  public static final String MERCHANT_NOT_FOUND = "Không tìm thấy chủ hàng";

  // Forwarder
  public static final String FORWARDER_NOT_FOUND = "Không tìm thấy chủ xe";

  // Driver
  public static final String DRIVER_LICENSE_ALREADY_EXIST = "Bằng lái xe đã được xử dụng";
  public static final String DRIVER_BUSY = "Lái xe đã được ghép trong hồ sơ đấu thầu khác";
  public static final String DRIVER_NOT_FOUND = "Không tìm thấy lái xe";

  // Geolocation
  public static final String GEOLOCATION_NOT_FOUND = "Không tìm thấy vị trí của lái xe";

  // Role
  public static final String ROLE_NOT_FOUND = "Không tìm thấy vai trò";
  public static final String ROLE_ALREADY_EXISTS = "Vai trò đã tồn tại";

  // Permission
  public static final String PERMISSION_NOT_FOUND = "Không tìm thấy phân quyền";
  public static final String PERMISSION_ALREADY_EXISTS = "Phân quyền đã tồn tại";

  // Discount
  public static final String DISCOUNT_NOT_FOUND = "Mã giảm giá đã hết hạn hoặc không tìm thấy";
  public static final String DISCOUNT_ALREADY_EXISTS = "Mã giảm giá đã tồn tại";

  // Currency
  public static final String CURRENCY_NOT_FOUND = "Không tìm thấy loại tiền tệ";

  // Notification
  public static final String NOTIFICATION_NOT_FOUND = "Không tìm thấy thông báo";
  public static final String NOTIFICATION_RELATED_RESOURCE_NOT_FOUND = "Không tìm thấy đối tượng liên quan";

  // Payment
  public static final String PAYMENT_NOT_FOUND = "Không tìm thấy hóa đơn";
  public static final String PAYMENT_INVALID_AMOUNT = "Sô tiền không hợp lệ";
  public static final String PAYMENT_TYPE_NOT_FOUND = "Không tìm thấy loại hóa đơn";

  // QRToken
  public static final String QRTOKEN_NOT_FOUND = "Mã QR không tồn tại";
  public static final String QRTOKEN_EXPIRED = "Mã QR đã hết hạn";

  // Combined
  public static final String COMBINED_INVALID_CANCEL = "Không thể hủy hàng ghép lúc này";

}
