package bookshop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bookshop.service.PostageService;

public class PostageServiceTest {

    private PostageService postageService;

    @BeforeEach
    public void setUp() {
        postageService = new PostageService();
    }

    //pricing tier for single books
    @Test public void testTier1_100g() {
         assertEquals(1.25,  postageService.calculatePostage(100),  0.001); 
    }
    
    @Test public void testTier2_250g(){
         assertEquals(2.00,  postageService.calculatePostage(250),  0.001); 
    
    }

    @Test public void testTier3_500g() {
         assertEquals(3.50,  postageService.calculatePostage(500),  0.001); 
    }

    @Test public void testTier4_1000g() {
         assertEquals(5.00,  postageService.calculatePostage(1000), 0.001); 
    }

    @Test public void testTier5_2000g() { 
        assertEquals(8.00,  postageService.calculatePostage(2000), 0.001); 
    }

    @Test public void testTier6_above()  { 
        assertEquals(12.00, postageService.calculatePostage(2001), 0.001); 
    }

    //invalid inputs
    @Test public void testZeroWeight() { 
        assertThrows(IllegalArgumentException.class, () -> postageService.calculatePostage(0));   
    }

    @Test public void testNegativeWeight() { 
        assertThrows(IllegalArgumentException.class, () -> postageService.calculatePostage(-10)); 
    }

    //multiple books
    @Test
    public void testMultipleBooks_combinedTier() {
        assertEquals(3.50, postageService.calculatePostageForMultiple(new double[]{150, 150}), 0.001);
    }

    @Test
    public void testMultipleBooks_aboveMax() {
        assertEquals(12.00, postageService.calculatePostageForMultiple(new double[]{800, 800, 800}), 0.001);
    }
}
