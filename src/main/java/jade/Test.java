package jade;

public class Test {
    private String s = "#type vertex\n" +
            "test";
//    private String s = "abc123";

    public String[] splitString(String regex) {
        return s.split(regex);
    }
}
