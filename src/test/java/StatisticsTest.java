import org.junit.jupiter.api.Test;
import static org.junit.Assert.assertEquals;

public class StatisticsTest {

    @Test
    public void AbbreviateSupTest() {
        String abv = TestMethoden.abbreviate("SUPERMARKET");
        assertEquals(abv,"SUP");
    }

    @Test
    public void AbbreviateResTest() {
        String abv = TestMethoden.abbreviate("RESTAURANT");
        assertEquals(abv,"RES");
    }

    @Test
    public void calculateStepSizeTest(){
        assertEquals(5,TestMethoden.calculateStepSize(4));
        assertEquals(10,TestMethoden.calculateStepSize(90));
    }

}
