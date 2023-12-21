package software.gunter.naturesniche.utils;

public class SuperMath {
    /**
     * Calculates the value of the function f(x) = a + b * e^(-c * x).
     *
     * @param x The input value x for the function.
     * @param a The asymptotic value that the function approaches as x tends to infinity.
     *          Determines the vertical offset of the function.
     * @param b The scaling factor of the exponential function. Affects the slope and
     *          the initial height of the curve at x = 0.
     * @param c The rate of growth coefficient of the exponential function. Determines how quickly
     *          the function reaches its asymptotic value.
     * @return The calculated y-value of the function.
     */
    public static double calculateAsymptoticFunctionValue(double x, double a, double b, double c) {
        return a + b * Math.exp(-c * x);
    }
}
