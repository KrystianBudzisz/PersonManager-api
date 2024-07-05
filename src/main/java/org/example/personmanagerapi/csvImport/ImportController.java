package org.example.personmanagerapi.csvImport;

import org.example.personmanagerapi.csvImport.model.ImportStatusDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/imports")
public class ImportController {

    @Autowired
    private CSVImportService csvImportService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'IMPORTER')")
    public ImportStatusDTO importCSV(@RequestParam("file") MultipartFile file) {
        return csvImportService.startCSVImport(file);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'IMPORTER')")
    public ImportStatusDTO getImportStatus(@PathVariable Long id) {
        return csvImportService.getImportStatus(id);
    }
}





