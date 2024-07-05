package org.example.personmanagerapi.csvimport;


import org.example.personmanagerapi.csvImport.ImportStatusRepository;
import org.example.personmanagerapi.csvImport.model.ImportStatus;
import org.example.personmanagerapi.csvImport.model.ImportStatusDTO;
import org.example.personmanagerapi.person.PersonRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ImportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ImportStatusRepository importStatusRepository;

    @Autowired
    private PersonRepository personRepository;

    private ImportStatusDTO importStatusDTO;

    private MockMultipartFile createCSVFile() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(byteArrayOutputStream);
        writer.println("type,firstName,lastName,pesel,height,weight,email,universityName,yearOfStudy,fieldOfStudy,scholarship");
        writer.println("student,John,Doe,12345678901,180.5,75.0,john.doe@example.com,XYZ University,3,Computer Science,1000.0");
        writer.close();
        return new MockMultipartFile("file", "test.csv", MediaType.TEXT_PLAIN_VALUE, byteArrayOutputStream.toByteArray());
    }

    @BeforeEach
    public void setUp() {

        importStatusDTO = new ImportStatusDTO();
        importStatusDTO.setId(1L);
        importStatusDTO.setStatus("IN_PROGRESS");
        importStatusDTO.setCreatedDate(LocalDateTime.now());
        importStatusDTO.setStartedDate(null);
        importStatusDTO.setProcessedRecords(0);
    }

    @AfterEach
    public void cleanUp() {
        importStatusRepository.deleteAll();
        personRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "IMPORTER"})
    public void importCSV_ShouldReturnImportStatus() throws Exception {
        MockMultipartFile file = createCSVFile();

        mockMvc.perform(multipart("/api/imports").file(file))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));

        ImportStatus importStatus = importStatusRepository.findAll().get(0);
        assertThat(importStatus).isNotNull();
        assertThat(importStatus.getStatus()).isEqualTo("IN_PROGRESS");

    }

    @Test
    @WithMockUser(roles = {"ADMIN", "IMPORTER"})
    public void importCSV_ShouldThrowCSVProcessingException_WhenFileIsEmpty() throws Exception {
        MockMultipartFile emptyFile = new MockMultipartFile("file", "empty.csv", MediaType.TEXT_PLAIN_VALUE, new byte[0]);

        mockMvc.perform(multipart("/api/imports").file(emptyFile))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Bad request. File is empty."));

        assertThat(importStatusRepository.findAll()).isEmpty();
        assertThat(personRepository.findAll()).isEmpty();
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "IMPORTER"})
    public void getImportStatus_ShouldReturnImportStatus() throws Exception {
        ImportStatus importStatus = new ImportStatus();
        importStatus.setStatus("IN_PROGRESS");
        importStatus.setCreatedDate(LocalDateTime.now());
        importStatusRepository.save(importStatus);

        mockMvc.perform(get("/api/imports/{id}", importStatus.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(importStatus.getId()))
                .andExpect(jsonPath("$.status").value(importStatus.getStatus()));
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "IMPORTER"})
    public void getImportStatus_ShouldThrowImportStatusNotFoundException() throws Exception {
        mockMvc.perform(get("/api/imports/{id}", 999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Import status not found"));
    }
}