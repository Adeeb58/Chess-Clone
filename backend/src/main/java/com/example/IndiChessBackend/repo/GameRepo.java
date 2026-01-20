package com.example.IndiChessBackend.repo;

import com.example.IndiChessBackend.model.Game;
import com.example.IndiChessBackend.model.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepo extends JpaRepository<Game, Long> {
    List<Game> findByWhitePlayerOrBlackPlayer(User whitePlayer, User blackPlayer);
}
