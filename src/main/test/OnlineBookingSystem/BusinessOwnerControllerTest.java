package OnlineBookingSystem;

import OnlineBookingSystem.Controllers.BusinessOwnerController;
import OnlineBookingSystem.ModelClasses.OBSFascade;
import OnlineBookingSystem.ModelClasses.OBSModel;
import OnlineBookingSystem.ModelClasses.Role;
import com.inflectra.spiratest.addons.junitextension.SpiraTestCase;
import com.inflectra.spiratest.addons.junitextension.SpiraTestConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import javax.servlet.http.HttpSession;

@SpiraTestConfiguration(
        //following are REQUIRED
        url = "https://rmit-university.spiraservice.net",
        login = "s3554025",
        rssToken = "{A80D40D0-9F2F-4D2C-9A3A-81B9E6262877}",
        projectId = 222
)

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class BusinessOwnerControllerTest
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
    @SpiraTestCase(testCaseId = 2272)
    public void addValidSerivce()
    {
        BusinessOwnerController businessOwnerController = new BusinessOwnerController(this.httpSession);
        ModelAndView modelAndView = businessOwnerController.createNewService(
                "FireAlarm-as-a-Service", 30);

        Assertions.assertEquals("redirect:/businessowner/service", modelAndView.getViewName());
    }

    @Test
    @SpiraTestCase(testCaseId = 2273)
    public void addInvalidServiceWithZeroDuration()
    {
        BusinessOwnerController businessOwnerController = new BusinessOwnerController(this.httpSession);
        ModelAndView modelAndView = businessOwnerController.createNewService(
                "FireAlarm-as-a-Service", 0);

        Assertions.assertEquals(modelAndView.getModel().get("Error"),
                "Duration should be longer than 0 minute!");
    }

    @Test
    @SpiraTestCase(testCaseId = 2274)
    public void addInvalidServiceWithEmptyName()
    {
        BusinessOwnerController businessOwnerController = new BusinessOwnerController(this.httpSession);
        ModelAndView modelAndView = businessOwnerController.createNewService("", 30);

        Assertions.assertEquals(modelAndView.getModel().get("Error"), "Service name cannot be empty!");
    }

    @Test
    @SpiraTestCase(testCaseId = 2275)
    public void cancelService()
    {
        // This test won't pass for some reasons I don't know
        BusinessOwnerController businessOwnerController = new BusinessOwnerController(this.httpSession);
        ModelAndView modelAndView = businessOwnerController.cancelService(1, new RedirectAttributesModelMap());

        Assertions.assertEquals("redirect:/businessowner/service", modelAndView.getViewName());
    }
}
