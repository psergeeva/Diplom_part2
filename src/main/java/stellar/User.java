package stellar;

import org.apache.commons.lang3.RandomStringUtils;
import com.github.javafaker.Faker;
import com.fasterxml.jackson.annotation.JsonInclude;
import static stellar.UserDataGenerator.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {
    public String email;
    public String password;
    public String name;

    public User() {
    }

    public User (String email, String name) {
        this.email = email;
        this.name = name;
    }

    public User (String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static User getRandom() {
        return new User(generateEmail(), generatePassword(), generateName());
    }

    public static User getRandomWithoutEmail() {
        String password = RandomStringUtils.randomAlphabetic(10);
        String name = RandomStringUtils.randomAlphabetic(10);
        return new User(password, name);
    }

    public static User getRandomWithoutPassword() {
        Faker faker = Faker.instance();
        String email = faker.internet().emailAddress();
        String name = RandomStringUtils.randomAlphabetic(10);
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        return user;
    }

    public static User getRandomWithoutName() {
        Faker faker = Faker.instance();
        String email = faker.internet().emailAddress();
        String password = RandomStringUtils.randomAlphabetic(10);
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        return user;
    }
}