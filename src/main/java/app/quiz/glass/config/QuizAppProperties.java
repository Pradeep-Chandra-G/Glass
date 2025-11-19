package app.quiz.glass.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "quiz")
public class QuizAppProperties {

    private Timer timer = new Timer();
    private Pagination pagination = new Pagination();
    private AutoSave autoSave = new AutoSave();
    private Security security = new Security();

    @Data
    public static class Timer {
        private Long syncInterval = 1000L;
    }

    @Data
    public static class Pagination {
        private Integer defaultPageSize = 10;
        private Integer maxPageSize = 100;
    }

    @Data
    public static class AutoSave {
        private Boolean enabled = true;
        private Long debounceMs = 2000L;
    }

    @Data
    public static class Security {
        private Password password = new Password();
        private Session session = new Session();
    }

    @Data
    public static class Password {
        private Integer minLength = 8;
    }

    @Data
    public static class Session {
        private Integer maxConcurrent = 1;
    }
}

