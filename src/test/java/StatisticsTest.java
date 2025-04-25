import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StatisticsTest {

    @Test
    public void AbbreviateSupTest() {
        String abv = TestMethoden.abbreviate("SUPERMARKET");
        Assertions.assertEquals("SUP", abv);
    }

    @Test
    public void AbbreviateResTest() {
        String abv = TestMethoden.abbreviate("RESTAURANT");
        Assertions.assertEquals("RES", abv);
    }

    @Test
    public void calculateStepSizeTest(){
        Assertions.assertEquals(5, TestMethoden.calculateStepSize(4));
        Assertions.assertEquals(10, TestMethoden.calculateStepSize(90));
    }

}
