package com.fcgo.eft.sutra.repository.oracle;


import com.fcgo.eft.sutra.entity.oracle.NchlToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NchlTokenRepository extends JpaRepository<NchlToken, Integer> {
}
