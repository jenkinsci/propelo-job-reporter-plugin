package io.jenkins.plugins.propelo.commons.service;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class JobRunScmCommitIdsServiceTest {

    private static final String SHA_ONE = "08d386c3b05997a04bf1ffdc0948af0f44b5bd2d";
    private static final String SHA_TWO = "37482a7fcf68e1181fe3b999da2372aa1d85220a";
    private static final String GROOVY_SHA = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
    private static final String SERVICE_SHA = "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb";

    @Test
    public void shouldParseSingleSha() {
        JobRunScmCommitIdsService service = new JobRunScmCommitIdsService();
        Assert.assertEquals(Collections.singletonList(SHA_ONE), service.parseCommitIdsFromRawValue(SHA_ONE));
    }

    @Test
    public void shouldParseCommaAndWhitespaceSeparatedListsAndDedupe() {
        JobRunScmCommitIdsService service = new JobRunScmCommitIdsService();
        String raw = SHA_ONE + ", " + SHA_TWO + "\n" + SHA_ONE + ";" + SHA_TWO;
        Assert.assertEquals(Arrays.asList(SHA_ONE, SHA_TWO), service.parseCommitIdsFromRawValue(raw));
    }

    @Test
    public void shouldRejectInvalidValuesAndIgnoreBlankInput() {
        JobRunScmCommitIdsService service = new JobRunScmCommitIdsService();
        Assert.assertTrue(service.parseCommitIdsFromRawValue(null).isEmpty());
        Assert.assertTrue(service.parseCommitIdsFromRawValue("   ").isEmpty());
        Assert.assertTrue(service.parseCommitIdsFromRawValue("not-a-sha, abc").isEmpty());
        Assert.assertEquals(Collections.singletonList(SHA_ONE),
                service.parseCommitIdsFromRawValue("deadbeef, " + SHA_ONE + ", 08d386c"));
    }

    @Test
    public void shouldMergeDiscoveredAndExplicitCommitIdsWithNativeFirst() {
        JobRunScmCommitIdsService service = new JobRunScmCommitIdsService();
        List<String> discovered = new ArrayList<>(Collections.singletonList(GROOVY_SHA));
        List<String> explicit = Collections.singletonList(SERVICE_SHA);

        List<String> merged = service.mergeCommitIds(discovered, explicit);

        Assert.assertEquals(Arrays.asList(GROOVY_SHA, SERVICE_SHA), merged);
    }

    @Test
    public void shouldDeduplicateCommitIdsAcrossDiscoveredAndExplicitPreservingFirstSeenOrder() {
        JobRunScmCommitIdsService service = new JobRunScmCommitIdsService();
        List<String> discovered = Collections.singletonList(SHA_ONE);
        List<String> explicit = Collections.singletonList(SHA_ONE);

        List<String> merged = service.mergeCommitIds(discovered, explicit);

        Assert.assertEquals(Collections.singletonList(SHA_ONE), merged);
    }

    @Test
    public void shouldIgnoreNullAndBlankEntriesWhenMerging() {
        JobRunScmCommitIdsService service = new JobRunScmCommitIdsService();
        List<String> discovered = Arrays.asList(GROOVY_SHA, null, "  ", SERVICE_SHA);
        List<String> explicit = Arrays.asList(null, " ", SHA_TWO);

        List<String> merged = service.mergeCommitIds(discovered, explicit);

        Assert.assertEquals(Arrays.asList(GROOVY_SHA, SERVICE_SHA, SHA_TWO), merged);
    }

    @Test
    public void shouldPreserveDiscoveredValuesInNewMutableListWhenExplicitIsNullOrEmpty() {
        JobRunScmCommitIdsService service = new JobRunScmCommitIdsService();
        List<String> discovered = new ArrayList<>(Arrays.asList(GROOVY_SHA, SERVICE_SHA));

        List<String> mergedFromNull = service.mergeCommitIds(discovered, null);
        List<String> mergedFromEmpty = service.mergeCommitIds(discovered, Collections.emptyList());

        Assert.assertEquals(Arrays.asList(GROOVY_SHA, SERVICE_SHA), mergedFromNull);
        Assert.assertEquals(Arrays.asList(GROOVY_SHA, SERVICE_SHA), mergedFromEmpty);
        Assert.assertNotSame(discovered, mergedFromNull);
        Assert.assertNotSame(discovered, mergedFromEmpty);
        Assert.assertTrue(mergedFromNull instanceof ArrayList);
    }

    @Test
    public void shouldNotMutateInputListsWhenMerging() {
        JobRunScmCommitIdsService service = new JobRunScmCommitIdsService();
        List<String> discovered = new ArrayList<>(Collections.singletonList(GROOVY_SHA));
        List<String> explicit = new ArrayList<>(Collections.singletonList(SERVICE_SHA));
        List<String> discoveredSnapshot = new ArrayList<>(discovered);
        List<String> explicitSnapshot = new ArrayList<>(explicit);

        service.mergeCommitIds(discovered, explicit);

        Assert.assertEquals(discoveredSnapshot, discovered);
        Assert.assertEquals(explicitSnapshot, explicit);
    }
}
