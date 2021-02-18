package com.galvanize.shelternet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.galvanize.shelternet.model.AdoptionApplication;
import com.galvanize.shelternet.model.Animal;
import com.galvanize.shelternet.model.ApplicationStatus;
import com.galvanize.shelternet.repository.AnimalRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AdoptionApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AnimalRepository animalRepository;

    @Test
    public void submitAdoptionApplication() throws Exception {
        Animal animal = new Animal("DOGGY", "DOG", LocalDate.of(2020, 4, 15), "M", "WHITE");

        Animal animalSaved = animalRepository.save(animal);

        AdoptionApplication adoptionApplication = new AdoptionApplication("JOHN", "5131 W Thunderbird Rd.", "602-444-4444", animalSaved.getId());

        mockMvc.perform(post("/application")
                .content(objectMapper.writeValueAsString(adoptionApplication))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("JOHN"))
                .andExpect(jsonPath("$.address").value("5131 W Thunderbird Rd."))
                .andExpect(jsonPath("$.phoneNumber").value("602-444-4444"))
                .andExpect(jsonPath("$.animalId").value(animalSaved.getId()))
                .andExpect(jsonPath("$.status").value(ApplicationStatus.PENDING.name()));
    }

    @Test
    public void submitAdoptionApplication_WhenAnimalDoesntExists() throws Exception {
        AdoptionApplication adoptionApplication = new AdoptionApplication("JOHN", "5131 W Thunderbird Rd.", "602-444-4444", 1L);

        mockMvc.perform(post("/application")
                .content(objectMapper.writeValueAsString(adoptionApplication))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}