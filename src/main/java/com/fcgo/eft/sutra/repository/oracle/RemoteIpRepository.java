package com.fcgo.eft.sutra.repository.oracle;

import com.fcgo.eft.sutra.entity.oracle.RemoteIp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RemoteIpRepository extends JpaRepository<RemoteIp, String> {
    Optional<RemoteIp> findByIdAndUsername(String id, String username);
}
