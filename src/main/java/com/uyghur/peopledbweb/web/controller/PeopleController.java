package com.uyghur.peopledbweb.web.controller;

import com.uyghur.peopledbweb.biz.model.Person;
import com.uyghur.peopledbweb.biz.service.PersonService;
import com.uyghur.peopledbweb.data.FileStorageRepository;
import com.uyghur.peopledbweb.data.PersonRepository;
import com.uyghur.peopledbweb.exception.StorageException;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

@Controller
@RequestMapping("/people")
@Log4j2
public class PeopleController {

    public static final String DISPO = """
             attachment; filename="%s"
            """;
    private PersonRepository personRepository;

    private FileStorageRepository fileStorageRepository;

    private PersonService personService;

    public PeopleController(PersonRepository personRepository,
                            FileStorageRepository fileStorageRepository, PersonService personService) {
        this.personRepository = personRepository;
        this.fileStorageRepository = fileStorageRepository;
        this.personService = personService;
    }

    @ModelAttribute("people")
    public Page<Person> getPeople(@PageableDefault(size = 10) Pageable page){
        return personService.findAll(page);
    }

    @ModelAttribute
    public Person getPerson(){
        return new Person();
    }

    @GetMapping
    public String showPeoplePage() {

        return "people";
    }

    @GetMapping("/images/{resource}")
    public ResponseEntity<Resource> getResource(@PathVariable String resource){
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, format(DISPO, resource))
                .body(fileStorageRepository.findByName(resource));

    }




    @PostMapping
    public String savePerson(Model model,@Valid Person person, Errors errors, @RequestParam("photoFileName") MultipartFile photoFile) throws IOException {
        log.info("Filename "+ photoFile.getOriginalFilename());
        log.info("File size: "+ photoFile.getSize());
        log.info("Errors "+errors);
        if (!errors.hasErrors()) {
            try {
//                fileStorageRepository.save(photoFile.getOriginalFilename(),photoFile.getInputStream());
//                personRepository.save(person);
                personService.save(person, photoFile.getInputStream());
                return "redirect:people";
            } catch (StorageException e) {
                model.addAttribute("errorMsg","System is currently unable to accept photo files at this time.");
                return "people";
            }
        }
        return "people";
    }

    @PostMapping(params = "action=delete")
    public String deletePeople(@RequestParam Optional<List<Long>> selections){
        if (selections.isPresent()) {
//            personRepository.deleteAllById(selections.get());
            personService.deleteAllById(selections.get());
        }
        return "redirect:people";
    }

    @PostMapping(params = "action=edit")
    public String editPerson(@RequestParam Optional<List<Long>> selections,Model model){
        if (selections.isPresent()) {
            Optional<Person> person = personRepository.findById(selections.get().get(0));
            model.addAttribute("person",person);
        }
        return "people";
    }


    @PostMapping(params = "action=import")
    public String importCSV(@RequestParam MultipartFile csvFile){
        log.info("File Name: "+csvFile.getOriginalFilename());
        log.info("File Size: "+csvFile.getSize());
        try {
            personService.importCSV(csvFile.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:people";
    }
}
