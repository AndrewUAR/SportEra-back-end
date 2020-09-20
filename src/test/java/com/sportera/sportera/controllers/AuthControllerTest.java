package com.sportera.sportera.controllers;

import com.sportera.sportera.TestUtil;
import com.sportera.sportera.errors.ApiError;
import com.sportera.sportera.models.User;
import com.sportera.sportera.payloads.response.JwtResponse;
import com.sportera.sportera.repositories.UserRepository;
import com.sportera.sportera.services.UserService;
import com.sportera.sportera.shared.GenericResponse;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AuthControllerTest {

    private static final String API_1_0_SIGNUP = "/api/1.0/signup";

    private static final String API_1_0_SIGNIN = "/api/1.0/signin";

    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    AuthenticationManager authenticationManager;

    @AfterEach
    public void cleanUp() {
        userRepository.deleteAll();
        testRestTemplate.getRestTemplate().getInterceptors().clear();
    }


    public <T> ResponseEntity<T> postSignup(Object request, Class<T> response) {
        return testRestTemplate.postForEntity(API_1_0_SIGNUP, request, response);
    }

    @Test
    public void injectedComponentsAreNotNull() {
        assertThat(testRestTemplate).isNotNull();
        assertThat(userRepository).isNotNull();
        assertThat(userService).isNotNull();
    }

    @Test
    public void postUser_whenUserIsValid_receiveOK() {
        User user = TestUtil.createValidUser();
        ResponseEntity<Object> response = testRestTemplate.postForEntity(API_1_0_SIGNUP, user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void postUser_whenUserIsValid_userSavedToDatabase() {
        User user = TestUtil.createValidUser();
        testRestTemplate.postForEntity(API_1_0_SIGNUP, user, Object.class);
        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    public void postUser_whenUserIsValid_receiveSuccessMessage() {
        User user = TestUtil.createValidUser();
        ResponseEntity<GenericResponse> response = postSignup(user, GenericResponse.class);
        assertThat(response.getBody().getMessage()).isNotNull();
    }

    @Test
    public void postUser_whereUserIsValid_passwordIsHashedInDatabase() {
        User user = TestUtil.createValidUser();
        testRestTemplate.postForEntity(API_1_0_SIGNUP, user, Object.class);
        List<User> users = userRepository.findAll();
        User inDB = users.get(0);
        assertThat(inDB.getPassword()).isNotEqualTo(user.getPassword());
    }

    @Test
    public void postUser_whenUserHasNullUsername_receiveBadRequest() {
        User user = TestUtil.createValidUser();
        user.setUsername(null);
        ResponseEntity<Object> response = postSignup(user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasNullEmail_receiveBadRequest() {
        User user = TestUtil.createValidUser();
        user.setEmail(null);
        ResponseEntity<Object> response = postSignup(user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasNullPassword_receiveBadRequest() {
        User user = TestUtil.createValidUser();
        user.setPassword(null);
        ResponseEntity<Object> response = postSignup(user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasUsernameLessThanRequired_receiveBadRequest() {
        User user = TestUtil.createValidUser();
        user.setUsername("abc");
        ResponseEntity<Object> response = postSignup(user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasUsernameExceedsTheLengthLimit_receiveBadRequest() {
        User user = TestUtil.createValidUser();
        String valueOf256Chars = IntStream.rangeClosed(1,26).mapToObj(x -> "a").collect(Collectors.joining());
        user.setUsername(valueOf256Chars);
        ResponseEntity<Object> response = postSignup(user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasInvalidEmailPattern_receiveBadRequest() {
        User user = TestUtil.createValidUser();
        user.setEmail("testEmail@");
        ResponseEntity<Object> response = postSignup(user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasEmailExceedsTheLengthLimit_receiveBadRequest() {
        User user = TestUtil.createValidUser();
        String valueOf256Chars = IntStream.rangeClosed(1,255).mapToObj(x -> "a").collect(Collectors.joining()) + "@email.com";
        user.setEmail(valueOf256Chars);
        ResponseEntity<Object> response = postSignup(user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasPasswordLessThanRequired_receiveBadRequest() {
        User user = TestUtil.createValidUser();
        String valueOf256Chars = IntStream.rangeClosed(1,256).mapToObj(x -> "a").collect(Collectors.joining());
        user.setPassword(valueOf256Chars + "A1");
        ResponseEntity<Object> response = postSignup(user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasPasswordExceedsTheLengthLimit_receiveBadRequest() {
        User user = TestUtil.createValidUser();
        user.setPassword("P4sswor");
        ResponseEntity<Object> response = postSignup(user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasPasswordWithAllLowercase_receiveBadRequest() {
        User user = TestUtil.createValidUser();
        user.setPassword("alllowercase");
        ResponseEntity<Object> response = postSignup(user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasPasswordWithAllUppercase_receiveBadRequest() {
        User user = TestUtil.createValidUser();
        user.setPassword("ALLUPPERCASE");
        ResponseEntity<Object> response = postSignup(user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasPasswordWithAllNumbers_receiveBadRequest() {
        User user = TestUtil.createValidUser();
        user.setPassword("134567890");
        ResponseEntity<Object> response = postSignup(user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserIsInvalid_receiveApiError() {
        User user = new User();
        user.setUsername(null);
        ResponseEntity<ApiError> response = postSignup(user, ApiError.class);
        assertThat(response.getBody().getUrl()).isEqualTo(API_1_0_SIGNUP);
    }

    @Test
    public void postUser_whenUserIsInvalid_receiveApiErrorWithValidationErrors() {
        User user = new User();
        user.setUsername(null);
        ResponseEntity<ApiError> response = postSignup(user, ApiError.class);
        assertThat(response.getBody().getValidationErrors().size()).isEqualTo(3);
    }

    @Test
    public void postUser_whenUserHasNullFirstName_receiveMessageOfNullErrorForFirstName() {
        User user = new User();
        user.setUsername(null);
        ResponseEntity<ApiError> response = postSignup(user, ApiError.class);
        Map<String, String> validationErrors = response.getBody().getValidationErrors();
        assertThat(validationErrors.get("username")).isEqualTo("Username cannot be null");
    }

    @Test
    public void postUser_whenUserHasNullEmail_receiveMessageOfNullErrorForFirstName() {
        User user = new User();
        user.setEmail(null);
        ResponseEntity<ApiError> response = postSignup(user, ApiError.class);
        Map<String, String> validationErrors = response.getBody().getValidationErrors();
        assertThat(validationErrors.get("email")).isEqualTo("Email cannot be null");
    }

    @Test
    public void postUser_whenUserHasNullPassword_receiveGenericMessageOfNullError() {
        User user = new User();
        user.setPassword(null);
        ResponseEntity<ApiError> response = postSignup(user, ApiError.class);
        Map<String, String> validationErrors = response.getBody().getValidationErrors();
        assertThat(validationErrors.get("password")).isEqualTo("Password cannot be null");
    }

    @Test
    public void postUser_whenUserHasInvalidLengthFirstName_receiveMessageOfSizeError() {
        User user = new User();
        user.setUsername("abc");
        ResponseEntity<ApiError> response = postSignup(user, ApiError.class);
        Map<String, String> validationErrors = response.getBody().getValidationErrors();
        assertThat(validationErrors.get("username")).isEqualTo("It must have minimum 4 and maximum 25 characters");
    }

    @Test
    public void postUser_whenUserHasInvalidPasswordPattern_receiveMessageOfPasswordPatternError() {
        User user = new User();
        user.setPassword("alllowercase");
        ResponseEntity<ApiError> response = postSignup(user, ApiError.class);
        Map<String, String> validationErrors = response.getBody().getValidationErrors();
        assertThat(validationErrors.get("password")).isEqualTo("Password must have at least one uppercase, one lowercase letter and one number");
    }

    @Test
    public void postUser_whenAnotherUserHasSameEmail_receiveBadRequest() {
        userRepository.save(TestUtil.createValidUser());
        User user = TestUtil.createValidUser();
        ResponseEntity<Object> response = postSignup(user, Object.class);
        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUser_whenAnotherUserHasSameUsername_receiveMessageOfDuplicateUsername() {
        userRepository.save(TestUtil.createValidUser());
        User user = TestUtil.createValidUser();
        ResponseEntity<ApiError> response = postSignup(user, ApiError.class);
        Map<String, String> validationErrors = response.getBody().getValidationErrors();
        assertThat(validationErrors.get("username")).isEqualTo("This username is already taken");
    }

    @Test
    public void postUser_whenAnotherUserHasSameEmail_receiveMessageOfDuplicateEmail() {
        userRepository.save(TestUtil.createValidUser());
        User user = TestUtil.createValidUser();
        ResponseEntity<ApiError> response = postSignup(user, ApiError.class);
        Map<String, String> validationErrors = response.getBody().getValidationErrors();
        assertThat(validationErrors.get("email")).isEqualTo("This email is in use");
    }

///////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////LOGIN FUNCTIONALITY/////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////

    public <T> ResponseEntity<T> signIn(Class<T> responseType) {
        JSONObject emptyJsonObject = new JSONObject();
        return testRestTemplate.postForEntity(API_1_0_SIGNIN, emptyJsonObject, responseType);
    }

    private ResponseEntity<JwtResponse> authenticate(User user) {
        ResponseEntity<JwtResponse> response = testRestTemplate.postForEntity("/api/1.0/signin", user, JwtResponse.class);
        return response;
    }

    @Test
    public void postLogin_withoutUserCredentials_receiveUnauthorized() {
        ResponseEntity<Object> response = signIn(Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void postLogin_withIncorrectUserCredentials_receiveUnauthorized() {
        userService.save(TestUtil.createValidUser3());
        User user = new User();
        user.setUsername("test-user3");
        user.setEmail("test3@gmail.com");
        ResponseEntity<JwtResponse> response = authenticate(user);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void postLogin_withoutUserCredentials_receiveApiError() {
        ResponseEntity<String> response = signIn(String.class);
        assertThat(response.getBody().contains("validationErrors")).isFalse();
    }

    @Test
    public void postLogin_withoutUserCredentials_receiveUnauthorizedWithoutWWWAuthenticateHeader() {
        User user = new User();
        ResponseEntity<JwtResponse> response = authenticate(user);
        assertThat(response.getHeaders().containsKey("WWW-Authenticate")).isFalse();
    }

    @Test
    public void postLogin_whenUserAccountIsNotActive_receiveUnauthorized() {
        userService.save(TestUtil.createValidUser2());
        User user = new User();
        user.setUsername("test-user");
        user.setEmail("test2@gmail.com");
        ResponseEntity<JwtResponse> response = authenticate(user);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void postLogin_withValidCredentials_receiveOk() {
        userService.save(TestUtil.createValidUser3());
        User user = TestUtil.createLoginUser();
        ResponseEntity<JwtResponse> response = authenticate(user);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void postLogin_withValidCredentials_receiveLoggedInUserId() {
        User inDB = userService.save(TestUtil.createValidUser3());
        User loggingUser = TestUtil.createLoginUser();
        authenticate(loggingUser);
        ResponseEntity<JwtResponse> response = authenticate(loggingUser);
        JwtResponse body = response.getBody();
        Integer id = body.getId().intValue();
        assertThat(id).isEqualTo(inDB.getId());
    }

    @Test
    public void postLogin_withValidCredentials_receiveLoggedInActiveUser() {
        User inDB = userService.save(TestUtil.createValidUser3());
        User loggingUser = TestUtil.createLoginUser();
        authenticate(loggingUser);
        ResponseEntity<JwtResponse> response = authenticate(loggingUser);
        JwtResponse body = response.getBody();
        Boolean isActive = body.getIsActive();
        assertThat(isActive).isTrue();
    }

    @Test
    public void postLogin_withValidCredentials_receiveLoggedInUserEmail() {
        User inDB = userService.save(TestUtil.createValidUser3());
        User loggingUser = TestUtil.createLoginUser();
        ResponseEntity<JwtResponse> response = authenticate(loggingUser);
        JwtResponse body = response.getBody();
        String email = body.getEmail();
        assertThat(email).isEqualTo(inDB.getEmail());
    }

    @Test
    public void postLogin_withValidCredentials_receiveLoggedInUsersUsername() {
        User inDB = userService.save(TestUtil.createValidUser3());
        User loggingUser = TestUtil.createLoginUser();
        ResponseEntity<JwtResponse> response = authenticate(loggingUser);
        JwtResponse body = response.getBody();
        String username = body.getUsername();
        assertThat(username).isEqualTo(inDB.getUsername());
    }

    @Test
    public void postLogin_withValidCredentials_notReceiveLoggedInUsersPassword() {
        User inDB = userService.save(TestUtil.createValidUser3());
        User loggingUser = TestUtil.createLoginUser();
        ResponseEntity<JwtResponse> response = authenticate(loggingUser);
        JwtResponse body = response.getBody();
        assertThat(body).hasFieldOrProperty("password");
    }

    @Test
    public void postLogin_withValidCredentials_receiveLoggedInJwtToken() {
        User inDB = userService.save(TestUtil.createValidUser3());
        User loggingUser = TestUtil.createLoginUser();
        ResponseEntity<JwtResponse> response = authenticate(loggingUser);
        JwtResponse body = response.getBody();
        assertThat(body.getAccessToken()).isNotNull();
    }

    @Test
    public void postLogin_withValidCredentials_receiveLoggedInJwtTokenWithCorrectTokenType() {
        User inDB = userService.save(TestUtil.createValidUser3());
        User loggingUser = TestUtil.createLoginUser();
        ResponseEntity<JwtResponse> response = authenticate(loggingUser);
        JwtResponse body = response.getBody();
        assertThat(body.getTokenType()).isEqualTo("Bearer");
    }
}
