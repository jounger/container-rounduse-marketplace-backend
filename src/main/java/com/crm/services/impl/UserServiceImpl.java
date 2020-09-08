package com.crm.services.impl;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.xml.bind.DatatypeConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.crm.common.Constant;
import com.crm.common.ErrorMessage;
import com.crm.common.Tool;
import com.crm.enums.EnumUserStatus;
import com.crm.exception.DuplicateRecordException;
import com.crm.exception.NotFoundException;
import com.crm.models.FileUpload;
import com.crm.models.PasswordResetToken;
import com.crm.models.Role;
import com.crm.models.User;
import com.crm.payload.request.ChangePasswordRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.SignUpRequest;
import com.crm.repository.PasswordResetTokenRepository;
import com.crm.repository.RoleRepository;
import com.crm.repository.UserRepository;
import com.crm.services.UserService;
import com.crm.specification.builder.UserSpecificationsBuilder;

@Service
public class UserServiceImpl implements UserService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private PasswordResetTokenRepository passwordResetTokenRepository;

  @Autowired
  private JavaMailSender javaMailSender;

  @Override
  public User createUser(SignUpRequest request) {
    if (userRepository.existsByUsername(request.getUsername()) || userRepository.existsByEmail(request.getEmail())
        || userRepository.existsByPhone(request.getPhone())) {
      throw new DuplicateRecordException(ErrorMessage.USER_ALREADY_EXISTS);
    }
    User user = new User();
    user.setUsername(request.getUsername());
    user.setEmail(request.getEmail());
    user.setPhone(request.getPhone());
    user.setStatus(EnumUserStatus.PENDING.name());
    Set<String> rolesString = request.getRoles();
    Set<Role> roles = new HashSet<>();

    if (rolesString == null) {
      Role userRole = roleRepository.findByName("ROLE_OTHER")
          .orElseThrow(() -> new NotFoundException(ErrorMessage.ROLE_NOT_FOUND));
      roles.add(userRole);
    } else {
      rolesString.forEach(role -> {
        for (int i = 0; i < rolesString.size(); i++) {
          Role userRole = roleRepository.findByName(role)
              .orElseThrow(() -> new NotFoundException(ErrorMessage.ROLE_NOT_FOUND));
          roles.add(userRole);
        }
      });
    }
    user.setRoles(roles);
    String address = request.getAddress();
    if (address == null) {
      throw new NotFoundException(ErrorMessage.USER_ADDRESS_NOT_FOUND);
    } else {
      user.setAddress(address);
    }
    String encoder = passwordEncoder.encode(request.getPassword());
    user.setPassword(encoder);

    User _user = userRepository.save(user);
    return _user;
  }

  @Override
  public Page<User> getUsers(PaginationRequest request) {
    Page<User> pages = null;
    if (request.getStatus() == null) {
      pages = userRepository
          .findAll(PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
    } else {
      pages = userRepository.findByStatus(EnumUserStatus.findByName(request.getStatus()),
          PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
    }
    return pages;
  }

  @Override
  public Page<User> searchUsers(PaginationRequest request, String search) {
    // Extract data from search string
    UserSpecificationsBuilder builder = new UserSpecificationsBuilder();
    Pattern pattern = Pattern.compile(Constant.SEARCH_REGEX, Pattern.UNICODE_CHARACTER_CLASS);
    Matcher matcher = pattern.matcher(search + ",");
    while (matcher.find()) {
      // Chaining criteria
      builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
    }
    // Build specification
    Specification<User> spec = builder.build();
    PageRequest page = PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt"));
    // Filter with repository
    Page<User> pages = userRepository.findAll(spec, page);
    // Return result
    return pages;
  }

  @Override
  public User editProfileImage(String username, FileUpload profileImage) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND));

    if (profileImage != null) {
//      user.setProfileImage(profileImage);
    }

    User _user = userRepository.save(user);

    return _user;
  }

  @Override
  public User editUser(String username, Map<String, Object> updates) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND));

    String status = String.valueOf(updates.get("status"));
    EnumUserStatus eStatus = EnumUserStatus.findByName(status);
    if (status != null && eStatus != null) {
      user.setStatus(eStatus.name());
    }

    String profileImagePath = String.valueOf(updates.get("profileImagePath"));
    if (updates.get("profileImagePath") != null) {
      user.setProfileImagePath(profileImagePath);
    }

    User _user = userRepository.save(user);
    return _user;
  }

  @Override
  public List<User> getUsersByRole(String roleName) {
    List<User> users = userRepository.findByRole(roleName);
    if (users.size() == 0) {
      throw new NotFoundException("Error: User is not found");
    }
    return users;
  }

  @Override
  public User changePassword(String username, ChangePasswordRequest request) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND));
    if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
      throw new NotFoundException(ErrorMessage.PASSWORD_NOT_CORRECT);
    }
    user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    User _user = userRepository.save(user);
    return _user;
  }

  @Override
  public void getResetPasswordToken(String email) throws MessagingException, IOException {
    User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND));

    PasswordResetToken passwordResetToken = new PasswordResetToken();
    passwordResetToken.setUser(user);
    passwordResetToken.setExpiredDate(LocalDateTime.now().plusHours(3));

    String rawToken = Tool.randomString();
    // encode data using BASE64
    String token = DatatypeConverter.printBase64Binary(rawToken.getBytes());
    passwordResetToken.setToken(token);

    passwordResetTokenRepository.save(passwordResetToken);

    sendEmailWithAttachment(email, token);
  }

  void sendEmailWithAttachment(String email, String token) throws MessagingException, IOException {

    MimeMessage msg = javaMailSender.createMimeMessage();

    // true = multipart message
    MimeMessageHelper helper = new MimeMessageHelper(msg, true);
    helper.setTo(email);

    helper.setSubject("[CRuM] Đặt lại mật khẩu");

    // default = text/plain
    // helper.setText("Check attachment for image!");

    // true = text/html
    helper.setText("<p>Chúng tôi biết được bạn đã quên mật khẩu tại CRuM. Xin lỗi vì sự bất tiện này!</p>" + "<p></p>"
        + "<p>Đừng lo lắng! Bạn có thể sử dụng đường dẫn dưới đây để cài lại mật khẩu của mình:</p>" + "<p></p>"
        + "<a href=\'http://containerrounduse.com/reset-password/" + token
        + "\'>http://containerrounduse.com/reset-password/" + token + "</a>" + "<p></p>"
        + "<p>Đường dẫn trên sẽ hết hạn trong vòng 3 tiếng nếu bạn không sử dụng. Để lấy đường dẫn đặt lại mật khẩu mới, "
        + "hãy truy cập </p><a href=\'http://containerrounduse.com/forgot-password\'>http://containerrounduse.com/forgot-password</a>"
        + "<p>Trân trọng,</p>" + "<p>Container Round User Inc</p>", true);
    // helper.addAttachment("my_photo.png", new ClassPathResource("android.png"));

    javaMailSender.send(msg);

  }

  @Override
  public Boolean isValidResetPasswordTolken(String token) {
    PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.INVALID_RESET_PASSWORD_TOKEN));
    if (resetToken.getExpiredDate().isAfter(LocalDateTime.now())) {
      return true;
    } else {
      passwordResetTokenRepository.delete(resetToken);
      return false;
    }

  }

  @Override
  public void resetPasswordByToken(String token, String newPassword) {
    // split token
    PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.INVALID_RESET_PASSWORD_TOKEN));
    // check valid new password
    if (newPassword.length() < 6 && newPassword.length() > 120) {
      throw new InvalidParameterException(ErrorMessage.NEW_PASSWORD_NOT_VALID);
    }
    // set new password
    User user = passwordResetToken.getUser();
    String password = passwordEncoder.encode(newPassword);
    user.setPassword(password);

    passwordResetTokenRepository.delete(passwordResetToken);

    userRepository.save(user);
  }

}
