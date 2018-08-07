package OnlineBookingSystem.DisplayClasses;

import OnlineBookingSystem.ModelClasses.Day;

import java.util.ArrayList;

/**
 * Created by boblo on 3/04/2017.
 */
public class ShiftCell extends TableCell {
    public ShiftStatus status;
    public ArrayList<Link> employees;
    public Day day;
}
