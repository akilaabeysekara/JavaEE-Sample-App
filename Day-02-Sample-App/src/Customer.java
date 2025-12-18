public class Customer {
    private final String id;
    private final String name;
    private final String address;

    public Customer(String id, String name, String address) {
        this.id = id == null ? "" : id;
        this.name = name == null ? "" : name;
        this.address = address == null ? "" : address;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    // Very small JSON builder (escape quotes/newlines)
    public String toJson() {
        return "{" +
                "\"id\":\"" + escape(id) + "\"," +
                "\"name\":\"" + escape(name) + "\"," +
                "\"address\":\"" + escape(address) + "\"" +
                "}";
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}
