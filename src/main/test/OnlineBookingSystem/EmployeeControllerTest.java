package OnlineBookingSystem;

import OnlineBookingSystem.Controllers.EmployeeController;
import OnlineBookingSystem.ModelClasses.OBSFascade;
import OnlineBookingSystem.ModelClasses.OBSModel;
import OnlineBookingSystem.ModelClasses.Role;
import com.inflectra.spiratest.addons.junitextension.SpiraTestCase;
import com.inflectra.spiratest.addons.junitextension.SpiraTestConfiguration;
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

@SpiraTestConfiguration(
        //following are REQUIRED
        url = "https://rmit-university.spiraservice.net",
        login = "s3554025",
        rssToken = "{A80D40D0-9F2F-4D2C-9A3A-81B9E6262877}",
        projectId = 222
)

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
    @SpiraTestCase(testCaseId = 2276)
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
    @SpiraTestCase(testCaseId = 2277)
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
    @SpiraTestCase(testCaseId = 2278)
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
    @SpiraTestCase(testCaseId = 2279)
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
