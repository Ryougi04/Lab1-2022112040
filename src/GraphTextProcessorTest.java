import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GraphTextProcessorTest {
    @BeforeAll
    public static void setup() throws Exception {
        GraphTextProcessor.graph.clear();
        GraphTextProcessor.parseTextFile("Easy Test.txt");
    }

    @Test
    public void testPageRank_the() {
        double pr = GraphTextProcessor.calPageRank("the");
        assertTrue(pr > 0.1 && pr < 0.3, "Expected 'the' to have relatively high PageRank");
    }

    @Test
    public void testPageRank_again() {
        double pr = GraphTextProcessor.calPageRank("again");
        assertTrue(pr > 0.0 && pr < 0.1, "Expected 'again' to have low PageRank");
    }

    @Test
    public void testPageRank_sad() {
        double pr = GraphTextProcessor.calPageRank("sad");
        assertEquals(0.0, pr, 1e-6, "Expected 'sad' to have PageRank 0");
    }
}