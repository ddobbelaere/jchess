/*
 * Copyright (C) 2019  Dieter Dobbelaere.
 * All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.ddobbelaere.jchess.chess;

/**
 * <p>
 * Legal chess position that consists of
 * <ul>
 * <li>Board state (how pieces are positioned on the chess board)</li>
 * <li>Color of side to move</li>
 * <li>Castling availability</li>
 * <li>En passant capture possibility</li>
 * <li>Number of halfmoves since the last capture or pawn advance</li>
 * <li>Number of full moves since the start of the game</li>
 * </ul>
 * </p>
 * <p>
 * By <em>legal</em> we mean:
 * <ul>
 * <li>Each side has exactly one king</li>
 * <li>The king of the side that is not to move is not in check</li>
 * <li>Castling availability passes obvious sanity checks (e.g., white cannot
 * castle kingside if there is no rook on h1)</li>
 * <li>En passant information passes obvious sanity checks</li>
 * </ul>
 * </p>
 *
 * @author Dieter Dobbelaere
 */
public class ChessPosition
{
	/**
	 * Chess board corresponding to the position.
	 */
	ChessBoard board;

	/**
	 * Active player still has short castling rights.
	 */
	boolean weCanCastleShort;

	/**
	 * Active player still has long castling rights.
	 */
	boolean weCanCastleLong;

	/**
	 * Opponent still has short castling rights.
	 */
	boolean theyCanCastleShort;

	/**
	 * Opponent still has long castling rights.
	 */
	boolean theyCanCastleLong;

	/**
	 * Square where en passant pawn can be captured. Negative if en passant capture
	 * is not possible.
	 */
	byte enPassantCaptureSquare;

	/**
	 * Number of halfmoves since the last capture or pawn advance.
	 */
	int numNoCaptureOrPawnAdvancePlies;

	/**
	 * Number of full moves since the start of the game.
	 */
	int numGameMoves;

	/**
	 * Starting position.
	 */
	public static final ChessPosition STARTING = fromFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

	/**
	 * Create a legal chess position from a FEN string.
	 *
	 * @param fen Given FEN string.
	 * @return Position corresponding to the given FEN string.
	 * @throws IllegalFenException If the FEN string is invalid or represents an
	 *                             illegal position.
	 */
	public static ChessPosition fromFen(String fen)
	{
		// Split FEN into different parts (assuming whitespace delimiters).
		String[] fenParts = fen.split("\\s+");

		// A FEN string should have at least three parts.
		if (fenParts.length < 3)
		{
			throw new IllegalFenException("FEN string has less than three parts.");
		}

		// Construct returned object.
		ChessPosition position = new ChessPosition();

		// Process piece placement string.
		byte col = 0;
		byte row = 7;
		final String pieceNames = "kqrbnp";

		for (char c : fenParts[0].toCharArray())
		{
			if (c == '/')
			{
				// Next row.
				col = 0;
				row--;
			}
			else if (c >= '1' && c <= '8')
			{
				// Skip empty squares.
				col += c - '0';
			}
			else if (pieceNames.indexOf(Character.toLowerCase(c)) >= 0)
			{
				// Sanity check of square indices.
				if (row < 0 || row > 7 || col < 0 || col > 7)
				{
					throw new IllegalFenException("Invalid piece placement string.");
				}

				long squareBitboard = (1 << (8 * row + col));

				if (Character.isUpperCase(c))
				{
					position.board.ourPieces |= squareBitboard;
				}
				else
				{
					position.board.theirPieces |= squareBitboard;
				}

				switch (Character.toLowerCase(c))
				{
				case 'p':
					position.board.pawns |= squareBitboard;
					break;
				case 'r':
					position.board.rooks |= squareBitboard;
					break;
				case 'b':
					position.board.bishops |= squareBitboard;
					break;
				case 'q':
					position.board.rooks |= squareBitboard;
					position.board.bishops |= squareBitboard;
					break;
				case 'k':
					position.board.kings |= squareBitboard;
					break;
				}

				// Increment column.
				col++;
			}
			else
			{
				throw new IllegalFenException("Invalid piece placement string.");
			}
		}

		// Process castling availability.
		position.weCanCastleShort = fenParts[2].contains("K");
		position.weCanCastleLong = fenParts[2].contains("Q");
		position.theyCanCastleShort = fenParts[2].contains("k");
		position.theyCanCastleLong = fenParts[2].contains("q");

		// Process (optional) en passant square.
		if (fenParts.length >= 4 && fenParts[3].length() == 2)
		{
			int square = 8 * (fenParts[3].charAt(1) - '1') + (fenParts[3].charAt(0) - 'a');

			// Sanity check on square.
			if (square < 0 || square > 63)
			{
				throw new IllegalFenException("Invalid en passant target square.");
			}

			// Store the square.
			position.enPassantCaptureSquare = (byte) square;
		}

		// Process active color.
		if (fenParts[1].equals("b"))
		{
			// Black to move. Mirror position.
			position.mirror();
		}

		return position;
	}

	/**
	 * Mirror the position (change side to move).
	 */
	void mirror()
	{
		// Mirror the chess board.
		board.mirror();

		// Swap castling availability.
		boolean temp = weCanCastleShort;
		weCanCastleShort = theyCanCastleShort;
		theyCanCastleShort = temp;

		temp = weCanCastleLong;
		weCanCastleLong = theyCanCastleLong;
		theyCanCastleLong = temp;

		// Mirror en passant square.
		enPassantCaptureSquare = (byte) (((enPassantCaptureSquare & 0b111) << 3) + (enPassantCaptureSquare >> 3));
	}

	/**
	 * @return String representation of the chess position.
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();

		// Chess board representation.
		sb.append(board);
		// sb.append('\n');

		// TODO: castling availability, en passant information, move numbers.
		return sb.toString();
	}
}
