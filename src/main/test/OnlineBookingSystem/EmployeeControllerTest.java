package OnlineBookingSystem;

import OnlineBookingSystem.Controllers.EmployeeController;
import OnlineBookingSystem.ModelClasses.OBSFascade;
import OnlineBookingSystem.ModelClasses.OBSModel;
import OnlineBookingSystem.ModelClasses.Role;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class EmployeeControllerTest
{
    private HttpSession httpSession;

    @BeforeEach
    public void prepareLoginSession()
    {
        // Prepare database API
        OBSFascade obs = OBSModel.getModel();

        // Prepare a fake session, make some fake news
        this.httpSession = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getSession();

        httpSession.setAttribute("id", obs.getBusinessOwnerByUsername("whatever").getId());
        httpSession.setAttribute("role", Role.BusinessOwner);
    }

    @Test
    public void addNewEmployee() throws Exception
    {
        EmployeeController employeeController = new EmployeeController(this.httpSession);
        ModelAndView modelAndView = employeeController.postEmployeeAdd(
                                    "SuchDurian",
                                    UUID.randomUUID().toString().substring(0, 8) + "@whatever.com",
                                    "0400000000",
                                    "1024 Durian Rd, Melbourne, VIC 3000",
                                    new Integer[]{1});

        Assertions.assertEquals("redirect:/businessowner/dashboard", modelAndView.getViewName());
    }

    @Test
    public void addInvalidEmployeeWithDuplicatedName() throws Exception
    {
        String employeeName = "DuplicatedFireAlarm";

        // The first pass
        EmployeeController employeeController = new EmployeeController(this.httpSession);
        employeeController.postEmployeeAdd(
                employeeName,
                UUID.randomUUID().toString().substring(0, 8) + "@whatever.com",
                "0400000000",
                "1024 Durian Rd, Melbourne, VIC 3000",
                new Integer[]{1});

        // ...and do it again with the same request
        ModelAndView modelAndView = employeeController.postEmployeeAdd(
                employeeName,
                UUID.randomUUID().toString().substring(0, 8) + "@whatever.com",
                "0400000000",
                "1024 Durian Rd, Melbourne, VIC 3000",
                new Integer[]{1});

        Assertions.assertNotNull(modelAndView.getModel());
        Assertions.assertTrue(modelAndView.getModel().containsKey("Error"));
        Assertions.assertEquals(modelAndView.getModel().get("Error"), "Employee has already added!");
    }

    @Test
    public void addInvalidEmployeeWithNoName() throws Exception
    {
        EmployeeController employeeController = new EmployeeController(this.httpSession);
        ModelAndView modelAndView = employeeController.postEmployeeAdd(
                "",
                UUID.randomUUID().toString().substring(0, 8) + "@whatever.com",
                "0400000000",
                "1024 Durian Rd, Melbourne, VIC 3000",
                new Integer[]{1});

        Assertions.assertNotNull(modelAndView.getModel());
        Assertions.assertTrue(modelAndView.getModel().containsKey("Error"));
        Assertions.assertEquals(modelAndView.getModel().get("Error"),
                "Name must only contain letters and spaces");
    }

    @Test
    public void addInvalidChinesePhoneNumber() throws Exception
    {
        EmployeeController employeeController = new EmployeeController(this.httpSession);
        ModelAndView modelAndView = employeeController.postEmployeeAdd(
                "CommieDurian",
                UUID.randomUUID().toString().substring(0, 8) + "@whatever.com",
                "+8613800138000",
                "1024 Durian Rd, Melbourne, VIC 3000",
                new Integer[]{1});

        Assertions.assertNotNull(modelAndView.getModel());
        Assertions.assertTrue(modelAndView.getModel().containsKey("Error"));
        Assertions.assertEquals(modelAndView.getModel().get("Error"),
                "Phone must be in the correct format.");
    }
}
