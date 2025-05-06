package io.jenkins.plugins.propelo.commons.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LevelOpsPluginConfigValidatorTest {

    @Test
    void testIsLevelOpsApiKeyEncrypted() {
        assertFalse(LevelOpsPluginConfigValidator.isLevelOpsApiKeyEncrypted("eyJrZXkiOiJGakhiQHo3SI6ImZvbyJ9"));
        assertTrue(LevelOpsPluginConfigValidator.isLevelOpsApiKeyEncrypted("{AQAAABAAAACwlkxqDO9ZqbYHVrC2wtS6NU5LKpxS87kjaeXosy/jjQUUD6Ecoe1H9UspasipRlUST1axwhYRQDdD4 mKlxQ2JZQXl4ooB0hrU1tPBV1SNfnMjk2mh9tQFTRJ0AzAEg7D CmDVe49KKmBQQoaW2uHwCQXWAJ1xt0KzSW pNSxZwm O52rFEtxLdOGJ9VmFn8fZrKjuRGYYVsSHRPPH4brulOSp3jVWhvuSEL/flOSkuibsQT3W LnlGkut04qi5Cj}"));
    }
}