package OnlineBookingSystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.servlet.http.HttpSession;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BookingControllerTest
{
    @Autowired
    private MockMvc mockMvc;

    private HttpSession httpSession;

    @BeforeEach
    public void prepareLoginSession() throws Exception
    {
        // A similar example of holding session:
        // https://stackoverflow.com/questions/13687055/spring-mvc-3-1-integration-tests-with-session-support
        mockMvc.perform(MockMvcRequestBuilders
                .post("/")
                .param("username", "customer123")
                .param("password", "1234567890"))
                .andExpect(status().is3xxRedirection())
                .andDo(mvcResult -> this.httpSession = mvcResult.getRequest().getSession());
    }

    @Test
    public void addBooking() throws Exception
    {

    }


}
