package com.ipiecoles.java.mdd050.controller;

import com.ipiecoles.java.mdd050.model.Employe;
import com.ipiecoles.java.mdd050.model.Manager;
import com.ipiecoles.java.mdd050.model.Technicien;
import com.ipiecoles.java.mdd050.repository.EmployeRepository;
import com.ipiecoles.java.mdd050.repository.ManagerRepository;
import com.ipiecoles.java.mdd050.repository.TechnicienRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@RestController
@RequestMapping(value = "/managers")
public class ManagerController {

    // 10 - Ajouter ou supprimer un technicien dans l'équipe d'un manager

    @Autowired
    private TechnicienRepository technicienRepository;

    // /managers/532/equipe/576/remove
    @RequestMapping(method = RequestMethod.GET, value = "/{id}/equipe/{id}/remove")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeTechnicienFromEquipe(@PathVariable Long idManager, @PathVariable Long idTechnicien){

        // Récupérer un technicien
        Optional<Technicien> technicienOptional = technicienRepository.findById(idTechnicien);
        if(technicienOptional.isEmpty()){
            throw new EntityNotFoundException("Impossible de trouve le technicien d'identifiant" + idTechnicien);
        }

        // Set son manager à null
        Technicien technicien = technicienOptional.get();
        if (technicien.getManager() == null) {
            throw new IllegalArgumentException("Le technicien n'a pas de manager !");
        }
        if (technicien.getManager().getId().equals(idManager)) {
            throw new IllegalArgumentException("Le manager d'identifiant " + idManager + " n'a pas le technicien d'identifiant " + idTechnicien + " dans son équipe !");
        }
        technicien.setManager(null);

        // Je persiste la modification
        technicienRepository.save(technicien);
    }

    @Autowired
    private EmployeRepository employeRepository;

    @Autowired
    private ManagerRepository managerRepository;


    // /managers/532/equipe/T00110/add
    @RequestMapping(method = RequestMethod.GET, value = "/{id}/equipe/{matricule}/add")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addTechnicienToEquipe(@PathVariable Long id, @PathVariable String matricule){
        // Récupérer le technicien à partir du matricule
        Employe employe = employeRepository.findByMatricule(matricule);
        if(!(employe instanceof Technicien)){
            throw new IllegalArgumentException("L'employé de matricule " + matricule + " n'est pas un technicien !");
        }
        Technicien technicien = (Technicien)employe;

        if(technicien.getManager() != null){
            throw new IllegalArgumentException("Le technicien de matricule " + matricule + " a déjà un manager !");
        }

        // Récupérer le manager à partir de son id
        Optional<Manager> managerOptional = managerRepository.findById(id);
        if(managerOptional.isEmpty()){
            throw new EntityNotFoundException("Le manager d'identifiant " + id + " n'existe pas !");
        }
        Manager manager = managerOptional.get();

        // Setter le manager au technicien et sauvegarder
        technicien.setManager(manager);

        technicienRepository.save(technicien);
    }
}
