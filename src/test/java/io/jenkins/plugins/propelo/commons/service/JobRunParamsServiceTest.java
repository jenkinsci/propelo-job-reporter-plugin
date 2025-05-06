package io.jenkins.plugins.propelo.commons.service;

import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;

import static io.jenkins.plugins.propelo.commons.service.JobRunParamsService.PARAM_TYPE_REGEX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JobRunParamsServiceTest {

    @Test
    void testRegex() {
        String data = "(StringParameterValue) env_name='STAGING'";
        Matcher matcher = PARAM_TYPE_REGEX.matcher(data);
        assertTrue(matcher.matches());
        assertEquals("StringParameterValue", matcher.group(1));
    }

    @Test
    void testRegex2() {
        String data = "(TextParameterValue) boker_ids='broker1\n" +
                "broker2\n" +
                "broker3'";
        Matcher matcher = PARAM_TYPE_REGEX.matcher(data);
        assertTrue(matcher.matches());
        assertEquals("TextParameterValue", matcher.group(1));
    }

}