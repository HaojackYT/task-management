package ut.edu.task_management.constants;

public final class AppConstants {
    private AppConstants() {}

    public static final String JWT_SECRET = "w83K6m5vSxyF5Rz8Tq4uGQWlBPD2cYsmJ8jRlh12vNo=";
    public static final long JWT_EXPIRATION_MS = 3600_000L * 24; // 24 hours
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
}
