package com.gym.trainers;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class TrainerService {
    
    private final TrainerDao trainerDao ;
    
    public TrainerService(TrainerDao trainerDao){
        this.trainerDao = trainerDao;
    }

    private void Validate(Trainer t){
        if(t == null){
            throw new IllegalArgumentException("Trainer khong duoc null"); 
        }
        if(t.getFullName() == null || t.getFullName().isBlank()){
            throw new IllegalArgumentException("Name Trainer khong duoc null");
        }
    }
    public Trainer save(Trainer t){
        Validate(t);
        return trainerDao.save(t);
    }
    public Trainer update(Trainer t){
        Validate(t);
        return trainerDao.update(t);
    }

    public List<Trainer> findAll() {
        return trainerDao.findAll();
    }

    public Optional<Trainer> findById(long id) {
        return trainerDao.findById(id);
    }

    public void delete(long id) {
        trainerDao.delete(id);
    }
}   

