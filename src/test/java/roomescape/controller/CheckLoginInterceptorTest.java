package roomescape.controller;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import roomescape.config.CheckLoginInterceptor;
import roomescape.controller.api.dto.request.TokenContextRequest;
import roomescape.domain.user.Role;
import roomescape.exception.UnauthorizedException;
import roomescape.service.MemberService;
import roomescape.service.dto.output.TokenLoginOutput;
import roomescape.util.TokenProvider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@ExtendWith(SpringExtension.class)
class CheckLoginInterceptorTest {
    @MockBean
    MemberService memberService;
    @MockBean
    TokenProvider tokenProvider;

    CheckLoginInterceptor sut;
    MockHttpServletRequest request;
    MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        sut = new CheckLoginInterceptor(memberService, tokenProvider, new TokenContextRequest());
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();

    }

    @Test
    @DisplayName("토큰의 정보가 타당하면 참을 반환한다.")
    void return_true_when_token_info_is_admin() {
        final var output = new TokenLoginOutput(1, "운영자", "admin@email.com", "password1234", Role.USER.getValue());
        Mockito.when(tokenProvider.parseToken(request))
                .thenReturn("user_token");
        Mockito.when(memberService.loginToken("user_token"))
                .thenReturn(output);
        request.setCookies(new Cookie("token", "user_token"));

        final var result = sut.preHandle(request, response, null);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("토큰이 없으면 예외를 발생한다.")
    void return_false_when_not_exist_token() {
        Mockito.when(tokenProvider.parseToken(request))
                .thenThrow(new UnauthorizedException());

        assertThatThrownBy(() -> sut.preHandle(request, response, null))
                .isInstanceOf(UnauthorizedException.class);
    }
}
