package server.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

class PaginatorTest {
    private Paginator<String> pgntr;

    @BeforeEach
    void setUp() {
        pgntr = new Paginator<>(1, "test");
    }

    @Test
    void getMaxPageNumTest() {
        assertEquals(Integer.valueOf(1), pgntr.getMaxPageNum());
    }

    @Test
    void getFilling() {
        assertEquals("test", pgntr.getFilling());
    }

}


