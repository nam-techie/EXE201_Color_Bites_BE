package com.exe201.color_bites_be.service;

import com.exe201.color_bites_be.dto.request.LoginRequest;
import com.exe201.color_bites_be.dto.request.RegisterRequest;
import com.exe201.color_bites_be.dto.response.AccountResponse;
import com.exe201.color_bites_be.dto.response.CloudinaryResponse;
import com.exe201.color_bites_be.entity.Account;
import com.exe201.color_bites_be.entity.UserInformation;
import com.exe201.color_bites_be.enums.LoginMethod;
import com.exe201.color_bites_be.enums.Role;
import com.exe201.color_bites_be.enums.SubcriptionPlan;
import com.exe201.color_bites_be.exception.DuplicateEntity;
import com.exe201.color_bites_be.exception.NotFoundException;
import com.exe201.color_bites_be.model.UserPrincipal;
import com.exe201.color_bites_be.repository.AccountRepository;
import com.exe201.color_bites_be.repository.UserInformationRepository;
import com.exe201.color_bites_be.util.FileUpLoadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
public class AuthenticationService implements UserDetailsService {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserInformationRepository userInformationRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    private TokenService tokenService;

    @Autowired
    CloudinaryService cloudinaryService;

    @Autowired
    private AuthenticationManager authenticationManager;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    @Transactional
    public AccountResponse register(RegisterRequest registerRequest) {
        try {
            // Kiểm tra confirmPassword trước khi tiếp tục
            if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
                throw new IllegalArgumentException("Mật khẩu và xác nhận mật khẩu không khớp!");
            }

            // Kiểm tra trùng lặp email và username trước khi lưu vào cơ sở dữ liệu
            if (accountRepository.existsByEmail(registerRequest.getEmail())) {
                throw new DuplicateEntity("Email này đã được sử dụng!");
            }

            // Kiểm tra trùng lặp username
            if (accountRepository.existsByUserName(registerRequest.getUsername())) {
                throw new DuplicateEntity("Username này đã tồn tại!");
            }

            Account account = modelMapper.map(registerRequest, Account.class);

            //auto set role student
            account.setIsActive(true);
            account.setRole(Role.USER.name());
            account.setLoginMethod(LoginMethod.USERNAME);

            // Set thời gian thực khi tạo account
            LocalDateTime now = LocalDateTime.now();
            account.setCreatedAt(now);
            // Lần đầu register thì updatedAt = createdAt
            account.setUpdatedAt(now);

            // Mã hóa mật khẩu trước khi lưu vào cơ sở dữ liệu
            account.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            account.setEmail(registerRequest.getEmail());
            account.setUserName(registerRequest.getUsername());

            Account newAccount = accountRepository.save(account);

            // Tự động tạo UserInformation với subscription plan FREE
            UserInformation userInformation = new UserInformation();
            userInformation.setAccount(newAccount);
            userInformation.setSubscriptionPlan(SubcriptionPlan.FREE.name());
            userInformation.setCreatedAt(LocalDateTime.now());
            userInformation.setUpdatedAt(LocalDateTime.now());
            userInformationRepository.save(userInformation);

            return modelMapper.map(newAccount, AccountResponse.class);
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            throw new RuntimeException("Đã xảy ra lỗi trong quá trình đăng ký: " + e.getMessage());
        } catch (DuplicateEntity e) { // Xử lý lỗi duplicate với encoding đúng (không bị mã hóa lại)
            throw new DuplicateEntity(e.getMessage()); // Trả về lỗi trùng lặp như trước đây (không bị mã hóa lại)
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Đã xảy ra lỗi không xác định: " + e.getMessage());
        }
    }

    public AccountResponse login(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()));

            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            Account account = userPrincipal.getAccount();

            if (!account.getIsActive()) {
                throw new NotFoundException("Tài khoản đã bị vô hiệu hóa!");
            }

            // Cập nhật thời gian login cuối cùng
            account.setUpdatedAt(LocalDateTime.now());
            accountRepository.save(account);

            // tạo token cho tài khoản
            AccountResponse accountResponse = modelMapper.map(account, AccountResponse.class);
            if (authentication.isAuthenticated()) {
                accountResponse.setToken(tokenService.generateToken(account));
            }
            return accountResponse;
        } catch (NotFoundException e) {
            // Nếu tài khoản bị vô hiệu hóa
            throw new NotFoundException("Tài khoản đã bị vô hiệu hóa!");
        } catch (BadCredentialsException e) {
            // Nếu thông tin tài khoản hoặc mật khẩu sai
            throw new RuntimeException("Email hoặc mật khẩu sai!");
        } catch (Exception e) {
            // Xử lý các lỗi khác
            e.printStackTrace();
            throw new RuntimeException("Đã xảy ra lỗi trong quá trình đăng nhập, vui lòng thử lại sau.");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("Tài khoản không tồn tại: " + username));
        return new UserPrincipal(account);
    }

    // Thêm method logout
    public void logout(String token) {
        tokenService.invalidateToken(token);
    }

    /**
     * Method tiện ích để update account với thời gian thực
     *
     * @param account Account cần update
     * @return Account đã được update
     */
    public Account updateAccountWithCurrentTime(Account account) {
        account.setUpdatedAt(LocalDateTime.now());
        return accountRepository.save(account);
    }

    /**
     * Method để update thông tin account (ví dụ: thay đổi password, email, etc.)
     *
     * @param accountId      ID của account
     * @param updatedAccount Account với thông tin mới
     * @return Account đã được update
     */
    public Account updateAccount(String accountId, Account updatedAccount) {
        Account existingAccount = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy tài khoản với ID: " + accountId));

        // Cập nhật các trường cần thiết (giữ nguyên createdAt)
        existingAccount.setUserName(updatedAccount.getUserName());
        existingAccount.setEmail(updatedAccount.getEmail());
        existingAccount.setRole(updatedAccount.getRole());
        existingAccount.setIsActive(updatedAccount.getIsActive());
        existingAccount.setLoginMethod(updatedAccount.getLoginMethod());
        existingAccount.setGoogleId(updatedAccount.getGoogleId());

        // Tự động set updatedAt với thời gian hiện tại
        existingAccount.setUpdatedAt(LocalDateTime.now());

        return accountRepository.save(existingAccount);
    }

    @Transactional
    public String uploadImage(String id, final MultipartFile file) {
        final UserInformation userInformation = userInformationRepository.findByAccountId(id);
        if (userInformation == null) {
            throw new NotFoundException("Người dùng không tồn tại.");
        }
        FileUpLoadUtil.assertAllowed(file, FileUpLoadUtil.IMAGE_PATTERN);
        final String fileName = FileUpLoadUtil.getFileName(file.getOriginalFilename());
        final CloudinaryResponse cloudinaryResponse = cloudinaryService.uploadFile(file, fileName);
        userInformation.setAvatarUrl(cloudinaryResponse.getUrl());
        userInformationRepository.save(userInformation);
        return userInformation.getAvatarUrl();
    }

//    @Transactional
//    public void uploadVideo(String id, final MultipartFile file) {
//        final UserInformation userInformation = userInformationRepository.findByAccountId(id);
//        if (userInformation == null) {
//            throw new NotFoundException("Người dùng không tồn tại.");
//        }
//        FileUpLoadUtil.assertAllowed(file, FileUpLoadUtil.VIDEO_PATTERN);
//        final String fileName = FileUpLoadUtil.getFileName(file.getOriginalFilename());
//        final CloudinaryResponse cloudinaryResponse = cloudinaryService.uploadVideo(file, fileName);
//        userInformation.setVideoUrl(cloudinaryResponse.getUrl());
//        userInformationRepository.save(userInformation);
//    }
}
