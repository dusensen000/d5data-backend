package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest @AutoConfigureMockMvc
class SecurityTest {
    @Autowired MockMvc mvc;
    @Test void anonymousIsRejected() throws Exception { mvc.perform(get("/api/hello")).andExpect(status().isUnauthorized()); }
    @Test void wrongPasswordIsRejected() throws Exception {
        mvc.perform(post("/api/login").with(csrf()).param("username","test").param("password","wrong")).andExpect(status().isUnauthorized());
    }
    @Test void csrfRequired() throws Exception {
        mvc.perform(post("/api/login").param("username","test").param("password","123456")).andExpect(status().isForbidden());
    }
    @Test void loginHelloLogout() throws Exception {
        var login = mvc.perform(post("/api/login").with(csrf()).param("username","test").param("password","123456"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.username").value("test")).andReturn();
        var session = (MockHttpSession) login.getRequest().getSession(false);
        mvc.perform(get("/api/hello").session(session)).andExpect(status().isOk()).andExpect(content().string("Hello World"));
        mvc.perform(post("/api/logout").session(session).with(csrf())).andExpect(status().isNoContent());
        mvc.perform(get("/api/hello")).andExpect(status().isUnauthorized());
    }
}
