package manager;

import java.util.ArrayList;
import java.util.List;

import model.Staff;

public class StaffManager {
    private static StaffManager instance;
    private List<Staff> staffList;

    private StaffManager() {
        this.staffList = FileManager.loadStaff();
        if (this.staffList == null) this.staffList = new ArrayList<>();
    }

    public static synchronized StaffManager getInstance() {
        if (instance == null) instance = new StaffManager();
        return instance;
    }

    public void addStaff(Staff s) {
        staffList.add(s);
        FileManager.saveStaff(staffList);
    }

    public void deleteStaff(String id) {
        staffList.removeIf(s -> s.getId().equals(id));
        FileManager.saveStaff(staffList);
    }

    public List<Staff> getAllStaff() {
        return staffList;
    }

   
    public void updateStaff(String id, Staff updatedStaff) {
    for (int i = 0; i < staffList.size(); i++) {
        if (staffList.get(i).getId().equals(id)) {
            staffList.set(i, updatedStaff); 
            FileManager.saveStaff(staffList); 
            return;
        }
    }
    }
}