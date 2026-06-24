import org.junit.jupiter.api.*;

public class JUnitCycleTest {

    @BeforeAll  // 전체 테스트 시작 전 1회 실행
    static void beforeAll() {
        System.out.println("@Before All");
    }

    @BeforeEach // 케이스 시작 전마다 실행
    public void beforeEach() {
        System.out.println("@BeforeEach");
    }

    @Test
    public void test1() {
        System.out.println("Test1");
    }
    @Test
    public void test2() {
        System.out.println("Test2");
    }
    @Test
    public void test3() {
        System.out.println("Test3");
    }

    @AfterAll   // 전체 테스트 마치고 종료 전 1회 실행
    static void afterAll() {
        System.out.println("@AfterAll");
    }

    @AfterEach  // 케이스 종료 전마다 실행
    void afterEach() {
        System.out.println("@AfterEach");
    }
}
