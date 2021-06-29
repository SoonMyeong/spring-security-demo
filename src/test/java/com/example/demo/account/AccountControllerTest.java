package com.example.demo.account;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountService accountService;

    @Test
    @DisplayName("권한 없는 일반 비회원")
    public void index_anonymous() throws Exception {
        mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("권한 없는 일반 비회원 #annotation")
    @WithAnonymousUser
    public void index_anonymous2() throws Exception {
        mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk());
    }


    @Test
    @DisplayName("USER 권한을 가진 사용자 soon 이 / 에 접근했을 경우")
    public void index_user() throws Exception {
        mockMvc.perform(get("/").with(user("soon").roles("USER")))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("USER 권한을 가진 사용자 soon 이 / 에 접근했을 경우 #annotation")
    @WithMockUser(username = "soon", roles = "USER")
    public void index_user2() throws Exception {
        mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk());
    }


    @Test
    @DisplayName("USER 권한을 가진 사용자 soon 이 /admin 에 접근했을 경우")
    public void admin_user() throws Exception {
        mockMvc.perform(get("/admin").with(user("soon").roles("USER")))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("ADMIN 권한을 가진 사용자 admin 이 /admin 에 접근했을 경우")
    public void admin_admin() throws Exception {
        mockMvc.perform(get("/admin").with(user("admin").roles("ADMIN")))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @AfterTransaction
    @DisplayName("로그인 테스트")
    public void login() throws Exception {
        String password = "123";

        Account account = new Account();
        account.setUsername("soon");
        account.setPassword(password);
        account.setRole("USER");

        Account user = accountService.createNew(account);
        mockMvc.perform(formLogin().user(user.getUsername()).password(password))
                .andExpect(authenticated());
    }
    

}