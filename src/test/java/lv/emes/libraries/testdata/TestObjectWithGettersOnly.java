package lv.emes.libraries.testdata;

import java.util.Objects;

public class TestObjectWithGettersOnly {

    public String getField1() {
        return "fieldValue";
    }

    public int getField2() {
        return 4;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final TestObjectWithGettersOnly other = (TestObjectWithGettersOnly) obj;
        return Objects.equals(this.getField1(), other.getField1())
                && Objects.equals(this.getField2(), other.getField2());
    }
}
