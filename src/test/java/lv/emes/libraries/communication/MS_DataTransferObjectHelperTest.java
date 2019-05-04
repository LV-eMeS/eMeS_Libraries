package lv.emes.libraries.communication;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MS_DataTransferObjectHelperTest {

    @Test
    public void testSerialize() {
        assertThat(MS_DTOMappingHelper.serialize(1, IntegerSerializationAlgorithm.class)).isEqualTo("1");
        assertThat(MS_DTOMappingHelper.serializeList(Arrays.asList(1, 2, 3), IntegerSerializationAlgorithm.class)).isEqualTo(Arrays.asList("1", "2", "3"));
        assertThat(MS_DTOMappingHelper.serializeMap(ImmutableMap.of(1, 1, 2, 2, 3, 3), IntegerSerializationAlgorithm.class))
                .isEqualTo(ImmutableMap.of(1, "1", 2, "2", 3, "3"));
    }

    @Test
    public void testDeserialize() {
        assertThat(MS_DTOMappingHelper.deserialize("1", IntegerSerializationAlgorithm.class)).isEqualTo(1);
        assertThat(MS_DTOMappingHelper.deserializeList(Arrays.asList("1", "2", "3"), IntegerSerializationAlgorithm.class)).isEqualTo(Arrays.asList(1, 2, 3));
        assertThat(MS_DTOMappingHelper.deserializeMap(ImmutableMap.of(1, "1", 2, "2", 3, "3"), IntegerSerializationAlgorithm.class))
                .isEqualTo(ImmutableMap.of(1, 1, 2, 2, 3, 3));
    }

    @Test
    public void testNullArgs() {
        assertThatThrownBy(() -> MS_DTOMappingHelper.serialize(1, null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> MS_DTOMappingHelper.serializeList(Collections.emptyList(), null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> MS_DTOMappingHelper.serializeMap(Collections.emptyMap(), null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> MS_DTOMappingHelper.deserialize("1", null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> MS_DTOMappingHelper.deserializeList(Collections.emptyList(), null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> MS_DTOMappingHelper.deserializeMap(Collections.emptyMap(), null)).isInstanceOf(NullPointerException.class);
        assertThat(MS_DTOMappingHelper.serialize(null, IntegerSerializationAlgorithm.class)).isEqualTo(null);
        assertThat(MS_DTOMappingHelper.serializeList(null, IntegerSerializationAlgorithm.class)).isEqualTo(null);
        assertThat(MS_DTOMappingHelper.serializeMap(null, IntegerSerializationAlgorithm.class)).isEqualTo(null);
        assertThat(MS_DTOMappingHelper.deserialize(null, IntegerSerializationAlgorithm.class)).isEqualTo(null);
        assertThat(MS_DTOMappingHelper.deserializeList(null, IntegerSerializationAlgorithm.class)).isEqualTo(null);
        assertThat(MS_DTOMappingHelper.deserializeMap(null, IntegerSerializationAlgorithm.class)).isEqualTo(null);
    }

    public static class IntegerSerializationAlgorithm extends MS_DTOMappingAlgorithm<Integer, String> {

        @Override
        public String serialize(Integer objectToSerialize) {
            return objectToSerialize.toString();
        }

        @Override
        public Integer deserialize(String serializedObject) {
            return Integer.valueOf(serializedObject);
        }
    }
}