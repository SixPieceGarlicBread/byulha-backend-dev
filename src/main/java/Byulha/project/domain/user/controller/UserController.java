package Byulha.project.domain.user.controller;

import Byulha.project.domain.user.model.dto.request.*;
import Byulha.project.domain.user.model.dto.response.*;
import Byulha.project.domain.user.service.SignupService;
import Byulha.project.domain.user.service.UserFindService;
import Byulha.project.domain.user.service.UserService;
import Byulha.project.global.auth.jwt.AppAuthentication;
import Byulha.project.global.auth.role.UserAuth;
import Byulha.project.infra.s3.model.dto.request.RequestUploadFileDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Tag(name = "사용자", description = "사용자 관련 api")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor

public class UserController {

    private final UserService userService;
    private final SignupService signupService;
    private final UserFindService userFindService;

    /**
     * 회원가입 토큰 생성
     *
     * @return 회원가입 토큰
     */
    @GetMapping("/signup-token")
    public ResponseSignupTokenDto generateSignupToken() {
        return signupService.generateSignupToken();
    }

    /**
     * 회원가입
     *
     * @param dto           요청 body
     * @param signupToken   회원가입 토큰
     *
     * 새로운 유저가 회원가입하거나 상태가 INACTIVE인 유저가 회원가입을 할 때 사용됩니다.
     */
    @PostMapping("/{signup-token}")
    public void signup(@Valid @RequestBody RequestSignupDto dto,
                       @PathVariable("signup-token") String signupToken) {
        signupService.signup(dto, signupToken);
    }

    /**
     * 로그인
     *
     * @param dto           요청 body
     * @return              로그인 인증 정보
     */
    @PostMapping("/login")
    public ResponseLoginDto login(@Valid @RequestBody RequestLoginDto dto) {
        return userService.login(dto);
    }

    /**
     * 토큰 재발급
     *
     * @param dto           요청 body
     * @return              새로 발급된 토큰
     */
    @UserAuth
    @PostMapping("/reissue")
    public ResponseReissueDto reissue(HttpServletRequest request,
                                      @Valid @RequestBody RequestReissueDto dto) {
        return userService.reissue(request, dto.getRefreshToken());
    }

    /**
     * 내 정보 조회
     *
     * @param auth         인증 정보
     */
    @GetMapping
    @UserAuth
    public ResponseUserInfoDto getUserInfo(AppAuthentication auth) {
        return userService.getUserInfo(auth.getUserId());
    }

    /**
     * 닉네임(아이디) 찾기
     *
     * @param phone         전화번호
     */
    @PostMapping("/find/nickname")
    public void sendNicknameBySMS(@RequestParam String phone) throws NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        userFindService.sendNicknameBySMS(phone);
    }

    /**
     * 휴대폰 재설정 인증 코드 전송 (1)
     * <p>재설정 코드(6자리) SMS로 전송 -> 재설정 토큰(UUID) 응답.</p> 핸드폰 번호 재설정 플로우는 SMS인증 코드 전송 ->
     * 인증 코드 확인 & 핸드폰 번호 변경 순으로 흘러갑니다.
     *
     * @param dto 요청 body
     * @return 핸드폰 번호 재설정 토큰
     */
    @UserAuth
    @PostMapping("/change/phone/verify")
    public ResponseChangeTokenDto sendChangePhoneCodeBySMS(AppAuthentication auth, @Valid @RequestBody RequestWithPhoneNumberDto dto) throws NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        return userFindService.sendChangePhoneCodeBySMS(auth.getUserId(), dto.getPhoneNumber());
    }

    /**
     * 휴대폰 재설정 인증 코드 확인 (2)
     * <p>재설정 토큰과 재설정 코드로 요청받은 번호로 핸드폰 번호 변경 합니다.</p>
     *
     * @param dto 요청 body
     */
    @PatchMapping("/change/phone")
    @UserAuth
    public void changePhoneNumber(AppAuthentication auth, @Valid @RequestBody RequestVerifyTokenCodeDto dto) {
        userFindService.changePhoneNumber(auth.getUserId(), dto.getToken(), dto.getCode());
    }

    /**
     * 이미지 업로드
     *
     * AWS S3에 사용자가 원하는 이미지를 업로드합니다.
     *
     * @param auth         인증 정보
     * @param dto          요청 body
     */
    @UserAuth
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, value = "/upload/image")
    public void uploadImage(AppAuthentication auth,
                            @Valid @ModelAttribute RequestUploadFileDto dto) {
        userService.uploadImage(auth.getUserId(), dto);
    }
}