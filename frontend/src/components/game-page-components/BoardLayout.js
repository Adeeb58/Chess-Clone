import React from "react";
// import { useState } from "react";
import Player from "./Player";
import Board from "./Board";
import "../component-styles/BoardLayout.css";

const BoardLayout = ({ addMove }) => {



  return (
    <div className="board-layout-main">
      <div className="board-layout-player">
        <Player name="Opponent" rating="1500" />
      </div>

      <div className="board-layout-chessboard">
        <div className="board">
          <Board addMove={addMove} />
        </div>
      </div>

      <div className="board-layout-player">
        <Player name="You" rating="1200" />
      </div>
    </div>
  );
};

export default BoardLayout;
