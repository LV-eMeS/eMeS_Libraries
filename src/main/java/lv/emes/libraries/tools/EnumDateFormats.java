package lv.emes.libraries.tools;

public enum EnumDateFormats {
    DDMMYYYY(1),
    YYYYMMDD(2),
    DD_MM_YYYY(3),
    YYYY_MM_DD(4)
    ;

    private final Integer formatId;

    private EnumDateFormats(Integer formatId) {
        this.formatId = formatId;
    }

    public int getDescription() {
        return formatId;
    }

    @Override
    public String toString() {
        return formatId.toString();
    }
}
