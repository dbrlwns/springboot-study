import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class JUnitQuiz {

    @Test
    public void test1() {
        String name1 = "hong";
        String name2 = "hong";
        String name3 = "kem";

        Assertions.assertNotNull(name1);
        Assertions.assertNotNull(name2);
        assertThat(name3).isNotNull();  // AssertJ 적용

        assertThat(name1).isEqualTo(name2);
        assertThat(name1).isNotEqualTo(name3);

    }

    @Test
    public void test2() {
        int num1 = 15;
        int num2 = 0;
        int num3 = -15;

        assertThat(num1).isPositive();
        assertThat(num2).isZero();
        assertThat(num3).isNegative();

        assertThat(num1).isGreaterThan(num2);
        assertThat(num3).isLessThan(num1);
    }
}
