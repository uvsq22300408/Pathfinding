#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <stdbool.h>

typedef struct {
    int x, y;
} Point;

typedef struct {
    int lignes, colonnes;
    int indexDepart, indexArrivee;
    int* grille;
} DijkstraAlgorithm;

int getIndex(DijkstraAlgorithm* algo, Point p) {
    return (p.x * algo->colonnes) + p.y;
}

Point getPoint(DijkstraAlgorithm* algo, int index) {
    Point p = {index / algo->colonnes, index % algo->colonnes};
    return p;
}

double getDist(DijkstraAlgorithm* algo, int i1, int i2) {
    Point p1 = getPoint(algo, i1);
    Point p2 = getPoint(algo, i2);
    return sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y));
}

int* getIndexVoisins(DijkstraAlgorithm* algo, int sommet, int* nbVoisins) {
    static int voisins[8];
    *nbVoisins = 0;
    Point coord = getPoint(algo, sommet);

    for (int i = -1; i <= 1; i++) {
        for (int j = -1; j <= 1; j++) {
            if (i == 0 && j == 0) continue;

            int xVoisin = coord.x + i;
            int yVoisin = coord.y + j;

            if (xVoisin >= 0 && xVoisin < algo->lignes && yVoisin >= 0 && yVoisin < algo->colonnes) {
                int indexVoisin = getIndex(algo, (Point){xVoisin, yVoisin});
                
                if (algo->grille[indexVoisin] != 3) { // Vérifie que ce n'est pas un obstacle
                    voisins[(*nbVoisins)++] = indexVoisin;
                }
            }
        }
    }
    return voisins;
}

int minDistIndex(double* d, bool* P, int taille) {
    int indexMin = -1;
    double distMin = 1e9;

    for (int i = 0; i < taille; i++) {
        if (!P[i] && d[i] < distMin) {
            distMin = d[i];
            indexMin = i;
        }
    }
    return indexMin;
}

Point* cheminPoints(DijkstraAlgorithm* algo, int* pred, int* tailleChemin) {
    int indexCourant = algo->indexArrivee;
    *tailleChemin = 0;

    Point* chemin = malloc(100 * sizeof(Point));
    if (!chemin) {
        fprintf(stderr, "Erreur d'allocation mémoire\n");
        exit(1);
    }

    while (indexCourant != algo->indexDepart) {
        if (indexCourant == algo->indexArrivee) {
            indexCourant = pred[indexCourant]; // Ignore le point d'arrivée
            continue;
        }
        chemin[(*tailleChemin)++] = getPoint(algo, indexCourant);
        indexCourant = pred[indexCourant];
    }

    return chemin;
}

Point* calculChemin(DijkstraAlgorithm* algo, int* tailleChemin) {
    bool* P = calloc(algo->lignes * algo->colonnes, sizeof(bool));
    int* pred = malloc(algo->lignes * algo->colonnes * sizeof(int));
    double* d = malloc(algo->lignes * algo->colonnes * sizeof(double));

    if (!P || !pred || !d) {
        fprintf(stderr, "Erreur d'allocation mémoire\n");
        exit(1);
    }

    for (int i = 0; i < algo->lignes * algo->colonnes; i++) {
        d[i] = 1e9;
        pred[i] = -1;
    }

    d[algo->indexDepart] = 0.0;

    while (!P[algo->indexArrivee]) {
        int a = minDistIndex(d, P, algo->lignes * algo->colonnes);
        if (a == -1) {
            free(P); free(pred); free(d);
            return NULL; // Pas de chemin trouvé
        }

        P[a] = true;
        int nbVoisins;
        int* voisinsA = getIndexVoisins(algo, a, &nbVoisins);

        for (int i = 0; i < nbVoisins; i++) {
            int v = voisinsA[i];

            if (algo->grille[v] == 3) continue;

            double distParA = d[a] + getDist(algo, a, v);
            if (d[v] > distParA) {
                d[v] = distParA;
                pred[v] = a;
            }
        }
    }

    Point* chemin = cheminPoints(algo, pred, tailleChemin);

    free(P); free(pred); free(d);
    return chemin;
}

int main(int argc, char* argv[]) {
    if (argc < 7) {
        printf("Usage: %s <lignes> <colonnes> <x_depart> <y_depart> <x_arrivee> <y_arrivee> [<x_obstacle> <y_obstacle> ...]\n", argv[0]);
        return 1;
    }

    DijkstraAlgorithm algo;
    algo.lignes = atoi(argv[1]);
    algo.colonnes = atoi(argv[2]);
    algo.grille = calloc(algo.lignes * algo.colonnes, sizeof(int));

    if (!algo.grille) {
        fprintf(stderr, "Erreur d'allocation mémoire\n");
        return 1;
    }

    Point depart = {atoi(argv[3]), atoi(argv[4])};
    Point arrivee = {atoi(argv[5]), atoi(argv[6])};

    algo.indexDepart = getIndex(&algo, depart);
    algo.indexArrivee = getIndex(&algo, arrivee);

    if (argc > 7) {
        for (int i = 7; i < argc; i += 2) {
            int x = atoi(argv[i]);
            int y = atoi(argv[i + 1]);
            algo.grille[getIndex(&algo, (Point){x, y})] = 3;
        }
    }

    int tailleChemin;
    Point* chemin = calculChemin(&algo, &tailleChemin);

    if (!chemin) {
        printf("Aucun chemin trouvé.\n");
    } else {
        for (int i = tailleChemin - 1; i >= 0; i--) {
            printf("%d,%d\n", chemin[i].x, chemin[i].y);
        }
        free(chemin);
    }

    free(algo.grille);
    return 0;
}
