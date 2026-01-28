package model;
public class Plane {
    private String planeID;
    private String planeModel;
    private int capacity;
    private Seat[][] seatMatrix;

    public Plane(String planeID, String planeModel) {
        this.planeID = planeID;
        this.planeModel = planeModel;
        
        // 5 bus + 25 eco satiri
        // sutun 6 
        this.seatMatrix = new Seat[30][6];
        this.capacity = 180; 
        
        initializeSeatMatrix();
    }

    
    private void initializeSeatMatrix() {
        for (int i = 0; i < 5; i++) {
            char rowLetter = (char) ('A' + i);
            for (int j = 0; j < 6; j++) {
                String seatNum = "" + rowLetter + (j + 1);
                seatMatrix[i][j] = new Seat(seatNum, Seat.SeatClass.BUSINESS, false);
            }
        }

        for (int i = 5; i < 30; i++) {
            char rowLetter = (char) ('A' + i);
            for (int j = 0; j < 6; j++) {
                String seatNum = "" + rowLetter + (j + 1);
                seatMatrix[i][j] = new Seat(seatNum, Seat.SeatClass.ECONOMY, false);
            }
        }
    }

    public String getPlaneID() { return planeID; }
    public void setPlaneID(String planeID) { this.planeID = planeID; }

    public String getPlaneModel() { return planeModel; }
    public void setPlaneModel(String planeModel) { this.planeModel = planeModel; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public Seat[][] getSeatMatrix() { return seatMatrix; }
    public void setSeatMatrix(Seat[][] seatMatrix) { this.seatMatrix = seatMatrix; }
}
