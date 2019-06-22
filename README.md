# JChess

[![Build Status](https://travis-ci.org/ddobbelaere/jchess.svg?branch=master)](https://travis-ci.org/ddobbelaere/jchess)
[![Coverage Status](https://coveralls.io/repos/github/ddobbelaere/jchess/badge.svg?branch=master)](https://coveralls.io/github/ddobbelaere/jchess?branch=master)
[![Jitpack](https://jitpack.io/v/ddobbelaere/jchess.svg)](https://jitpack.io/#ddobbelaere/jchess)

JChess is a pure Java chess library.

### API

The public API of the latest release is [available here](https://javadoc.jitpack.io/com/github/ddobbelaere/jchess/latest/javadoc/).

### Examples

```java
// Play a random game from the starting position.
Position position = Position.STARTING;

while(true)
{
  List<Move> moves = position.getLegalMoves();
  
  if(moves.isEmpty())
  {
    if(position.isCheckMate())
    {
      System.out.println("Checkmate!");
    }
    else
    {
      System.out.println("Stalemate!");
    }
    
    break;
  }
  
  position = position.applyMove(moves.get(new Random().nextInt(moves.size())));
}
```

### Currently Implemented

- [x] Board representation.
- [x] Magic bitboards.
- [x] Move generation.

### Feature Wishlist

- [ ] Scalable PGN reader/writer.
- [ ] UCI engine interface.
- [ ] Chess game analysis.
- [ ] Chess problem extractor.

### Acknowledgments

- Board representation inspired by [lc0](https://github.com/LeelaChessZero/lc0).
