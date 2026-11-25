package utils;

public final class Config {
    private Config() { }
    public static String baseUrl() {
        return System.getProperty("baseUrl", "https://opensource-demo.orangehrmlive.com").replaceAll("/+$", "");
    }
    public static String username() { return System.getProperty("username", "Admin"); }
    public static String password() { return System.getProperty("password", "admin123"); }
    public static int timeout() { return Integer.getInteger("timeout", 25); }
}
