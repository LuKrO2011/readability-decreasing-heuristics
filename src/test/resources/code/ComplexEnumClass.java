public class ComplexEnumClass {
    public enum ComplexEnum {
        VALUE1("val1"),
        VALUE2("val2")

        private String value;

        ComplexEnum(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    private ComplexEnum complexEnum = ComplexEnum.VALUE1;
}
