package magizhchi.academy.pawndbsync;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PawndbsyncApplication {
    public static void main(String[] args) {
        SpringApplication.run(PawndbsyncApplication.class, args);
    }
}
