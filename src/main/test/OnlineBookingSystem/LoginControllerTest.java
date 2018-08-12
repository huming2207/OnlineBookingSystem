package OnlineBookingSystem;

import OnlineBookingSystem.Controllers.LoginController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class LoginControllerTest
{
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void performValidAdminLoginTest() throws Exception
    {
        mockMvc.perform(MockMvcRequestBuilders
                .post("/")
                .param("username", "whatever")
                .param("password", "1234567890"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void performValidCustomerLoginTest() throws Exception
    {
        mockMvc.perform(MockMvcRequestBuilders
                .post("/")
                .param("username", "customer123")
                .param("password", "1234567890"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void performInvalidAdminLoginTest() throws Exception
    {
        mockMvc.perform(MockMvcRequestBuilders
                .post("/")
                .param("username", "whatever")
                .param("password", "just_a_wrong_password"))
                .andExpect(content().string(containsString("Incorrect username or password")));
    }

    @Test
    public void performInvalidCustomerLoginTest() throws Exception
    {
        mockMvc.perform(MockMvcRequestBuilders
                .post("/")
                .param("username", "customer123")
                .param("password", "just_a_wrong_password"))
                .andExpect(content().string(containsString("Incorrect username or password")));
    }

}
