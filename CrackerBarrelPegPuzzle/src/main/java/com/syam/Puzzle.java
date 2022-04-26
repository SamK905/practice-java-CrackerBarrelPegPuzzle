package com.syam;


/**
 *
 * @author Syam
 */
import java.util.*;
//we define the moves ie mfrom, m_over and to
class Move
{
    public int mfrom;
    public int m_over;
    public int to;

    public Move(int mfrom, int m_over, int to)
    {
        this.mfrom = mfrom;
        this.m_over = m_over;
        this.to   = to;
    }
    //define a method for the moves in reverse order
    public Move in_reverse()
    { return new Move(to, m_over, mfrom); }

    @Override
    public String toString()
    {
        return "(" + mfrom + ", " + m_over + ", " + to + ")";
    }
}

class On_Board
{
    public int pegCount;
    public int[] cells;

    public On_Board(int emptyCell)
    {
        cells = new int[15];
        pegCount = 14;
        for (int i = 0; i < 15; i++)
            cells[i] = i == emptyCell ? 0 : 1;
        //return 0 for empty cells and 1 for full cells
    }

    public On_Board(int pegCount, int[] cells)
    {
        this.pegCount = pegCount;
        this.cells    = cells.clone();
    }

    public On_Board move(Move mv)
    {
        if (cells[mv.mfrom] == 1 &&
                cells[mv.m_over] == 1 &&
                cells[mv.to]   == 0)
        {
            On_Board boardAfterMove = new On_Board(pegCount-1, cells.clone());
            boardAfterMove.cells[mv.mfrom] = 0;//moved away mfrom
            boardAfterMove.cells[mv.m_over] = 0;//removing jumped m_over
            boardAfterMove.cells[mv.to]   = 1;//the landing cell after a jump

            return boardAfterMove;
        }

        return null;
    }
}

class TheIterator implements Iterator<Move>
{
    private Move[] moves;
    private Move   in_reverse;
    private int    i;

    public TheIterator(Move[] moves)
    {
        this.moves = moves;
        this.i     = 0;
    }

    @Override
    public boolean hasNext()
    { return i < moves.length || (i == moves.length && in_reverse != null); }

    @Override
    public Move next()
    {
        if (in_reverse != null)
        {
            Move result = in_reverse;
            in_reverse = null;
            return result;
        }

        Move mv = moves[i++];
        in_reverse = mv.in_reverse();

        return mv;
    }
}

class MyList implements Iterable<Move>
{
    public static final Move[] moves =
            {
                    //Defining the moves that will be iterating
                    new Move(0, 1, 3),
                    new Move(0, 2, 5),
                    new Move(1, 3, 6),
                    new Move(1, 4, 8),
                    new Move(2, 4, 7),
                    new Move(2, 5, 9),
                    new Move(3, 6, 10),
                    new Move(3, 7, 12),
                    new Move(4, 7, 11),
                    new Move(4, 8, 13),
                    new Move(5, 8, 12),
                    new Move(5, 9, 14),
                    new Move(3, 4, 5),
                    new Move(6, 7, 8),
                    new Move(7, 8, 9),
                    new Move(10, 11, 12),
                    new Move(11, 12, 13),
                    new Move(12, 13, 14)
            };

    @Override
    public TheIterator iterator()
    { return new TheIterator(moves); }
}

public class Puzzle
{
    static MyList steps()
    { return new MyList(); }

    static ArrayList<LinkedList<Move>> solve(On_Board b)
    {
        ArrayList<LinkedList<Move>> out = new ArrayList<LinkedList<Move>>();
        solve(b, out, 0);

        return out;
    }

    static LinkedList<Move> firstSolution(On_Board b)
    {
        ArrayList<LinkedList<Move>> out = new ArrayList<LinkedList<Move>>();
        solve(b, out, 1);

        if (out.size() == 0) // sanity
            return null;

        return out.get(0);
    }

    static void solve(On_Board b, ArrayList<LinkedList<Move>> solutions, int count)
    {
        if (b.pegCount == 1)
        {
            solutions.add(new LinkedList<Move>());
            return;
        }

        for (Move mv : steps())
        {
            On_Board boardAfterMove = b.move(mv);
            if (boardAfterMove == null) continue;

            ArrayList<LinkedList<Move>> tailSolutions = new ArrayList<LinkedList<Move>>();
            solve(boardAfterMove, tailSolutions, count);

            for (LinkedList<Move> solution : tailSolutions)
            {
                solution.add(0, mv);
                solutions.add(solution);

                if (solutions.size() == count)
                    return;
            }
        }
    }

    static void printOn_Board(On_Board b)
    {
        System.out.print("(" + b.pegCount + ", [");
        for (int i = 0; i < b.cells.length; i++)
            System.out.print(i < b.cells.length-1 ? b.cells[i] + ", " : b.cells[i] + "])");
        System.out.println();
    }

    static void show(On_Board b)
    {
        int[][] lines = { {4,0,0}, {3,1,2}, {2,3,5}, {1,6,9}, {0,10,14} };
        for (int[] l : lines)
        {
            int spaces = l[0];
            int begin  = l[1];
            int end    = l[2];

            String space = new String();
            for (int i = 0; i < spaces; i++)
                space += " ";

            System.out.print(space);
            for (int i = begin; i <= end; i++)
                System.out.print(b.cells[i] == 0 ? ". " : "x ");

            System.out.println();
        }

        System.out.println();
    }

    static void replay(List<Move> moves, On_Board b)
    {
        show(b);
        for (Move mv : moves)
        {
            b = b.move(mv);
            show(b);
        }
    }

    static void terse()
    {
        for (int i = 0; i < 15; i++)
        {
            On_Board b = new On_Board(i);
            printOn_Board(b);
            List<Move> moves = firstSolution(b);
            for (Move mv : moves)
            {
                System.out.println(mv);
                b = b.move(mv);
            }
            //output the board
            printOn_Board(b);
            //output the terse by the code 'terse();' if needed
            System.out.println();
        }
    }

    static void go()
    {
        for (int i = 1; i < 10; i++)
        {
            //Print the board header with i as the itteration step
            System.out.println("=== " + i + " ===");
            On_Board b = new On_Board(i);
            replay(firstSolution(b), b);
            System.out.println();
        }
    }

    public static void main(String[] args)
    {
        go();
    }
}
