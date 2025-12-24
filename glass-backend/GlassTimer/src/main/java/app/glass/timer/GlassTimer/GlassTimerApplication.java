package app.glass.timer.GlassTimer;

import app.glass.timer.GlassTimer.config.QuizTimerProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GlassTimerApplication {

	public static void main(String[] args) {
		SpringApplication.run(GlassTimerApplication.class, args);
	}

}
