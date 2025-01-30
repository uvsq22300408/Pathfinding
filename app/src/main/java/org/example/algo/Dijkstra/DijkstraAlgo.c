#include <stdio.h>
#include <stdlib.h>

int main(int argc, char *argv[]) {
    if (argc < 5) {
        fprintf(stderr, "Usage: %s startX startY endX endY\n", argv[0]);
        return 1;
    }

    int startX = atoi(argv[1]);
    int startY = atoi(argv[2]);
    int endX = atoi(argv[3]);
    int endY = atoi(argv[4]);

    // Remplacer par Dijkstra
    printf("%d,%d %d,%d\n", startX, startY, endX, endY);
    
    return 0;
}
