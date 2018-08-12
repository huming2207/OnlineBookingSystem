package OnlineBookingSystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BookingControllerTest
{
    @Autowired
    private MockMvc mockMvc;

    private MockHttpSession httpSession;

    @BeforeEach
    public void prepareLoginSession() throws Exception
    {
        // A similar example of holding session:
        // https://stackoverflow.com/questions/13687055/spring-mvc-3-1-integration-tests-with-session-support
        // https://stackoverflow.com/questions/26142631/why-does-spring-mockmvc-result-not-contain-a-cookie/26281932#26281932
        mockMvc.perform(MockMvcRequestBuilders
                .post("/")
                .param("username", "customer123")
                .param("password", "1234567890"))
                .andExpect(status().is3xxRedirection())
                .andDo(mvcResult -> this.httpSession = (MockHttpSession) mvcResult.getRequest().getSession());
    }

    @Test
    public void addBooking() throws Exception
    {
        String dateString = this.getDateString(LocalDate.now().plus(2, ChronoUnit.DAYS));
        mockMvc.perform(MockMvcRequestBuilders
                .post(String.format("/booking/cuaddbooking/1/%s/10:30/1/1", dateString))
                .session(this.httpSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(content().string(containsString("Booking Added")));
    }

    @Test
    public void addPastInvalidBooking() throws Exception
    {
        mockMvc.perform(MockMvcRequestBuilders
                .post("/booking/cuaddbooking/1/2000-1-1/10:30/1/1")
                .session(this.httpSession))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(containsString(
                        "The Service, Employee combination you have selected is no longer available, " +
                                "please return to your dashboard and try selecting a different slot.")));
    }

    @Test
    public void addWrongFormattedBooking() throws Exception
    {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/booking/cuaddbooking/1/null-lol-1/null:hahaha/1/1")
                .session(this.httpSession))
                .andExpect(status().is5xxServerError());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/booking/cuaddbooking/1/null-lol-1/null:hahaha/1/1")
                .session(this.httpSession))
                .andExpect(status().is5xxServerError());
    }

    @Test
    public void addClosedInvalidBooking() throws Exception
    {
        String dateString = this.getDateString(LocalDate.now().plus(2, ChronoUnit.DAYS));
        mockMvc.perform(MockMvcRequestBuilders
                .post(String.format("/booking/cuaddbooking/1/%s/00:30/1/1", dateString))
                .session(this.httpSession))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(containsString(
                        "The Service, Employee combination you have selected is no longer available, " +
                                "please return to your dashboard and try selecting a different slot.")));
    }

    private String getDateString(LocalDate date)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(date);
    }

    private String getTimeString(LocalTime time)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        return simpleDateFormat.format(time);
    }
}
