# JChess

[![Build Status](https://travis-ci.org/ddobbelaere/jchess.svg?branch=master)](https://travis-ci.org/ddobbelaere/jchess)
[![Coverage Status](https://coveralls.io/repos/github/ddobbelaere/jchess/badge.svg?branch=master)](https://coveralls.io/github/ddobbelaere/jchess?branch=master)
[![Jitpack](https://jitpack.io/v/ddobbelaere/jchess.svg)](https://jitpack.io/#ddobbelaere/jchess)

JChess is a pure Java chess library.

### API

The public API of the latest release is [available here](https://javadoc.jitpack.io/com/github/ddobbelaere/jchess/latest/javadoc/).

### Examples

```java
// Play a game from a given position (as FEN string).
Game game = new Game("7k/4NK1p/8/8/8/8/8/8 w - -");

// Play a move in standard algebraic notation (SAN).
game.playMove("Ng6#");

// Print the resulting FEN string.
System.out.println(game.getLastPosition().getFen());
```

```java
// Play a random game from the starting position.
Game game = new Game();

while(true)
{
  List<Move> moves = game.getLegalMoves();
  
  if(moves.isEmpty() || game.isThreefoldRepetition())
  {
    break;
  }
  
  game.playMove(moves.get(new Random().nextInt(moves.size())));
}
```

### Currently Implemented

- [x] Board and game representation.
- [x] Magic bitboards.
- [x] Move generation.

### Feature Wishlist

- [ ] Scalable PGN reader/writer.
- [ ] UCI engine interface.
- [ ] Chess game analysis.
- [ ] Chess problem extractor.

### Acknowledgments

- Board representation inspired by [lc0](https://github.com/LeelaChessZero/lc0).
