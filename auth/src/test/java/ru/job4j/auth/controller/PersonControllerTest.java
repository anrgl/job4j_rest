package ru.job4j.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.job4j.auth.AuthApplication;
import ru.job4j.auth.domain.Person;
import ru.job4j.auth.repository.PersonRepository;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = AuthApplication.class)
@AutoConfigureMockMvc
class PersonControllerTest {

    private final String BASE_URL = "/api/v1/person";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonRepository persons;

    @Test
    public void findAllShouldReturnStatusOk() throws Exception {
        this.mockMvc.perform(get(BASE_URL + "/"))
                .andExpect(status().isOk());
    }

    @Test
    public void findByIdShouldReturnStatusOk() throws Exception {
        when(persons.findById(1)).thenReturn(Optional.of(Person.of("username", "password")));
        this.mockMvc.perform(get(BASE_URL + "/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void findByIdShouldReturnStatusNotFound() throws Exception {
        this.mockMvc.perform(get(BASE_URL + "/42"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void createShouldReturnStatusCreated() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(Person.of("username", "qwerty"));
        this.mockMvc.perform(post(BASE_URL + "/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isCreated());
        ArgumentCaptor<Person> argument = ArgumentCaptor.forClass(Person.class);
        verify(persons).save(argument.capture());
        assertThat(argument.getValue().getLogin(), is("username"));
    }

    @Test
    public void updateShouldReturnStatusOk() throws Exception {
        when(persons.findById(1)).thenReturn(Optional.of(Person.of("username", "password")));
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(Person.of("new username", "qwerty"));
        this.mockMvc.perform(put(BASE_URL + "/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk());
        ArgumentCaptor<Person> argument = ArgumentCaptor.forClass(Person.class);
        verify(persons).save(argument.capture());
        assertThat(argument.getValue().getLogin(), is("new username"));
    }

    @Test
    public void deleteShouldReturnStatusOk() throws Exception {
        when(persons.findById(1)).thenReturn(Optional.of(Person.of("username", "password")));
        this.mockMvc.perform(delete(BASE_URL + "/1"))
                .andExpect(status().isOk());
    }
}