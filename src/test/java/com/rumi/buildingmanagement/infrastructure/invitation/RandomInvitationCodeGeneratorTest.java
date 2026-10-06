package com.rumi.buildingmanagement.infrastructure.invitation;

import com.rumi.buildingmanagement.domain.model.ResidentInvitation;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class RandomInvitationCodeGeneratorTest {

    private final RandomInvitationCodeGenerator generator = new RandomInvitationCodeGenerator();

    @Test
    void generatesCodesWithThePrefixAndEightUnambiguousCharacters() {
        String code = generator.generate();

        assertThat(code).matches("RUMI-[A-HJ-KM-NP-Z2-9]{8}");
        assertThat(code.length()).isLessThanOrEqualTo(ResidentInvitation.MAX_CODE_LENGTH);
    }

    @Test
    void generatesDifferentCodes() {
        Set<String> codes = new HashSet<>();
        for (int i = 0; i < 200; i++) {
            codes.add(generator.generate());
        }

        assertThat(codes).hasSize(200);
    }
}
