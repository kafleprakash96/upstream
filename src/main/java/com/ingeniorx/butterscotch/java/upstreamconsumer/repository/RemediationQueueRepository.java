package com.ingeniorx.butterscotch.java.upstreamconsumer.repository;

import com.ingeniorx.butterscotch.java.upstreamconsumer.model.RemediationQueue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RemediationQueueRepository extends JpaRepository<RemediationQueue,Long> {

    public List<RemediationQueue> findByFilename(String filename);
}
