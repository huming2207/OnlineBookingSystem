package OnlineBookingSystem;

import OnlineBookingSystem.Controllers.LoginController;
import OnlineBookingSystem.ModelClasses.OBSFascade;
import OnlineBookingSystem.ModelClasses.OBSModel;
import OnlineBookingSystem.ModelClasses.Role;
import OnlineBookingSystem.ModelClasses.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class LoginControllerTest
{
    @Autowired
    private LoginController loginController;

    @Test
    public void performValidAdminLoginTest()
    {
        ModelAndView modelAndView = loginController.login("whatever", "1234567890");
        HttpSession httpSession = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getSession();

        // Test roles, shouldn't be null
        OBSFascade obs = OBSModel.getModel();
        Role role = (Role)httpSession.getAttribute("role");
        Assertions.assertNotNull(role);
        Assertions.assertSame(role, Role.BusinessOwner);

        // Test the user, should be a valid BusinessOwner
        User user = obs.getBusinessOwnerById((Integer)httpSession.getAttribute("id"));
        Assertions.assertNotNull(user);
    }

    @Test
    public void performValidCustomerLoginTest()
    {
        // Do a admin login
        ModelAndView modelAndView = loginController.login("customer123", "1234567890");
        HttpSession httpSession = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getSession();

        // Test roles, shouldn't be null
        OBSFascade obs = OBSModel.getModel();
        Role role = (Role)httpSession.getAttribute("role");
        Assertions.assertNotNull(role);
        Assertions.assertSame(role, Role.Customer);

        // Test the user, should be a valid BusinessOwner
        User user = obs.getCustomerById((Integer)httpSession.getAttribute("id"));
        Assertions.assertNotNull(user);
    }

    @Test
    public void performInvalidAdminLoginTest()
    {
        // Do a invalid admin login
        ModelAndView modelAndView = loginController.login("null-admin", "not-a-valid-password");
        HttpSession httpSession = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getSession();

        // Test roles, should be null
        OBSFascade obs = OBSModel.getModel();
        Role role = (Role)httpSession.getAttribute("role");
        Assertions.assertNull(role);

        // Test the user, should throws a null pointer exception as the user info is not correct.
        Assertions.assertThrows(NullPointerException.class, () -> {
            User user = obs.getBusinessOwnerById((Integer)httpSession.getAttribute("id"));
        });
    }

    @Test
    public void performInvalidCustomerLoginTest() throws Exception
    {
        // Do a invalid admin login
        ModelAndView modelAndView = loginController.login("null-user", "not-a-valid-password");
        HttpSession httpSession = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getSession();

        // Test roles, should be null
        OBSFascade obs = OBSModel.getModel();
        Role role = (Role)httpSession.getAttribute("role");
        Assertions.assertNull(role);

        // Test the user, should throws a null pointer exception as the user info is not correct.
        Assertions.assertThrows(NullPointerException.class, () -> {
            User user = obs.getCustomerById((Integer)httpSession.getAttribute("id"));
        });
    }

}
