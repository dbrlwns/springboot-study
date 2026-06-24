import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JUnitCycleQuiz {

    @BeforeEach
    public void beforeEach()
    {
        System.out.println("Hello!");
    }

    @Test
    public void test1() {
        System.out.println("This is a first Test");
    }

    @Test
    public void test2() {
        System.out.println("This is a second Test");
    }

    @AfterAll
    public static void afterAll()
    {
        System.out.println("Bye!");
    }
}
