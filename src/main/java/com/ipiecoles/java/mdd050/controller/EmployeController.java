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

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
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
            throw new EntityNotFoundException("L'employé d'identifiant" + id + "n'a pas été trouvé");
        }
        return optionalEmploye.get();
    }

    // 3 - Recherche par matricule
    @RequestMapping(method = RequestMethod.GET, value = "", params = {"matricule"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Employe searchByMatricule(@RequestParam String matricule){
        Employe employe = employeRepository.findByMatricule(matricule);
        if (employe == null){
            throw new EntityNotFoundException("L'employé de matricule " + matricule + " n'a pas été trouvé");
        }
        return employe;
    }

    // 4 - Liste des employés
    @RequestMapping(method = RequestMethod.GET, value = "", produces = MediaType.APPLICATION_JSON_VALUE, params = {"page", "size", "sortProperty", "sortDirection"})
    public Page<Employe> listeEmployes(
            // Paramètres passés dans l'URL
            @RequestParam(value = "0") Integer page,
            @RequestParam(value = "10") Integer size,
            @RequestParam(value = "matricule") String sortProperty,
            @RequestParam(value = "sortDirection", defaultValue =  "ASC") String sortDirection
    ){
        if (page < 0) {
            // Erreur 400
            throw new IllegalArgumentException("Le paramètre page doit être positif ou nul !");
        }
        if (size <= 0 || size > 50) {
            throw new IllegalArgumentException("Le paramètre size doit être compris entre 0 et 50 !");
        }
        if(!"ASC".equalsIgnoreCase(sortDirection) && !"DESC".equalsIgnoreCase(sortDirection)){
            throw new IllegalArgumentException("Le paramètre sortDirection doit valoir ASC ou DESC");
        }
        return employeRepository.findAll(PageRequest.of(page, size, Sort.Direction.fromString(sortDirection), sortProperty));
    }

    // 5 - Création d'un employé (8-a, 9-a)
    @RequestMapping(method = RequestMethod.POST, value = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    // Retourne 201 au lieu de 200
    @ResponseStatus(HttpStatus.CREATED)
    // @RequestBody = envoi de données complexes (ex : création d'un objet)
    public Employe createEmploye(@RequestBody Employe employe){
        if(employeRepository.findByMatricule(employe.getMatricule()) != null){
            throw new EntityExistsException("Il y a déjà un employé de matricule " + employe.getMatricule());
        }
        return employeRepository.save(employe);
    }

    // 6 - Modification d'un employé (8-b, 9-b)
    // équivalent de @RequestMapping(method = RequestMethod.PUT)
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Employe updateEmploye(@PathVariable Long id, @RequestBody Employe employe){
        if(!employeRepository.existsById(id)){
            throw new EntityNotFoundException("L'employé d'identifiant " + id + " n'a pas été trouvé");
        }
        return employeRepository.save(employe);
    }

    // 7 - Suppression d'un employé (8-c, 9-c)
    // équivalent de @RequestMapping(method = RequestMethod.DELETE)
    @DeleteMapping(value = "/{id}") // Pas de consumes ou de produces pour le delete
    @ResponseStatus(HttpStatus.NO_CONTENT) // Retourne 204
    public void deleteEmploye(@PathVariable Long id){
        if(!employeRepository.existsById(id)){
            throw new EntityNotFoundException("L'employé d'identifiant " + id + " n'a pas été trouvé");
        }
        employeRepository.deleteById(id);
    }

}
