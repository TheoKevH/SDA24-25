// Name: Theodore Kevin Himawan
// NPM: 2306210973
// TA Code: ALM
/* References: 
1.https://rosettacode.org/wiki/Visualize_a_tree#Java (for visualizing tree)
*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.*;

public class TP2 {
    // Initialize variables that will be used in the program
    private static InputReader in;
    private static PrintWriter out;
    static HashMap<Integer, Participant> participantsMap = new HashMap<>();
    static HashMap<Integer, Team> teamsMap = new HashMap<>();
    static int participantId = 1;
    static int teamIdCounter = 1;
    static Team headTeam = null;
    static Team tailTeam = null;
    static Team jokiTeam = null;
    static Team sofitaTeam = null;

    public static void main(String[] args) {
        InputStream inputStream = System.in;
        in = new InputReader(inputStream);
        OutputStream outputStream = System.out;
        out = new PrintWriter(outputStream);

        // Call a method to process the inputs and initialize the variables
        processAndInitialize();

        // Set initial positions of Joki and Sofita
        sofitaTeam = headTeam;
        jokiTeam = getLowestTeamExcludingSofita(sofitaTeam);

        // To read and do the queries (A, B, M, T, G, V, E, U, R, J)
        int Q = in.nextInteger(); // Number of queries
        for (int i = 0; i < Q; i++) {
            String command = in.next();
            switch(command){
                case "A":
                    int participant = in.nextInteger();
                    A(participant);
                    break;
                case "B":
                    String extremeBound = in.next();
                    B(extremeBound);
                    break;
                case "M":
                    String direction = in.next();
                    M(direction);
                    break;
                case "T":
                    int pengirim = in.nextInteger();
                    int penerima = in.nextInteger();
                    int points = in.nextInteger();
                    T(pengirim, penerima, points);
                    break;
                case "G":
                    String newTeam = in.next();
                    G(newTeam);
                    break;
                case "V":
                    int peserta1 = in.nextInteger();
                    int peserta2 = in.nextInteger();
                    int teamId = in.nextInteger();
                    int hasil = in.nextInteger();
                    V(peserta1, peserta2, teamId, hasil);
                    break;
                case "E":
                    int pointToEliminate = in.nextInteger();
                    E(pointToEliminate);
                    break;
                case "U":
                    U();
                    break;
                case "R":
                    R();
                    break;
                case "J":
                    String jokiDirection = in.next();
                    J(jokiDirection);
                    break;
                default:
                    out.println("Input tidak valid!");
            }
        }

        out.flush();
    }

    static void processAndInitialize() {
        // For inputs (baca input doang sih...)
        int M = in.nextInteger(); // Number of teams
        int[] Mi = new int[M];    // Number of participants in each team
        for (int i = 0; i < M; i++) {
            Mi[i] = in.nextInteger();
        }

        int totalParticipants = 0;
        for (int i = 0; i < M; i++) {
            totalParticipants += Mi[i];
        }

        int[] Pj = new int[totalParticipants]; // Points of participants
        for (int i = 0; i < totalParticipants; i++) {
            Pj[i] = in.nextInteger();
        }

        // Initialize teams and participants
        int participantIndex = 0;
        for (int i = 0; i < M; i++) {
            int newTeamId = teamIdCounter++;
            Team team = new Team(newTeamId);
            for (int j = 0; j < Mi[i]; j++) {
                Participant participant = new Participant(participantId++, Pj[participantIndex++], team);
                team.addParticipant(participant);
                participantsMap.put(participant.id, participant);
            }
            addTeam(team);
            teamsMap.put(team.id, team);
        }
    }

    // Method for query A (add participants to a team)
    static void A(int jumlahPeserta) {
        if (sofitaTeam == null) {
            out.println(-1);
            return;
        }
        for (int i = 0; i < jumlahPeserta; i++) {
            Participant participant = new Participant(participantId++, 3, sofitaTeam);
            sofitaTeam.addParticipant(participant);
            participantsMap.put(participant.id, participant);
        }
        out.println(sofitaTeam.numParticipants);
    }

    // Method for query B (perhitungan statprob gitu...)
    static void B(String bound) {
        if (sofitaTeam == null) {
            out.println(0);
            return;
        }
        int count = sofitaTeam.getExtremeCount(bound);
        out.println(count);
    }

    // Method for query M (Sofita moves Left or Right)
    static void M(String direction) {
        if (sofitaTeam == null) {
            out.println(-1);
            return;
        }

        Team nextTeam;
        if (direction.equals("L")) {
            nextTeam = sofitaTeam.prev;
        } else {
            nextTeam = sofitaTeam.next;
        }

        while (nextTeam != sofitaTeam && (!nextTeam.isValid() || nextTeam.isEliminated)) {
            nextTeam = direction.equals("L") ? nextTeam.prev : nextTeam.next;
        }

        if (nextTeam.isValid() && !nextTeam.isEliminated) {
            sofitaTeam = nextTeam;
            checkForJoki();
            out.println(sofitaTeam.id);
        } else {
            out.println(-1);
        }

    }

    // Method for query T (Sending points)
    static void T(int senderId, int receiverId, int jumlahPoin) {
        if (sofitaTeam == null) {
            out.println(-1);
            return;
        }
        Participant sender = participantsMap.get(senderId);
        Participant receiver = participantsMap.get(receiverId);
        if (sender == null || receiver == null || sender.team != sofitaTeam || receiver.team != sofitaTeam) {
            out.println(-1);
            return;
        }
        if (jumlahPoin >= sender.points) {
            out.println(-1);
            return;
        }
        // Update points
        sofitaTeam.updateParticipantPoints(sender, sender.points - jumlahPoin);
        sofitaTeam.updateParticipantPoints(receiver, receiver.points + jumlahPoin);
        out.println(sender.points + " " + receiver.points);
    }

    // Method for query G (New team created either Left or Right)
    static void G(String direction) {
        int newTeamId = teamIdCounter++;
        Team newTeam = new Team(newTeamId);
        for (int i = 0; i < 7; i++) {
            Participant participant = new Participant(participantId++, 1, newTeam);
            newTeam.addParticipant(participant);
            participantsMap.put(participant.id, participant);
        }
        // Insert new team
        if (sofitaTeam == null) {
            addTeam(newTeam);
            sofitaTeam = newTeam;
        } else if (direction.equals("L")) {
            // Insert to the left of sofitaTeam
            Team prevTeam = sofitaTeam.prev;
            prevTeam.next = newTeam;
            newTeam.prev = prevTeam;
            newTeam.next = sofitaTeam;
            sofitaTeam.prev = newTeam;
            if (sofitaTeam == headTeam) {
                headTeam = newTeam;
            }
        } else {
            // Insert to the right of sofitaTeam
            Team nextTeam = sofitaTeam.next;
            sofitaTeam.next = newTeam;
            newTeam.prev = sofitaTeam;
            newTeam.next = nextTeam;
            nextTeam.prev = newTeam;
            if (sofitaTeam == tailTeam) {
                tailTeam = newTeam;
            }
        }
        teamsMap.put(newTeam.id, newTeam);
        out.println(newTeam.id);
    }

    // Method for query V (Peserta bertanding)
    static void V(int participant1Id, int participant2Id, int teamId, int hasil) {
        if (sofitaTeam == null) {
            out.println(-1);
            return;
        }

        Participant participant1 = participantsMap.get(participant1Id);
        Participant participant2 = participantsMap.get(participant2Id);
        Team opponentTeam = teamsMap.get(teamId);

        if (participant1 == null || participant2 == null || opponentTeam == null) {
            out.println(-1);
            return;
        }

        if (participant1.team != sofitaTeam || participant2.team != opponentTeam) {
            out.println(-1);
            return;
        }
        // Update matches
        participant1.matches++;
        participant2.matches++;
        if (hasil == 0) {
            // Draw
            sofitaTeam.updateParticipantPoints(participant1, participant1.points + 1);
            opponentTeam.updateParticipantPoints(participant2, participant2.points + 1);
            out.println(participant1.points + " " + participant2.points);
        } else if (hasil == 1) {
            // Participant 1 wins
            sofitaTeam.updateParticipantPoints(participant1, participant1.points + 3);
            opponentTeam.updateParticipantPoints(participant2, participant2.points - 3);
            out.println(participant1.points);
        } else {
            // Participant 2 wins
            opponentTeam.updateParticipantPoints(participant2, participant2.points + 3);
            sofitaTeam.updateParticipantPoints(participant1, participant1.points - 3);
            out.println(participant2.points);
        }
        // Check for eliminations
        checkParticipantElimination(participant1);
        checkParticipantElimination(participant2);
    }

    // Method for query E (Eliminate teams)
    static void E(int poin) {
        int eliminatedTeams = 0;
        List<Team> teamsToEliminate = new ArrayList<>();
        Team current = headTeam;
        if (current == null) {
            out.println(0);
            return;
        }
        do {
            if (current.totalPoints < poin) {
                teamsToEliminate.add(current);
            }
            current = current.next;
        } while (current != headTeam);
        for (Team team : teamsToEliminate) {
            eliminateTeam(team);
            eliminatedTeams++;
        }
        out.println(eliminatedTeams);
    }

    // Method for query U (Unique points)
    static void U() {

        if (sofitaTeam == null) {
            out.println(-1);
            return;
        }
        int uniquePoints = sofitaTeam.pointsTree.countUnique();
        out.println(uniquePoints);
    }

    // Method for query R (Pengurutan ulang)
    static void R() {
        // Collect teams into a list
        List<Team> teamList = new ArrayList<>();
        Team current = headTeam;
        if (current == null) {
            out.println(-1);
            return;
        }
        do {
            teamList.add(current);
            current = current.next;
        } while (current != headTeam);
        // Sort teams using custom merge sort
        Team[] teamsArray = teamList.toArray(new Team[teamList.size()]);
        mergeSortTeams(teamsArray, 0, teamsArray.length - 1);
        // Rebuild linked list
        headTeam = teamsArray[0];
        tailTeam = teamsArray[teamsArray.length - 1];
        for (int i = 0; i < teamsArray.length; i++) {
            Team team = teamsArray[i];
            if (i == 0) {
                team.prev = teamsArray[teamsArray.length - 1];
            } else {
                team.prev = teamsArray[i - 1];
            }
            if (i == teamsArray.length - 1) {
                team.next = teamsArray[0];
            } else {
                team.next = teamsArray[i + 1];
            }
        }
        // Update positions
        sofitaTeam = headTeam;
        // Check for Joki and Sofita in the same team
        checkForJoki();
        out.println(sofitaTeam.id);
    }

    // Method for query J (Joki move Left or Right)
    static void J(String direction) {
        if (jokiTeam == null) {
            out.println(-1);
            return;
        }
        Team targetTeam;
        if (direction.equals("L")) {
            targetTeam = jokiTeam.prev;
        } else {
            targetTeam = jokiTeam.next;
        }

        // Find next valid team
        while (targetTeam != jokiTeam && (!targetTeam.isValid() || targetTeam.isEliminated)) {
            targetTeam = direction.equals("L") ? targetTeam.prev : targetTeam.next;
        }

        if (targetTeam != sofitaTeam && targetTeam.isValid() && !targetTeam.isEliminated) {
            jokiTeam = targetTeam;
            checkForJoki();
        }

        out.println(jokiTeam.id);
    }

    // Adds a team to the linked list
    static void addTeam(Team team) {
        if (headTeam == null) {
            headTeam = team;
            tailTeam = team;
            team.prev = team;
            team.next = team;
        } else {
            tailTeam.next = team;
            team.prev = tailTeam;
            team.next = headTeam;
            headTeam.prev = team;
            tailTeam = team;
        }
    }


    // Get team with lowest total points, excluding sofita's team
    static Team getLowestTeamExcludingSofita(Team excludeTeam) {
        Team current = headTeam;
        if (current == null) {
            return null;
        }
        Team minTeam = null;
        int minPoints = Integer.MAX_VALUE;
        do {
            if (current != excludeTeam && current.totalPoints < minPoints) {
                minPoints = current.totalPoints;
                minTeam = current;
            }
            current = current.next;
        } while (current != headTeam);
        return minTeam;
    }

    // Check if participant should be eliminated
    static void checkParticipantElimination(Participant participant) {
        if (participant.points <= 0) {
            participant.team.removeParticipant(participant);
            participantsMap.remove(participant.id);
            // Check if team should be eliminated
            if (participant.team.numParticipants < 7) {
                eliminateTeam(participant.team);
            }
        }
    }

    // Method to eliminate a team
    static void eliminateTeam(Team team) {

        if (team == null || team.isEliminated) {
            return;
        }

        team.isEliminated = true;

        // Update linked list pointers
        if (team.next == team) {
            // Last team in the list
            headTeam = null;
            tailTeam = null;
        } else {
            team.prev.next = team.next;
            team.next.prev = team.prev;
            if (team == headTeam) {
                headTeam = team.next;
            }
            if (team == tailTeam) {
                tailTeam = team.prev;
            }
        }

        // Remove from maps
        teamsMap.remove(team.id);
        for (Participant p : team.participants) {
            participantsMap.remove(p.id);
        }

        // Update Sofita's position if necessary
        if (sofitaTeam == team) {
            Team newSofitaTeam = getTeamWithHighestPoints();
            if (newSofitaTeam != null && !newSofitaTeam.isEliminated) {
                sofitaTeam = newSofitaTeam;
            } else {
                sofitaTeam = null;
            }
        }

        // Update Joki's position if necessary
        if (jokiTeam == team) {
            jokiTeam = getLowestTeamExcludingSofita(sofitaTeam);
            if (jokiTeam == null || !jokiTeam.isValid()) {
                jokiTeam = null;
            }
        }
    }

    // Get team with highest total points
    static Team getTeamWithHighestPoints() {
        Team current = headTeam;
        if (current == null) {
            return null;
        }
        Team maxTeam = null;
        int maxPoints = -1;
        do {
            if (current.totalPoints > maxPoints) {
                maxPoints = current.totalPoints;
                maxTeam = current;
            }
            current = current.next;
        } while (current != headTeam);
        return maxTeam;
    }

    // Merge sort for teams
    static void mergeSortTeams(Team[] arr, int l, int r) {
        if (l < r) {
            int m = (l + r) / 2;
            mergeSortTeams(arr, l, m);
            mergeSortTeams(arr, m + 1, r);
            mergeTeams(arr, l, m, r);
        }
    }

    static void mergeTeams(Team[] arr, int l, int m, int r) {
        int n1 = m - l + 1;
        int n2 = r - m;

        Team[] L = new Team[n1];
        Team[] R = new Team[n2];

        for (int i = 0; i < n1; i++)
            L[i] = arr[l + i];
        for (int j = 0; j < n2; j++)
            R[j] = arr[m + 1 + j];

        int i = 0, j = 0, k = l;

        while (i < n1 && j < n2) {
            if (compareTeams(L[i], R[j]) <= 0) {
                arr[k++] = R[j++];
            } else {
                arr[k++] = L[i++];
            }
        }

        while (i < n1) {
            arr[k++] = L[i++];
        }

        while (j < n2) {
            arr[k++] = R[j++];
        }
    }

    // Comparison function for teams
    static int compareTeams(Team a, Team b) {
        if (a.totalPoints != b.totalPoints) {
            return Integer.compare(a.totalPoints, b.totalPoints);
        }
        if (a.numParticipants != b.numParticipants) {
            return Integer.compare(b.numParticipants, a.numParticipants);
        }
        return Integer.compare(b.id, a.id);
    }

    // Check if Joki and Sofita are in the same team
    static void checkForJoki() {
        if (jokiTeam == null || sofitaTeam == null || !jokiTeam.isValid() || !sofitaTeam.isValid()) {
            return;
        }

        if (jokiTeam == sofitaTeam) {
            Team team = sofitaTeam;
            team.jokiCaughtTimes++;

            if (team.jokiCaughtTimes == 1) {
                // Remove top 3 participants
                List<Participant> topParticipants = team.getTopParticipants(3);
                for (Participant p : topParticipants) {
                    team.removeParticipant(p);
                    participantsMap.remove(p.id);
                }

                // Check if team should be eliminated
                if (team.numParticipants < 7) {
                    Team nextValidTeam = getTeamWithHighestPoints();
                    while (nextValidTeam != null && !nextValidTeam.isValid()) {
                        nextValidTeam = getNextHighestPoints(nextValidTeam);
                    }
                    sofitaTeam = nextValidTeam;
                    eliminateTeam(team);
                }
            } else if (team.jokiCaughtTimes == 2) {
                team.resetParticipantsPoints();
            } else if (team.jokiCaughtTimes == 3) {
                Team nextValidTeam = getTeamWithHighestPoints();
                while (nextValidTeam != null && !nextValidTeam.isValid()) {
                    nextValidTeam = getNextHighestPoints(nextValidTeam);
                }
                sofitaTeam = nextValidTeam;
                eliminateTeam(team);
            }

            // Move Joki to next valid team with lowest points
            Team newJokiTeam = getLowestTeamExcludingSofita(sofitaTeam);
            while (newJokiTeam != null && !newJokiTeam.isValid()) {
                newJokiTeam = getNextLowestPoints(newJokiTeam);
            }
            jokiTeam = newJokiTeam;
        }
    }

    // Get team with lowest total points
    static Team getTeamWithLowestPoints() {
        Team current = headTeam;
        if (current == null) {
            return null;
        }
        Team minTeam = null;
        int minPoints = Integer.MAX_VALUE;
        do {
            if (current.totalPoints < minPoints) {
                minPoints = current.totalPoints;
                minTeam = current;
            }
            current = current.next;
        } while (current != headTeam);
        return minTeam;
    }

    static Team getNextHighestPoints(Team currentTeam) {
        Team current = headTeam;
        if (current == null) return null;

        Team maxTeam = null;
        int maxPoints = -1;
        do {
            if (!current.isEliminated && current != currentTeam && current.totalPoints > maxPoints) {
                maxPoints = current.totalPoints;
                maxTeam = current;
            }
            current = current.next;
        } while (current != headTeam);
        return maxTeam;
    }

    static Team getNextLowestPoints(Team currentTeam) {
        Team current = headTeam;
        if (current == null) return null;

        Team minTeam = null;
        int minPoints = Integer.MAX_VALUE;
        do {
            if (!current.isEliminated && current != currentTeam && current.totalPoints < minPoints) {
                minPoints = current.totalPoints;
                minTeam = current;
            }
            current = current.next;
        } while (current != headTeam);
        return minTeam;
    }

    /*// Get team with lowest total points
    static Team getTeamWithLowestPoints() {
        Team current = headTeam;
        if (current == null) {
            return null;
        }
        Team minTeam = null;
        int minPoints = Integer.MAX_VALUE;
        do {
            if (current.totalPoints < minPoints) {
                minPoints = current.totalPoints;
                minTeam = current;
            }
            current = current.next;
        } while (current != headTeam);
        return minTeam;
    } */

    // Participant class
    static class Participant {
        int id;
        int points;
        int matches;
        Team team;

        public Participant(int id, int points, Team team) {
            this.id = id;
            this.points = points;
            this.matches = 0;
            this.team = team;
        }
    }

    // Team class
    static class Team {
        int id;
        int totalPoints;
        int numParticipants;
        int jokiCaughtTimes;
        List<Participant> participants;
        AVLTree pointsTree;
        boolean isEliminated = false;

        Team prev;
        Team next;

        public Team(int id) {
            this.id = id;
            this.totalPoints = 0;
            this.numParticipants = 0;
            this.jokiCaughtTimes = 0;
            this.participants = new ArrayList<>();
            this.pointsTree = new AVLTree();
        }

        // Helper methods for the team class
        void addParticipant(Participant participant) {
            participants.add(participant);
            numParticipants++;
            totalPoints += participant.points;
            pointsTree.insert(participant.points);
        }

        void removeParticipant(Participant participant) {
            participants.remove(participant);
            numParticipants--;
            totalPoints -= participant.points;
            pointsTree.delete(participant.points);
        }

        void updateParticipantPoints(Participant participant, int newPoints) {
            pointsTree.delete(participant.points);
            totalPoints -= participant.points;
            participant.points = newPoints;
            totalPoints += participant.points;
            pointsTree.insert(participant.points);
        }

        // Reset the participants' points when sofita meets with joki for the 2nd time
        void resetParticipantsPoints() {
            pointsTree = new AVLTree();
            totalPoints = 0;
            for (Participant p : participants) {
                p.points = 1;
                totalPoints += p.points;
                pointsTree.insert(p.points);
            }
        }

        List<Participant> getTopParticipants(int n) {
            // Sort participants based on hierarchy
            List<Participant> sortedParticipants = new ArrayList<>(participants);
            sortParticipants(sortedParticipants);
            List<Participant> result = new ArrayList<>();
            for (int i = 0; i < Math.min(n, sortedParticipants.size()); i++) {
                result.add(sortedParticipants.get(i));
            }
            return result;
        }

        // Sorting for participants
        void sortParticipants(List<Participant> participants) {
            int n = participants.size();
            for (int i = 0; i < n - 1; i++) {
                int maxIdx = i;
                for (int j = i + 1; j < n; j++) {
                    if (compareParticipants(participants.get(j), participants.get(maxIdx)) < 0) {
                        maxIdx = j;
                    }
                }
                // Swap
                Participant temp = participants.get(maxIdx);
                participants.set(maxIdx, participants.get(i));
                participants.set(i, temp);
            }
        }

        int compareParticipants(Participant a, Participant b) {
            if (b.points != a.points) {
                return b.points - a.points;
            }
            if (a.matches != b.matches) {
                return a.matches - b.matches;
            }
            return a.id - b.id;
        }

        // To count extreme bound according to the TP 2 document (statprob(?))
        int getExtremeCount(String boundType) {
            int K = numParticipants;
            if (K == 0) {
                return 0;
            }
            int IndexQ1 = Math.max(0, (int) Math.floor(1.0 / 4 * (K - 1)));
            int IndexQ3 = Math.min(K - 1, (int) Math.floor(3.0 / 4 * (K - 1)));

            int Q1 = pointsTree.getKthSmallest(IndexQ1 + 1); // 1-based index
            int Q3 = pointsTree.getKthSmallest(IndexQ3 + 1);

            int IQR = Q3 - Q1;
            int L = Q1 - (int) Math.floor(1.5 * IQR);
            int U = Q3 + (int) Math.floor(1.5 * IQR);

            if (boundType.equals("U")) {
                return pointsTree.countGreaterThan(U);
            } else {
                return pointsTree.countLessThan(L);
            }
        }

        boolean isValid() {
            return !isEliminated && numParticipants >= 7;
        }
    }

    // AVL Tree class
    static class AVLTree {
        AVLNode root;

        void insert(int key) {
            root = insert(root, key);
        }

        void delete(int key) {
            root = delete(root, key);
        }

        int getKthSmallest(int k) {
            return getKthSmallest(root, k);
        }

        int countLessThan(int key) {
            return countLessThan(root, key);
        }

        int countGreaterThan(int key) {
            return countGreaterThan(root, key);
        }

        int countUnique() {
            return countUnique(root);
        }

        AVLNode insert(AVLNode node, int key) {
            if (node == null) {
                return new AVLNode(key);
            }
            if (key == node.key) {
                node.count++;
            } else if (key < node.key) {
                node.left = insert(node.left, key);
            } else {
                node.right = insert(node.right, key);
            }
            update(node);
            return balance(node);
        }

        AVLNode delete(AVLNode node, int key) {
            if (node == null) {
                return null;
            }
            if (key == node.key) {
                if (node.count > 1) {
                    node.count--;
                } else {
                    if (node.left == null) {
                        return node.right;
                    }
                    if (node.right == null) {
                        return node.left;
                    }
                    AVLNode temp = node;
                    node = getMin(temp.right);
                    node.right = deleteMin(temp.right);
                    node.left = temp.left;
                }
            } else if (key < node.key) {
                node.left = delete(node.left, key);
            } else {
                node.right = delete(node.right, key);
            }
            update(node);
            return balance(node);
        }

        int getKthSmallest(AVLNode node, int k) {
            if (node == null) {
                return -1;
            }
            int leftSize = (node.left != null) ? node.left.size : 0;
            if (k <= leftSize) {
                return getKthSmallest(node.left, k);
            } else if (k > leftSize + node.count) {
                return getKthSmallest(node.right, k - leftSize - node.count);
            } else {
                return node.key;
            }
        }

        int countLessThan(AVLNode node, int key) {
            if (node == null) {
                return 0;
            }
            if (key <= node.key) {
                return countLessThan(node.left, key);
            } else {
                int leftSize = (node.left != null) ? node.left.size : 0;
                return leftSize + node.count + countLessThan(node.right, key);
            }
        }

        int countGreaterThan(AVLNode node, int key) {
            if (node == null) {
                return 0;
            }
            if (key >= node.key) {
                return countGreaterThan(node.right, key);
            } else {
                int rightSize = (node.right != null) ? node.right.size : 0;
                return rightSize + node.count + countGreaterThan(node.left, key);
            }
        }

        int countUnique(AVLNode node) {
            if (node == null) {
                return 0;
            }
            return 1 + countUnique(node.left) + countUnique(node.right);
        }

        AVLNode getMin(AVLNode node) {
            while (node.left != null) {
                node = node.left;
            }
            return node;
        }

        AVLNode deleteMin(AVLNode node) {
            if (node.left == null) {
                return node.right;
            }
            node.left = deleteMin(node.left);
            update(node);
            return balance(node);
        }

        void update(AVLNode node) {
            node.height = 1 + Math.max(height(node.left), height(node.right));
            node.size = node.count + size(node.left) + size(node.right);
        }

        int height(AVLNode node) {
            return node != null ? node.height : 0;
        }

        int size(AVLNode node) {
            return node != null ? node.size : 0;
        }

        int balanceFactor(AVLNode node) {
            return height(node.left) - height(node.right);
        }

        AVLNode balance(AVLNode node) {
            int balanceFactor = balanceFactor(node);
            if (balanceFactor > 1) {
                if (balanceFactor(node.left) < 0) {
                    node.left = rotateLeft(node.left);
                }
                node = rotateRight(node);
            } else if (balanceFactor < -1) {
                if (balanceFactor(node.right) > 0) {
                    node.right = rotateRight(node.right);
                }
                node = rotateLeft(node);
            }
            return node;
        }

        AVLNode rotateRight(AVLNode y) {
            AVLNode x = y.left;
            y.left = x.right;
            x.right = y;
            update(y);
            update(x);
            return x;
        }

        AVLNode rotateLeft(AVLNode y) {
            AVLNode x = y.right;
            y.right = x.left;
            x.left = y;
            update(y);
            update(x);
            return x;
        }
    }

    static class AVLNode {
        int key;
        int count;
        int height;
        int size;
        AVLNode left;
        AVLNode right;

        public AVLNode(int key) {
            this.key = key;
            this.count = 1;
            this.height = 1;
            this.size = 1;
        }
    }

    // taken from https://rosettacode.org/wiki/Visualize_a_tree#Java
    // method buat debug, agar bisa memvisualisasikan kondisi tim & participants
    // this method does not do anything for the program, only for debugging
    static void visualizeTeams() {
        out.println("----------------------------------------");
        Team team = headTeam;
        if (team != null) {
            do {
                out.print("Team " + team.id + " [Total Points: " + team.totalPoints + "]");
                if (team == sofitaTeam) {
                    out.print(" <- Sofita");
                }
                if (team == jokiTeam) {
                    out.print(" <- Penjoki");
                }
                out.println();
                for (Participant p : team.participants) {
                    out.println("  Participant ID: " + p.id + ", Points: " + p.points);
                }
                team = team.next;
            } while (team != headTeam);
        }
        out.println("----------------------------------------");
    }

    // taken from https://codeforces.com/submissions/Petr
    // together with PrintWriter, these input-output (IO) is much faster than the
    // usual Scanner(System.in) and System.out
    // please use these classes to avoid your fast algorithm gets Time Limit
    // Exceeded caused by slow input-output (IO)
    static class InputReader {
        public BufferedReader reader;
        public StringTokenizer tokenizer;

        public InputReader(InputStream stream) {
            reader = new BufferedReader(new InputStreamReader(stream), 1 << 20);
            tokenizer = null;
        }

        public String next() {
            while (tokenizer == null || !tokenizer.hasMoreTokens()) {
                try {
                    String s = reader.readLine();
                    if (s == null) {
                        return null;
                    }
                    tokenizer = new StringTokenizer(s);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return tokenizer.nextToken();
        }

        public int nextInteger() {
            return Integer.parseInt(next());
        }

        public long nextLong() {
            return Long.parseLong(next());
        }
    }
}
