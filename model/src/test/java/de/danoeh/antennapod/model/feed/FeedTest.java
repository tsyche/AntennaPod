package de.danoeh.antennapod.model.feed;

import org.junit.Before;
import org.junit.Test;

import static de.danoeh.antennapod.model.feed.FeedMother.anyFeed;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class FeedTest {

    private Feed original;
    private Feed changedFeed;

    @Before
    public void setUp() {
        original = anyFeed();
        changedFeed = anyFeed();
    }

    @Test
    public void testUpdateFromOther_feedImageDownloadUrlChanged() {
        changedFeed.setImageUrl("http://example.com/new_picture");
        original.updateFromOther(changedFeed);
        assertEquals(original.getImageUrl(), changedFeed.getImageUrl());
    }

    @Test
    public void testUpdateFromOther_feedImageRemoved() {
        changedFeed.setImageUrl(null);
        original.updateFromOther(changedFeed);
        assertEquals(anyFeed().getImageUrl(), original.getImageUrl());
    }

    @Test
    public void testUpdateFromOther_feedImageAdded() {
        original.setImageUrl(null);
        changedFeed.setImageUrl("http://example.com/new_picture");
        original.updateFromOther(changedFeed);
        assertEquals(original.getImageUrl(), changedFeed.getImageUrl());
    }

    @Test
    public void testSetSortOrder_OnlyIntraFeedSortAllowed() {
        for (SortOrder sortOrder : SortOrder.values()) {
            if (sortOrder.scope == SortOrder.Scope.INTRA_FEED) {
                original.setSortOrder(sortOrder); // should be okay
            } else {
                assertThrows(IllegalArgumentException.class, () -> original.setSortOrder(sortOrder));
            }
        }
    }

    @Test
    public void testSetSortOrder_NullAllowed() {
        original.setSortOrder(null); // should be okay
    }

    // ========== Queue Location Feature Tests (commit 213f9c2ce) ==========

    @Test
    public void testFeedPreferencesQueueLocation_DefaultValue() {
        FeedPreferences prefs = new FeedPreferences(1, FeedPreferences.AutoDownloadSetting.GLOBAL,
                true, FeedPreferences.AutoDeleteAction.GLOBAL, VolumeAdaptionSetting.OFF,
                "", "", new FeedFilter(), 1, 0, 0,
                FeedPreferences.SkipSilence.GLOBAL, false, FeedPreferences.NewEpisodesAction.GLOBAL,
                new java.util.HashSet<>());

        // Verify default queue location is GLOBAL
        assertEquals(FeedPreferences.EnqueueLocation.GLOBAL, prefs.getEnqueueLocation());
    }

    @Test
    public void testFeedPreferencesQueueLocation_SetAndGet() {
        FeedPreferences prefs = new FeedPreferences(1, FeedPreferences.AutoDownloadSetting.GLOBAL,
                true, FeedPreferences.AutoDeleteAction.GLOBAL, VolumeAdaptionSetting.OFF,
                "", "", new FeedFilter(), 1, 0, 0,
                FeedPreferences.SkipSilence.GLOBAL, false, FeedPreferences.NewEpisodesAction.GLOBAL,
                new java.util.HashSet<>());

        // Test setting each queue location
        prefs.setEnqueueLocation(FeedPreferences.EnqueueLocation.BACK);
        assertEquals(FeedPreferences.EnqueueLocation.BACK, prefs.getEnqueueLocation());

        prefs.setEnqueueLocation(FeedPreferences.EnqueueLocation.FRONT);
        assertEquals(FeedPreferences.EnqueueLocation.FRONT, prefs.getEnqueueLocation());

        prefs.setEnqueueLocation(FeedPreferences.EnqueueLocation.AFTER_CURRENTLY_PLAYING);
        assertEquals(FeedPreferences.EnqueueLocation.AFTER_CURRENTLY_PLAYING, prefs.getEnqueueLocation());

        prefs.setEnqueueLocation(FeedPreferences.EnqueueLocation.RANDOM);
        assertEquals(FeedPreferences.EnqueueLocation.RANDOM, prefs.getEnqueueLocation());

        prefs.setEnqueueLocation(FeedPreferences.EnqueueLocation.GLOBAL);
        assertEquals(FeedPreferences.EnqueueLocation.GLOBAL, prefs.getEnqueueLocation());
    }

    @Test
    public void testFeedPreferencesQueueLocation_NullHandling() {
        FeedPreferences prefs = new FeedPreferences(1, FeedPreferences.AutoDownloadSetting.GLOBAL,
                true, FeedPreferences.AutoDeleteAction.GLOBAL, VolumeAdaptionSetting.OFF,
                "", "", new FeedFilter(), 1, 0, 0,
                FeedPreferences.SkipSilence.GLOBAL, false, FeedPreferences.NewEpisodesAction.GLOBAL,
                new java.util.HashSet<>());

        // Test that null defaults to GLOBAL
        prefs.setEnqueueLocation(null);
        assertEquals(FeedPreferences.EnqueueLocation.GLOBAL, prefs.getEnqueueLocation());
    }

    @Test
    public void testEnqueueLocation_FromCode() {
        // Test all enum codes
        assertEquals(FeedPreferences.EnqueueLocation.GLOBAL, FeedPreferences.EnqueueLocation.fromCode(0));
        assertEquals(FeedPreferences.EnqueueLocation.BACK, FeedPreferences.EnqueueLocation.fromCode(1));
        assertEquals(FeedPreferences.EnqueueLocation.FRONT, FeedPreferences.EnqueueLocation.fromCode(2));
        assertEquals(FeedPreferences.EnqueueLocation.AFTER_CURRENTLY_PLAYING,
                FeedPreferences.EnqueueLocation.fromCode(3));
        assertEquals(FeedPreferences.EnqueueLocation.RANDOM, FeedPreferences.EnqueueLocation.fromCode(4));
    }

    @Test
    public void testEnqueueLocation_FromCodeInvalid() {
        // Test invalid code defaults to GLOBAL
        assertEquals(FeedPreferences.EnqueueLocation.GLOBAL, FeedPreferences.EnqueueLocation.fromCode(999));
        assertEquals(FeedPreferences.EnqueueLocation.GLOBAL, FeedPreferences.EnqueueLocation.fromCode(-1));
    }

    @Test
    public void testEnqueueLocation_CodeProperties() {
        // Verify each enum has the correct code
        assertEquals(0, FeedPreferences.EnqueueLocation.GLOBAL.code);
        assertEquals(1, FeedPreferences.EnqueueLocation.BACK.code);
        assertEquals(2, FeedPreferences.EnqueueLocation.FRONT.code);
        assertEquals(3, FeedPreferences.EnqueueLocation.AFTER_CURRENTLY_PLAYING.code);
        assertEquals(4, FeedPreferences.EnqueueLocation.RANDOM.code);
    }
}