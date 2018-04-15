#include <stdio.h>
#include <stdlib.h>

int approx_sqrt(int n) {
    //the approx_square root of a number is less than or equal to half the number
    //unless the number is less than 4
    //sqrt(n) where n < 4 = 1
    //sqrt(4) = 2
    //sqrt(5) = 2
    //This is the logic, first divide the number by 2, e.g the square root of 25 is 5, half of 25 is 12.5
    //So the square root of a number will always be less than the number/2
    //Now we iterate from 2 to (n/2), until we find a point where the multiplication of our iterator (i) by itself is either 
    //equal to n or greater than n. if it's greater than n, then the square root is (i - 1)
    if(n < 4) {
        return 1;
    } else {
        //In C, 5/2 will give 2, what we want is 3. Hence we add 1 to the result of n/2 to cater for cases where n is odd.
        int max = (n/2) + 1;
        int i = 2;
        while(i <= max) {
            int sq = i * i;
            if(sq == n) {
                return i;
            } else if(sq > n) {
                return i - 1; 
            } else {
                i = i + 1;
            }
        }
    }

    //the code should never get here, if it did, something is terribly wrong.
    //return -1;
}

int main( int argc, char *argv[] )  {
    //Get the number from command line arguements.
    int n = atoi(argv[1]);
    int sqrt = approx_sqrt(n);
    printf("The approx square root of %d is %d\n", n,sqrt);
}
