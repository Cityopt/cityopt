package eu.cityopt.sim.eval;

public abstract class TabularPiecewiseFunction extends PiecewiseFunction {
    final double[] tt;
    final double[] vv;
    final int degree;

    public TabularPiecewiseFunction(double[] tt, double[] vv, int degree) {
        this.tt = tt;
        this.vv = vv;
        this.degree = degree;
    }

    @Override
    public double[] getTimes() {return tt;}

    @Override
    public double[] getValues() {return vv;}

    @Override
    public int getDegree() {
        return degree;
    }
}
