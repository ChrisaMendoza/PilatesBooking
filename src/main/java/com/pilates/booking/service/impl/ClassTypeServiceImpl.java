package com.pilates.booking.service.impl;

import com.pilates.booking.domain.ClassType;
import com.pilates.booking.repository.ClassTypeRepository;
import com.pilates.booking.service.ClassTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.pilates.booking.domain.ClassType}.
 */
@Service
@Transactional
public class ClassTypeServiceImpl implements ClassTypeService {

    private static final Logger LOG = LoggerFactory.getLogger(ClassTypeServiceImpl.class);

    private final ClassTypeRepository classTypeRepository;

    public ClassTypeServiceImpl(ClassTypeRepository classTypeRepository) {
        this.classTypeRepository = classTypeRepository;
    }

    @Override
    public Mono<ClassType> save(ClassType classType) {
        LOG.debug("Request to save ClassType : {}", classType);
        return classTypeRepository.save(classType);
    }

    @Override
    public Mono<ClassType> update(ClassType classType) {
        LOG.debug("Request to update ClassType : {}", classType);
        return classTypeRepository.save(classType);
    }

    @Override
    public Mono<ClassType> partialUpdate(ClassType classType) {
        LOG.debug("Request to partially update ClassType : {}", classType);

        return classTypeRepository
            .findById(classType.getId())
            .map(existingClassType -> {
                if (classType.getName() != null) {
                    existingClassType.setName(classType.getName());
                }
                if (classType.getDescription() != null) {
                    existingClassType.setDescription(classType.getDescription());
                }
                if (classType.getCapacity() != null) {
                    existingClassType.setCapacity(classType.getCapacity());
                }

                return existingClassType;
            })
            .flatMap(classTypeRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ClassType> findAll() {
        LOG.debug("Request to get all ClassTypes");
        return classTypeRepository.findAll();
    }

    public Mono<Long> countAll() {
        return classTypeRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<ClassType> findOne(Long id) {
        LOG.debug("Request to get ClassType : {}", id);
        return classTypeRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete ClassType : {}", id);
        return classTypeRepository.deleteById(id);
    }
}
