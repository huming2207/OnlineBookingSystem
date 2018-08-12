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

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class EmployeeControllerTest
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
    public void addNewEmployee() throws Exception
    {
        mockMvc.perform(MockMvcRequestBuilders.post("/businessowner/employee/add")
                .session(this.httpSession)
                .param("name",  UUID.randomUUID().toString().substring(0, 8))
                .param("email", UUID.randomUUID().toString().substring(0, 8) + "@whatever.com")
                .param("phone", "0400000000")
                .param("address", "1024 Durian Rd, Melbourne, VIC 3000")
                .param("service", "1"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void addInvalidEmployeeWithDuplicatedName() throws Exception
    {
        String employeeName = UUID.randomUUID().toString().substring(0, 8);

        // The first pass
        mockMvc.perform(MockMvcRequestBuilders.post("/businessowner/employee/add")
                .session(this.httpSession)
                .param("name",  employeeName)
                .param("email", UUID.randomUUID().toString().substring(0, 8) + "@whatever.com")
                .param("phone", "0400000000")
                .param("address", "1024 Durian Rd, Melbourne, VIC 3000")
                .param("service", "1"))
                .andExpect(status().is3xxRedirection());

        // ...and do it again with the same request
        mockMvc.perform(MockMvcRequestBuilders.post("/businessowner/employee/add")
                .session(this.httpSession)
                .param("name",  employeeName)
                .param("email", UUID.randomUUID().toString().substring(0, 8) + "@whatever.com")
                .param("phone", "0400000000")
                .param("address", "1024 Durian Rd, Melbourne, VIC 3000")
                .param("service", "1"))
                .andExpect(content().string(containsString("Employee has already added!")));
    }

    @Test
    public void addInvalidEmployeeWithNoName() throws Exception
    {
        mockMvc.perform(MockMvcRequestBuilders.post("/businessowner/employee/add")
                .session(this.httpSession)
                .param("name",  "")
                .param("email", UUID.randomUUID().toString().substring(0, 8) + "@whatever.com")
                .param("phone", "0400000000")
                .param("address", "1024 Durian Rd, Melbourne, VIC 3000")
                .param("service", "1"))
                .andExpect(content().string(containsString("Name must only contain letters and spaces")));
    }

    @Test
    public void addInvalidChinesePhoneNumber() throws Exception
    {
        mockMvc.perform(MockMvcRequestBuilders.post("/businessowner/employee/add")
                .session(this.httpSession)
                .param("name",  UUID.randomUUID().toString().substring(0, 8))
                .param("email", UUID.randomUUID().toString().substring(0, 8) + "@whatever.com")
                .param("phone", "+8613800138000")
                .param("address", "1024 Durian Rd, Melbourne, VIC 3000")
                .param("service", "1"))
                .andExpect(content().string(containsString("Phone must be in the correct format.")));
    }
}
