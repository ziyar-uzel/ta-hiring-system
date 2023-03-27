package nl.tudelft.sem.submission.repositories;

import nl.tudelft.sem.submission.entities.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {

}
