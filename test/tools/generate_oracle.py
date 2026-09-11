"""Regenerate deterministic, offline fixtures with chess==1.11.2.
Run: python -m pip install chess==1.11.2; python test/tools/generate_oracle.py
The game and its regular tests do not require Python or this package.
"""
from pathlib import Path
import random
import chess

root = Path(__file__).resolve().parents[1] / 'fixtures'
rng = random.Random(1208)

def record(board):
    moves = ','.join(sorted(m.uci() for m in board.legal_moves))
    flags = ','.join(str(int(v)) for v in (board.is_check(), board.is_checkmate(),
        board.is_stalemate(), board.is_insufficient_material()))
    return '\t'.join((board.fen(en_passant='fen'), moves, flags))

positions = {}
# Exercise every piece/color/square, with occupied rays and captures as well as empty boards.
for color in chess.COLORS:
    for kind in range(chess.PAWN, chess.KING + 1):
        for square in chess.SQUARES:
            for crowded in (False, True):
                b = chess.Board(None)
                b.turn = color
                b.set_piece_at(chess.A1, chess.Piece(chess.KING, chess.WHITE))
                b.set_piece_at(chess.H8, chess.Piece(chess.KING, chess.BLACK))
                if kind == chess.KING:
                    b.remove_piece_at(chess.A1 if color else chess.H8)
                elif square in (chess.A1, chess.H8):
                    continue
                b.set_piece_at(square, chess.Piece(kind, color))
                if crowded:
                    for s in rng.sample(list(chess.SQUARES), 10):
                        if b.piece_at(s) is None:
                            b.set_piece_at(s, chess.Piece(rng.choice([1, 2, 3, 4, 5]), rng.choice(chess.COLORS)))
                if b.is_valid(): positions[b.fen()] = record(b)
# Replay games independently to test transition state, not just static move generation.
sequences = []
for game in range(50):
    b = chess.Board()
    sequences.append(f'{game}\t-\t{record(b)}')
    for ply in range(160):
        if b.is_game_over(): break
        available = list(b.legal_moves)
        captures = [m for m in available if b.is_capture(m)]
        m = rng.choice(captures if captures and rng.random() < .4 else available)
        b.push(m)
        sequences.append(f'{game}\t{m.uci()}\t{record(b)}')
        if ply % 3 == 0: positions[b.fen()] = record(b)
root.joinpath('positions.tsv').write_text('\n'.join(positions.values()) + '\n')
root.joinpath('games.tsv').write_text('\n'.join(sequences) + '\n')
print(f'Generated {len(positions)} positions and {len(sequences)} game states using chess {chess.__version__}.')
