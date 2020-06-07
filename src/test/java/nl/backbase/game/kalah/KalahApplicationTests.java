package nl.backbase.game.kalah;

import nl.backbase.game.kalah.service.GameServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(
        classes = {
                KalahApplication.class,
        },
        properties = {
                "spring.main.allow-bean-definition-overriding=true"
        })
class KalahApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoads() {
        assertNotNull(applicationContext.getBean(MongoTemplate.class));
        assertNotNull(applicationContext.getBean("gameServiceImpl", GameServiceImpl.class));

    }

}
