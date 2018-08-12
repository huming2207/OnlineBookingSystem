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

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BusinessOwnerControllerTest
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
                .param("username", "whatever")
                .param("password", "1234567890"))
                .andExpect(status().is3xxRedirection())
                .andDo(mvcResult -> this.httpSession = (MockHttpSession) mvcResult.getRequest().getSession());
    }

    @Test
    public void addValidSerivce() throws Exception
    {
        mockMvc.perform(MockMvcRequestBuilders
                .post("/businessowner/service/add")
                .param("serviceName", "FireAlarm-as-a-Service")
                .param("duration", "30"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void addInvalidServiceWithZeroDuration() throws Exception
    {
        mockMvc.perform(MockMvcRequestBuilders
                .post("/businessowner/service/add")
                .param("serviceName", "FireAlarm-as-a-Service")
                .param("duration", "0"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString("Duration should be longer than 0 minute!")));
    }

    @Test
    public void addInvalidServiceWithEmptyName() throws Exception
    {
        mockMvc.perform(MockMvcRequestBuilders
                .post("/businessowner/service/add")
                .param("serviceName", "")
                .param("duration", "30"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString("Service name cannot be empty!")));
    }

    @Test
    public void cancelService() throws Exception
    {
        // This test MUST FAIL for some reasons I don't know
        mockMvc.perform(MockMvcRequestBuilders
                .post("/businessowner/service/add")
                .param("serviceName", "")
                .param("duration", "30"))
                .andExpect(status().is2xxSuccessful());
    }
}
