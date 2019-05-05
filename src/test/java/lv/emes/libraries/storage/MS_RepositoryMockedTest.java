package lv.emes.libraries.storage;


import org.assertj.core.data.MapEntry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MS_RepositoryMockedTest {

    @Mock
    public MS_Repository<String, Integer> repository;

    @Test
    public void testFindPage() {
        // Setup
        final int ITEM_COUNT = 12;
        Map<Integer, String> allItems = new LinkedHashMap<>(ITEM_COUNT);
        for (int i = 1; i <= ITEM_COUNT; i++) {
            allItems.put(i, String.valueOf(i));
        }
        when(repository.doFindAll()).thenReturn(allItems);
        when(repository.doFindPage(anyInt(), anyInt())).thenCallRealMethod();

        // Preparations
        Map<Integer, String> itemsInPage;

        // Tests
        itemsInPage = repository.doFindPage(1, 2); //first page, first 2 items should be returned
        assertThat(itemsInPage).hasSize(2).containsOnly(MapEntry.entry(1, "1"), MapEntry.entry(2, "2"));

        itemsInPage = repository.doFindPage(2, 2); //second page, next 2 items should be returned
        assertThat(itemsInPage).hasSize(2).containsOnly(MapEntry.entry(3, "3"), MapEntry.entry(4, "4"));

        itemsInPage = repository.doFindPage(2, 3); //second page with page size 3 already returns different results
        assertThat(itemsInPage).hasSize(3).containsOnly(MapEntry.entry(4, "4"), MapEntry.entry(5, "5"), MapEntry.entry(6, "6"));

        itemsInPage = repository.doFindPage(6, 2); //last page is also fine
        assertThat(itemsInPage).hasSize(2).containsOnly(MapEntry.entry(11, "11"), MapEntry.entry(12, "12"));

        itemsInPage = repository.doFindPage(3, 5); //last page, which contains less items than requested
        assertThat(itemsInPage).hasSize(2).containsOnly(MapEntry.entry(11, "11"), MapEntry.entry(12, "12"));

        itemsInPage = repository.doFindPage(4, 5); //page after last page
        assertThat(itemsInPage).hasSize(0);

        itemsInPage = repository.doFindPage(1, 67); //large page that exceeds count of items in repository
        assertThat(itemsInPage).hasSize(ITEM_COUNT).containsAllEntriesOf(allItems);
    }
}
