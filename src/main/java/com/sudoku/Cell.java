package com.sudoku;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Cell {

    private int value;                // The actual number in the cell
    private boolean fixed;            // True if this is a given cell
    private boolean certain;           // True if a certain has been used
    private boolean guessed;          // True if user is guessing
    private boolean wrong;            // True if the value is incorrect
    private Set<Integer> candidates;  // Candidate numbers (notes)

    public Cell(int value, boolean fixed) {
        this.value = value;
        this.fixed = fixed;
        this.candidates = new HashSet<>();
        this.certain = false;
        this.guessed = false;
        this.wrong = false;
    }

    // --- Value ---
    public int getValue() { return value; }
    public void setValue(int value) {
        this.value = value;
        if (value != 0) candidates.clear(); // Clear notes when a number is set
    }

    // --- Fixed ---
    public boolean isFixed() { return fixed; }
    public void setFixed(boolean fixed) { this.fixed = fixed; }

    // --- Certain ---
    public boolean isCertain() { return certain; }
    public void setCertain(boolean certain) { this.certain = certain; }

    // --- Guessed ---
    public boolean isGuessed() { return guessed; }
    public void setGuessed(boolean guessed) { this.guessed = guessed; }

    // --- Wrong ---
    public boolean isWrong() { return wrong; }
    public void setWrong(boolean wrong) { this.wrong = wrong; }

    // --- Candidates / Notes ---
    public Set<Integer> getCandidates() { return candidates; }
    public int getCandidate(int n){
        List<Integer> list = new ArrayList<>(candidates);
        return list.get(n);  // Access by index}
    }

    public void addCandidate(int n) { if (n >= 1 && n <= 9) candidates.add(n); }
    public void removeCandidate(int n) { candidates.remove(n); }
    public void clearCandidates() { candidates.clear(); }

    // --- Helper ---
    public boolean hasCandidates() { return !candidates.isEmpty(); }
}
