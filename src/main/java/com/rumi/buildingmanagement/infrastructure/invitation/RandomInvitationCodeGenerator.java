package com.rumi.buildingmanagement.infrastructure.invitation;

import com.rumi.buildingmanagement.domain.service.InvitationCodeGenerator;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

/**
 * Generates codes such as RUMI-7K2M9QXD: a fixed prefix and eight random characters.
 * Letters and digits that are easy to confuse (0, O, 1, I, L) are left out.
 */
@Component
public class RandomInvitationCodeGenerator implements InvitationCodeGenerator {

    static final String PREFIX = "RUMI-";
    static final String ALPHABET = "ABCDEFGHJKMNPQRSTUVWXYZ23456789";
    static final int RANDOM_LENGTH = 8;

    private final SecureRandom random = new SecureRandom();

    @Override
    public String generate() {
        StringBuilder code = new StringBuilder(PREFIX);
        for (int i = 0; i < RANDOM_LENGTH; i++) {
            code.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return code.toString();
    }
}
