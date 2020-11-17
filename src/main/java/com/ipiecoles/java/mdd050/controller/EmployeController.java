package com.ipiecoles.java.mdd050.controller;

import com.ipiecoles.java.mdd050.model.Commercial;
import com.ipiecoles.java.mdd050.model.Employe;
import com.ipiecoles.java.mdd050.repository.EmployeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

// Création du controller permettant d'accéder à des infos sur l'employé (à http://localhost:5367/employes)
@RestController
@RequestMapping("/employes")
public class EmployeController {

    // Appel du fichier où se trouve les requêtes SQL souhaitées
    @Autowired
    private EmployeRepository employeRepository;

    // 1 - Compter le nombre d'employés
    @RequestMapping(method = RequestMethod.GET, value = "/count", produces = MediaType.APPLICATION_JSON_VALUE) // produces = "application/json"
    public Long countEmployes(){
        return employeRepository.count();
    }

    // 2 - Afficher un employé
    @RequestMapping(method = RequestMethod.GET, value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Employe getEmploye(@PathVariable(value = "id") Long id){
        Optional<Employe> optionalEmploye = employeRepository.findById(id);
        if(optionalEmploye.isEmpty()) {
            // Erreur 404
        }
        return optionalEmploye.get();
    }

    // 3 - Recherche par matricule
    @RequestMapping(method = RequestMethod.GET, value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Employe searchByMatricule(@RequestParam String matricule){
        return employeRepository.findByMatricule(matricule);
    }

    // 4 - Liste des employés
    @RequestMapping(method = RequestMethod.GET, value = "", produces = MediaType.APPLICATION_JSON_VALUE, params = {"page", "size", "sortProperty", "sortDirection"})
    public Page<Employe> listeEmployes(
            @RequestParam Integer page,
            @RequestParam Integer size,
            @RequestParam String sortProperty,
            @RequestParam String sortDirection
    ){
        return employeRepository.findAll(PageRequest.of(page, size, Sort.Direction.fromString(sortDirection), sortProperty));
    }

    // 5 - Création d'un Commercial (à corriger)
    @RequestMapping(method = RequestMethod.POST, value = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE) // produces = "application/json"
    @ResponseStatus(value = HttpStatus.CREATED)
    public Employe createCommercial(@RequestBody Commercial employe){
        return employeRepository.save(employe);
    }

    // 6 - Modification d'un Commercial
}
