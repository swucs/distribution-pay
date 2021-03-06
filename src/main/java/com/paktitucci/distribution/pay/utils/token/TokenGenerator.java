package com.paktitucci.distribution.pay.utils.token;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.util.Strings;

import java.util.Random;

import static java.util.stream.Collectors.joining;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TokenGenerator {
    private static final char[] ELEMENTS = {
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    };
    private static final int START_INDEX_OF_TOKEN = 0;
    private static final int END_INDEX_OF_TOKEN = ELEMENTS.length;

    public static String generate(int tokenLength) {
        Random random = new Random(System.currentTimeMillis());

        return  random.ints(START_INDEX_OF_TOKEN, END_INDEX_OF_TOKEN)
                    .limit(tokenLength)
                    .mapToObj(index -> String.valueOf(ELEMENTS[index]))
                    .collect(joining(Strings.EMPTY));

    }
}
