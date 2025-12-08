package com.bulkcrud.bulkcrud.controller;

import com.bulkcrud.bulkcrud.entity.Record;
import com.bulkcrud.bulkcrud.service.RecordService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecordController.class)
class RecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecordService recordService;

    @Autowired
    private ObjectMapper objectMapper;

    private Record record1;
    private Record record2;

    @BeforeEach
    void setUp() {
        record1 = new Record("Alice", "alice@example.com", 25);
        record1.setId(1L);

        record2 = new Record("Bob", "bob@example.com", 30);
        record2.setId(2L);
    }

    @Test
    void getAllRecords_success() throws Exception {
        when(recordService.getAll()).thenReturn(Arrays.asList(record1, record2));

        mockMvc.perform(get("/records"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));
    }

    @Test
    void addRecords_success() throws Exception {
        when(recordService.saveAll(anyList())).thenReturn(Arrays.asList(record1, record2));

        mockMvc.perform(post("/records/bulk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Arrays.asList(record1, record2))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));
    }

    @Test
    public void addRecords_emptyInput_badRequest() throws Exception {
        mockMvc.perform(post("/records/bulk")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"records\": []}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Record list cannot be empty"));
    }

}
