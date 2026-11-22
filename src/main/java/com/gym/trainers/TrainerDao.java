package com.gym.trainers;

import java.util.List;
import java.util.Optional;

public interface TrainerDao {
    List<Trainer> findAll();
    Optional<Trainer> findById(long id);
    Trainer save(Trainer trainer);
    Trainer update(Trainer trainer);
    void delete(long id);
}