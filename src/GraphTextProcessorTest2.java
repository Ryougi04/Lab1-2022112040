import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GraphTextProcessorTest2 {

    @BeforeAll
    public static void setup() throws Exception {
        GraphTextProcessor.graph.clear();
        GraphTextProcessor.parseTextFile("Cursed Be The Treasure.txt"); // 确保包含你测试中的词
    }

    @Test
    public void testBridgeWords_case1_invalidWords() {
        String result = GraphTextProcessor.queryBridgeWords("suuuu", "asd");
        assertEquals("No \"suuuu\" or \"asd\" in the graph!", result);
    }

    @Test
    public void testBridgeWords_case2_noBridge() {
        String result = GraphTextProcessor.queryBridgeWords("my", "stockings");
        assertEquals("No bridge words from \"my\" to \"stockings\"!", result);
    }

    @Test
    public void testBridgeWords_case3_singleBridge() {
        String result = GraphTextProcessor.queryBridgeWords("gruff", "beneath");
        assertEquals("The bridge word from \"gruff\" to \"beneath\" is: voice.", result);
    }

    @Test
    public void testBridgeWords_case4_multipleBridges() {
        String result = GraphTextProcessor.queryBridgeWords("right", "to");
        assertTrue(result.startsWith("The bridge words from \"right\" to \"to\" are:"), "Should return multiple bridge words");
        assertTrue(result.contains("through"));
        assertTrue(result.contains("but"));
        assertTrue(result.contains("hand"));
    }
}