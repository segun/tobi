import time
import math
import sys

def binomial_dyna(n, k):
    #print "n == [%d], k == [%d]" % (n, k)
    if k == 0 or k == n:
        #print "found solution for n == [%d], k == [%d]" % (n, k)
        return 1
    else:
        solution1 = binomial_dyna(n - 1, k)
        solution2 = binomial_dyna(n - 1, k - 1)
        #print "found solution for n == [%d], k == [%d]" % (n, k)
        return solution1 + solution2

solutions = {}
def binomial_memo(n, k):
    global solutions
    #print "n == [%d], k == [%d]" % (n, k)
    if (n, k) in solutions:
        #print "found solution for n == [%d], k == [%d] in memory" % (n, k)
        return solutions[(n, k)]
    elif k == 0 or k == n:
        #print "found solution for n == [%d], k == [%d]" % (n, k)
        solutions[(n, k)] = 1
        return 1
    else:
        solution1 = binomial_memo(n - 1, k)
        solution2 = binomial_memo(n - 1, k - 1)
        solution = solution1 + solution2
        #print "found solution for n == [%d], k == [%d]" % (n, k)
        solutions[(n,k)] = solution
        return solution

if __name__ == '__main__':
    n = int(sys.argv[1])
    k = int(sys.argv[2])

    start_time = int(math.floor(time.time() * 1000))
    solution = binomial_memo(n, k)
    end_time = int(math.floor(time.time() * 1000))
    print "Binomial of n == [%d], k == [%d] using Memoization is === %d" % (n, k, solution)
    print "Total time taken %d milliseconds" % (end_time - start_time)

    start_time = int(math.floor(time.time() * 1000))
    solution = binomial_dyna(n, k)
    end_time = int(math.floor(time.time() * 1000))
    print "Binomial of n == [%d], k == [%d] using Dynamic Programming is === %d" % (n, k, solution)
    print "Total time taken %d milliseconds" % (end_time - start_time)
