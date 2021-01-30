package com.paktitucci.distribution.pay.utils;

import com.paktitucci.distribution.pay.utils.token.TokenGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

public class TokenGeneratorTest {

    @ParameterizedTest
    @ValueSource(ints = {3, 4, 5, 6, 7, 10, 24})
    @DisplayName("받은 인자수만큼 문자열 토큰 생성 테스트")
    public void tokenLengthTest(int tokenLength) {
        // when
        String token = TokenGenerator.generate(tokenLength);

        // then
        assertThat(token.length()).isEqualTo(tokenLength);

    }
}
