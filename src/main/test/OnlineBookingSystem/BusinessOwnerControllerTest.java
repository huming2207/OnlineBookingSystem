package OnlineBookingSystem;

import OnlineBookingSystem.Controllers.BusinessOwnerController;
import OnlineBookingSystem.ModelClasses.OBSFascade;
import OnlineBookingSystem.ModelClasses.OBSModel;
import OnlineBookingSystem.ModelClasses.Role;
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
    public void addValidSerivce()
    {
        BusinessOwnerController businessOwnerController = new BusinessOwnerController(this.httpSession);
        ModelAndView modelAndView = businessOwnerController.createNewService(
                "FireAlarm-as-a-Service", 30);

        Assertions.assertEquals("redirect:/businessowner/service", modelAndView.getViewName());
    }

    @Test
    public void addInvalidServiceWithZeroDuration()
    {
        BusinessOwnerController businessOwnerController = new BusinessOwnerController(this.httpSession);
        ModelAndView modelAndView = businessOwnerController.createNewService(
                "FireAlarm-as-a-Service", 0);

        Assertions.assertEquals(modelAndView.getModel().get("Error"),
                "Duration should be longer than 0 minute!");
    }

    @Test
    public void addInvalidServiceWithEmptyName()
    {
        BusinessOwnerController businessOwnerController = new BusinessOwnerController(this.httpSession);
        ModelAndView modelAndView = businessOwnerController.createNewService("", 30);

        Assertions.assertEquals(modelAndView.getModel().get("Error"), "Service name cannot be empty!");
    }

    @Test
    public void cancelService()
    {
        // This test won't pass for some reasons I don't know
        BusinessOwnerController businessOwnerController = new BusinessOwnerController(this.httpSession);
        ModelAndView modelAndView = businessOwnerController.cancelService(1, new RedirectAttributesModelMap());

        Assertions.assertEquals("redirect:/businessowner/service", modelAndView.getViewName());
    }
}
