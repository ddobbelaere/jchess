# JChess

[![Build Status](https://travis-ci.org/ddobbelaere/jchess.svg?branch=master)](https://travis-ci.org/ddobbelaere/jchess)
[![Coverage Status](https://coveralls.io/repos/github/ddobbelaere/jchess/badge.svg?branch=master)](https://coveralls.io/github/ddobbelaere/jchess?branch=master)
[![Jitpack](https://jitpack.io/v/ddobbelaere/jchess.svg)](https://jitpack.io/#ddobbelaere/jchess)

JChess is a pure Java chess library.

### API

The public API of the latest release is [available here](https://javadoc.jitpack.io/com/github/ddobbelaere/jchess/latest/javadoc/).

### Examples

```java
/* EXAMPLE 1 */
// Play a game from a given position (as FEN string).
Game game = new Game("8/8/8/8/8/p7/2K1N3/k7 w - -");

// Play some moves.
game.playMoves("Nc1", "a2", "Nb3#");

// Print the resulting FEN string.
System.out.println(game.getCurrentPosition().getFen());
// Prints "8/8/8/8/8/1N6/p1K5/k b - - 1 1".
```

```java
/* EXAMPLE 2 */
// Play a random game from the starting position.
Game game = new Game();

while(true)
{
  List<Move> moves = game.getLegalMoves();
  
  if(moves.isEmpty() || game.isThreefoldRepetition())
  {
    break;
  }
  
  game.playMoves(moves.get(new Random().nextInt(moves.size())));
}
```

### Currently Implemented

- [x] Board and game representation.
- [x] Magic bitboards.
- [x] Move generation.
- [x] Moves in standard algebraic notation (SAN).

### Feature Wishlist

- [ ] Scalable PGN reader/writer.
- [ ] UCI engine interface.
- [ ] Chess game analysis.
- [ ] Chess problem extractor.

### Installation

All release versions and latest snapshots are hosted by [JitPack](https://jitpack.io/#ddobbelaere/jchess).

### Acknowledgments

- Board representation inspired by [lc0](https://github.com/LeelaChessZero/lc0).
