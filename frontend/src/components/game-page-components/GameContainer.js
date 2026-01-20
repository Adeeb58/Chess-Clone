import React, { useState, useCallback } from "react";
import BoardLayout from "./BoardLayout";
import GamePlayControlContainer from "./GamePlayControlContainer";
import GameOverModal from "./GameOverModal";

const GameContainer = () => {
  const [moves, setMoves] = useState([]);  // Holds the list of moves
  const [gameOverState, setGameOverState] = useState({ show: false, message: "" });

  const handleGameUpdate = useCallback((status, message) => {
    if (status === 'COMPLETED' || status === 'FINISHED') {
      setGameOverState({ show: true, message: message || "Game Over" });
    }
  }, []);

  const handleNewGame = () => {
    window.location.reload();
  };

  // Function to add a move to the history
  const addMove = (move) => {
    // we will check if it is white move or black move
    if (move.piece !== move.piece.toLowerCase()) {
      const newMove = { move, moveToWhite: move.moveTo };
      setMoves((moves) => [...moves, newMove]);
    }
    else {
      setMoves((prevMoves) => {
        const newMoves = [...prevMoves];
        const lastMove = {
          ...newMoves[newMoves.length - 1],
          moveToBlack: move.moveTo,
          tc: `Black's Turn: ${move.tc}`,
          tr: move.tr
        };
        newMoves[newMoves.length - 1] = lastMove;  // Update the last move
        return newMoves;  // Return a new array to trigger re-render
      });
    }
  };

  return (
    <div className="game-container">
      <BoardLayout addMove={addMove} />
      <GamePlayControlContainer moves={moves} onGameUpdate={handleGameUpdate} />
      <GameOverModal
        show={gameOverState.show}
        message={gameOverState.message}
        onNewGame={handleNewGame}
      />
    </div>
  );
};

export default GameContainer;
