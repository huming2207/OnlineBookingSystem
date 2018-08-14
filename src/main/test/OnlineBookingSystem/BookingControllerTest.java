package OnlineBookingSystem;

import OnlineBookingSystem.Controllers.BookingController;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

@SpiraTestConfiguration(
        //following are REQUIRED
        url = "https://rmit-university.spiraservice.net",
        login = "s3554025",
        rssToken = "{A80D40D0-9F2F-4D2C-9A3A-81B9E6262877}",
        projectId = 222
)

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class BookingControllerTest
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

        httpSession.setAttribute("id", obs.getCustomerByUsername("customer123").getId());
        httpSession.setAttribute("role", Role.Customer);
    }

    @Test
    @SpiraTestCase(testCaseId = 2268)
    public void addBooking()
    {
        BookingController bookingController = new BookingController(this.httpSession);
        String dateString = this.getDateString(LocalDate.now().plus(2, ChronoUnit.DAYS));

        ModelAndView modelAndView = bookingController.customerAddBookingUpdate(
                                    1,
                                    dateString,
                                    "10:30",
                                    1,
                                    1, new RedirectAttributesModelMap());

        Assertions.assertNotNull(modelAndView.getViewName());
        Assertions.assertEquals("redirect:/customer/dashboard", modelAndView.getViewName());
        Assertions.assertNotNull(modelAndView.getStatus());
        Assertions.assertTrue(
                modelAndView.getStatus().is2xxSuccessful() || modelAndView.getStatus().is3xxRedirection());
    }

    @Test
    @SpiraTestCase(testCaseId = 2269)
    public void addPastInvalidBooking() throws Exception
    {
        BookingController bookingController = new BookingController(this.httpSession);
        ModelAndView modelAndView = bookingController.customerAddBookingUpdate(
                1,
                "2000-01-01",
                "10:30",
                1,
                1, new RedirectAttributesModelMap());

        Assertions.assertNotNull(modelAndView.getModel());
        Assertions.assertTrue(modelAndView.getModel().containsKey("Error"));
        Assertions.assertEquals(modelAndView.getModel().get("Error"),
                "The Service, Employee combination you " +
                "have selected is no longer available, please return to your " +
                "dashboard and try selecting a different slot.");
    }

    @Test
    @SpiraTestCase(testCaseId = 2270)
    public void addWrongFormattedBooking() throws Exception
    {
        BookingController bookingController = new BookingController(this.httpSession);

        Assertions.assertThrows(DateTimeParseException.class, () -> {
            ModelAndView modelAndView = bookingController.customerAddBookingUpdate(
                    1,
                    "not-a-date-string",
                    "not-a-time-string",
                    1,
                    1, new RedirectAttributesModelMap());

            Assertions.assertNotNull(modelAndView.getStatus());
            Assertions.assertTrue(modelAndView.getStatus().is5xxServerError());
        });
    }

    @Test
    @SpiraTestCase(testCaseId = 2271)
    public void addClosedInvalidBooking() throws Exception
    {
        BookingController bookingController = new BookingController(this.httpSession);
        String dateString = this.getDateString(LocalDate.now().plus(2, ChronoUnit.DAYS));

        ModelAndView modelAndView = bookingController.customerAddBookingUpdate(
                1,
                dateString,
                "00:30",
                1,
                1, new RedirectAttributesModelMap());

        Assertions.assertNotNull(modelAndView.getModel());
        Assertions.assertTrue(modelAndView.getModel().containsKey("Error"));
        Assertions.assertEquals(modelAndView.getModel().get("Error"),
                "The Service, Employee combination you " +
                        "have selected is no longer available, please return to your " +
                        "dashboard and try selecting a different slot.");
    }

    private String getDateString(LocalDate date)
    {
        DateTimeFormatter simpleDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return simpleDateFormat.format(date);
    }
}
