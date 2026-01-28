package test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import manager.SeatManager;

class SeatManagerTest {

    private SeatManager seatManager;

    @BeforeEach
    void setUp() {
        seatManager = new SeatManager();
        seatManager.createSeats();
    }

    @Test
    @DisplayName("Test: Rezervasyon sonrasi bos koltuk sayisi azalmali")
    void testAvailableSeatCountDecreases() {

        int initialCount = seatManager.getAvailableSeatCount();

        seatManager.reserveSeat("A1");

        int finalCount = seatManager.getAvailableSeatCount();

        assertEquals(initialCount - 1, finalCount,
                "Bir koltuk rezerve edildikten sonra bos koltuk sayisi 1 azalmalidir.");
    }

    @Test
    @DisplayName("Test: Var olmayan koltuk numarasi hata firlatmali")
    void testReserveNonExistentSeatThrowsException() {
        String invalidSeat = "Z99";

        assertThrows(IllegalArgumentException.class, () -> {
            seatManager.reserveSeat(invalidSeat);
        }, "Olmayan bir koltuk rezerve edilmeye calisildiginda hata firlatilmalidir.");
    }
}